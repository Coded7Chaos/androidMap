package com.tonygnk.maplibredemo.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface GpsApiService
{
    @GET("service/proximaparada.php")
    suspend fun getProximaParada(): Response<ResponseBody>

    @GET("service/recoruta.php")
    suspend fun getRecoruta(): Response<ResponseBody>
}