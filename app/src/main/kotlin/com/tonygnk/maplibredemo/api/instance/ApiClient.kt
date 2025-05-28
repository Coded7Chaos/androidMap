package com.tonygnk.maplibredemo.api.instance

import com.tonygnk.maplibredemo.api.rest.GpsLocService
import com.tonygnk.maplibredemo.api.rest.SimonAppService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient
{
    private val gpsRetrofit = Retrofit.Builder()
        .baseUrl("http://gpsloc.lapaz.bo/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val simonRetrofit = Retrofit.Builder()
        .baseUrl("http://simon.lapaz.bo/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val gpsService: GpsLocService = gpsRetrofit.create(GpsLocService::class.java)
    val simonService: SimonAppService = simonRetrofit.create(SimonAppService::class.java)
}
