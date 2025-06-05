package com.tonygnk.maplibredemo.api.notifications


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.tonygnk.maplibredemo.repository.ParadaRepository
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

object PollingManager {

    private const val TAG = "PollingManager"
    private const val POLLING_INTERVAL_MS = 5000L // 5 segundos

    // Set threadsafe de IDs ya procesados.
    // ConcurrentHashMap usará la clave (idParada) con valor `true` si ya se ejecutó.
    private val processedIds = ConcurrentHashMap.newKeySet<Int>()

    private var pollingJob: Job? = null


    fun startPolling(
        context: Context,
        paradaRepository: ParadaRepository,
        notificationRepository: NotificationRepository
    ) {
        if (pollingJob?.isActive == true) {
            Log.w(TAG, "PollingManager ya está en ejecución. No se inicia de nuevo.")
            return
        }

        Log.d(TAG, "Iniciando PollingManager")

        pollingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                // 1) Verificar conexión a internet
                val hayInternet = isNetworkAvailable(context)
                if (!hayInternet) {
                    Log.d(TAG, "Sin conexión: se esperará $POLLING_INTERVAL_MS ms antes de reintentar.")
                    delay(POLLING_INTERVAL_MS)
                    continue
                }

                Log.d(TAG, "Hay conexión → obteniendo notificaciones")

                // 2) Llamar al repositorio para obtener lista de IDs
                val idsRecibidos: List<Int> = try {
                    notificationRepository.fetchNotifications()
                } catch (e: Exception) {
                    Log.e(TAG, "Error al obtener notificaciones: ${e.localizedMessage}", e)
                    emptyList()
                }

                if (idsRecibidos.isEmpty()) {
                    Log.d(TAG, "Respuesta vacía: No hay notificaciones")
                    if (processedIds.isNotEmpty()) {
                        Log.d(TAG, "Hay paradas previamente deshabilitadas: ${processedIds.toList()}. Las reactivaremos.")
                        processedIds.forEach { idParada ->
                            paradaRepository.setParadaActiva(idParada)
                        }
                        processedIds.clear()
                        Log.d(TAG, "processedIds limpiado tras reactivar todas.")
                    } else {
                        Log.d(TAG, "No hay paradas previamente deshabilitadas. Nada que reactivar.")
                    }
                } else {
                    Log.d(TAG, "IDs recibidos: $idsRecibidos")
                    // 3) Filtrar solo los IDs que no estén en processedIds
                    val nuevosIds = idsRecibidos.filter { id -> !processedIds.contains(id) }
                    if (nuevosIds.isEmpty()) {
                        Log.d(TAG, "No hay IDs nuevos que procesar (todos ya vistos).")
                    } else {
                        Log.d(TAG, "IDs NUEVOS a procesar: $nuevosIds")
                        // 4) Para cada ID nuevo, marcar la parada como inactiva
                        nuevosIds.forEach { idParada ->
                            try {
                                paradaRepository.setParadaInactiva(idParada)
                                Log.d(TAG, "Parada $idParada inhabilitada correctamente.")
                                // Marcar como procesado para no repetir
                                processedIds.add(idParada)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error al inhabilitar parada $idParada: ${e.localizedMessage}", e)
                            }
                        }
                    }
                }

                // 5) Esperar 5 segundos antes de la próxima iteración
                delay(POLLING_INTERVAL_MS)
            }
            Log.d(TAG, "PollingManager corutina ha terminado.")
        }
    }

    fun stopPolling() {
        Log.d(TAG, "Stopping PollingManager")
        pollingJob?.cancel()
        pollingJob = null
        processedIds.clear()
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val net = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(net) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            Log.e(TAG, "Error comprobando conectividad: ${e.localizedMessage}", e)
            false
        }
    }

}