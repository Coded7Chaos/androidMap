package com.tonygnk.maplibredemo.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface SimonApiService
{
    @GET("Lista_Rutas.php")
    suspend fun getRutas(): Response<ResponseBody>

    @GET("Lista_App_Avisos_Paradas.php")
    suspend fun getAvisosParadas(): Response<ResponseBody>

    @GET("ListaParadasAppLPB2.php")
    suspend fun getParadas(): Response<ResponseBody>
}