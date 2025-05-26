package com.tonygnk.maplibredemo.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val simonService: SimonApiService = Retrofit.Builder()
        .baseUrl("http://simon.lapaz.bo/simonApp/")
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(SimonApiService::class.java)

    val gpsService: GpsApiService = Retrofit.Builder()
        .baseUrl("http://gpsloc.lapaz.bo/")
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(GpsApiService::class.java)
}