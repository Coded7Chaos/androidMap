package com.tonygnk.maplibredemo.api.rest

import com.tonygnk.maplibredemo.api.data.AvisoParada
import com.tonygnk.maplibredemo.api.data.ParadaApp
import com.tonygnk.maplibredemo.api.data.ParadasAppRequest
import com.tonygnk.maplibredemo.api.data.Ruta
import retrofit2.http.*

interface SimonAppService
{
    @GET("simonApp/Lista_Rutas.php")
    suspend fun getListaRutas(): List<Ruta>

    @GET("simonApp/Lista_App_Avisos_Paradas.php")
    suspend fun getAvisosParadas(): List<AvisoParada>

    @POST("simonApp/ListaParadasAppLPB2.php")
    suspend fun getParadasApp(@Body body: ParadasAppRequest): List<ParadaApp>
}