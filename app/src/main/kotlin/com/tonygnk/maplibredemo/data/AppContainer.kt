package com.tonygnk.maplibredemo.data

import android.content.Context
import com.tonygnk.maplibredemo.repository.OfflineParadasRepository
import com.tonygnk.maplibredemo.repository.ParadaRepository

interface AppContainer {
    val paradaRepository: ParadaRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val paradaRepository: ParadaRepository by lazy {
        OfflineParadasRepository(TransporteDatabase.getDatabase(context).paradaDao())
    }
}