package com.tonygnk.maplibredemo.data

import android.content.Context
import com.tonygnk.maplibredemo.repository.OfflineParadasRepository
import com.tonygnk.maplibredemo.repository.OfflineRutaRepository
import com.tonygnk.maplibredemo.repository.OfflineUserRepository
import com.tonygnk.maplibredemo.repository.ParadaRepository
import com.tonygnk.maplibredemo.repository.RutaRepository
import com.tonygnk.maplibredemo.repository.UserRepository

interface AppContainer {
    val paradaRepository: ParadaRepository
    val userRepository: UserRepository
    val rutaRepository: RutaRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val userRepository: UserRepository by lazy{
        OfflineUserRepository(TransporteDatabase.getDatabase(context).userDao())
    }

    override val paradaRepository: ParadaRepository by lazy {
        OfflineParadasRepository(TransporteDatabase.getDatabase(context).paradaDao())
    }

    override val rutaRepository: RutaRepository by lazy {
        OfflineRutaRepository(TransporteDatabase.getDatabase(context).rutaDao())
    }
}