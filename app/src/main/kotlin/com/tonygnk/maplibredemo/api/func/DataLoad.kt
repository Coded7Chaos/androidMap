package com.tonygnk.maplibredemo.api.func

import android.util.Log
import com.tonygnk.maplibredemo.api.data.ParadasAppRequest
import com.tonygnk.maplibredemo.api.data.ProxParadaRequest
import com.tonygnk.maplibredemo.api.data.RecorridoRequest
import com.tonygnk.maplibredemo.api.instance.ApiClient

class DataLoad
{
    suspend fun cargarDatos()
    {
        try
        {
            val recorrido = ApiClient.gpsService.getRecorrido(RecorridoRequest(ruta = 1))
            Log.d("MainActivity", "Recorrido: $recorrido")

            val rutas = ApiClient.simonService.getListaRutas()
            Log.d("MainActivity", "Rutas: $rutas")

            val avisos = ApiClient.simonService.getAvisosParadas()
            Log.d("MainActivity", "Avisos: $avisos")

            val paradasLPB = ApiClient.simonService
                .getParadasApp(ParadasAppRequest(ruta = 16, sentido = 2))
            Log.d("MainActivity", "Paradas LPB: $paradasLPB")

            val prox = ApiClient.gpsService
                .getProximaParada(ProxParadaRequest(ruta = 7, dir = 1, parada = 261))
            Log.d("MainActivity", "Próxima parada: $prox")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}