package com.tonygnk.maplibredemo.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty

class UserDelegate {
    private var value: String = "Valor por defecto"

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        println("Accediendo a ${property.name}")
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: String) {
        println("Actualizando ${property.name} a $newValue")
        value = newValue
    }
}


@Entity(tableName = "usuario")
data class User(
    @PrimaryKey val email: String,
    val nombre: String,
    val contrasenia: Int,
    val edad: Int
) {
    // Para propiedades observables con Compose
    var observableName by mutableStateOf(nombre)
        private set
}
