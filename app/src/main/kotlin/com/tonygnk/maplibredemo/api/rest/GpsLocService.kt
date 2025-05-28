package com.tonygnk.maplibredemo.api.rest

import com.tonygnk.maplibredemo.api.data.ProxParadaItem
import com.tonygnk.maplibredemo.api.data.ProxParadaRequest
import com.tonygnk.maplibredemo.api.data.RecorridoItem
import com.tonygnk.maplibredemo.api.data.RecorridoRequest
import retrofit2.http.*

interface GpsLocService
{
    @POST("service/recoruta.php")
    suspend fun getRecorrido(@Body body: RecorridoRequest): List<RecorridoItem>

    @POST("service/proximaparada.php")
    suspend fun getProximaParada(@Body body: ProxParadaRequest): List<ProxParadaItem>
}