package com.tonygnk.maplibredemo.data

import android.content.Context
import com.tonygnk.maplibredemo.repository.OfflineParadaRutaRepository
import com.tonygnk.maplibredemo.repository.OfflineParadasRepository
import com.tonygnk.maplibredemo.repository.OfflineRutaRepository
import com.tonygnk.maplibredemo.repository.OfflineUserRepository
import com.tonygnk.maplibredemo.repository.ParadaRepository
import com.tonygnk.maplibredemo.repository.ParadaRutaRepository
import com.tonygnk.maplibredemo.repository.RutaRepository
import com.tonygnk.maplibredemo.repository.UserRepository

interface AppContainer {
    val paradaRepository: ParadaRepository
    val userRepository: UserRepository
    val rutaRepository: RutaRepository
    val paradaRutaRepository: ParadaRutaRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val userRepository: UserRepository by lazy{
        OfflineUserRepository(TransporteDatabase.getDatabase(context).userDao())
    }

    override val paradaRepository: ParadaRepository by lazy {
        OfflineParadasRepository(TransporteDatabase.getDatabase(context).paradaDao())
    }

    override val paradaRutaRepository: ParadaRutaRepository by lazy {
        OfflineParadaRutaRepository(TransporteDatabase.getDatabase(context).paradaRutaDao())
    }
    override val rutaRepository: RutaRepository by lazy {
        OfflineRutaRepository(TransporteDatabase.getDatabase(context).rutaDao())
    }
}