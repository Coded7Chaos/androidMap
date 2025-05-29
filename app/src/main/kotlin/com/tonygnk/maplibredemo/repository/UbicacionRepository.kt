package com.tonygnk.maplibredemo.repository

import com.tonygnk.maplibredemo.models.Ubicacion
import kotlinx.coroutines.flow.Flow

interface UbicacionRepository {

    fun searchUbicacionByNombre(nombreUbicacion: String): Flow<List<Ubicacion>>

}