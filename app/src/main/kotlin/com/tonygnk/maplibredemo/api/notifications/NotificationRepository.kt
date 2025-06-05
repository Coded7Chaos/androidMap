package com.tonygnk.maplibredemo.api.notifications

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NotificationRepository( private val endpointUrl: String ) {

    companion object {
        private const val TAG = "NotificationRepo"
    }
    suspend fun fetchNotifications(): List<Int> = withContext(Dispatchers.IO) {
        val resultadoIds = mutableListOf<Int>()
        try {
            Log.d(TAG, "Iniciando petición a $endpointUrl")
            val url = URL(endpointUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
            }

            val code = connection.responseCode
            if (code == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "Response Code correcto")
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    val respuestaCompleta = reader.readText()
                    Log.d(TAG, "Respuesta JSON: $respuestaCompleta")
                    // Parseamos el JSON esperando { "parada_id": [1, 2, 3, …] }
                    val jsonObj = JSONObject(respuestaCompleta)
                    if (jsonObj.has("parada_id")) {
                        val arr = jsonObj.getJSONArray("parada_id")
                        for (i in 0 until arr.length()) {
                            val idParada = arr.optInt(i, -1)
                            if (idParada != -1) {
                                resultadoIds.add(idParada)
                            }
                        }
                        Log.d(TAG, "IDs obtenidos del servidor: $resultadoIds")
                    } else {
                        Log.d(TAG, "La respuesta no contiene clave 'parada_id'")
                    }
                }
            } else {
                Log.e(TAG, "fetchNotifications: HTTP error code = $code")
            }
            connection.disconnect()
        } catch (e: Exception) {
            Log.e(TAG, "Error en fetchNotifications: ${e.localizedMessage}", e)
        }
        return@withContext resultadoIds
    }
}
