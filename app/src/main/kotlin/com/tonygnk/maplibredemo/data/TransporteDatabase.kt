package com.tonygnk.maplibredemo.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tonygnk.maplibredemo.models.Coordenada
import com.tonygnk.maplibredemo.models.Dia
import com.tonygnk.maplibredemo.models.DiaHorario
import com.tonygnk.maplibredemo.models.Horario
import com.tonygnk.maplibredemo.models.Parada
import com.tonygnk.maplibredemo.models.ParadaRuta
import com.tonygnk.maplibredemo.models.Ruta
import com.tonygnk.maplibredemo.models.RutaHorario
import com.tonygnk.maplibredemo.models.User

@Database(entities = [Parada::class, Ruta::class, User::class, Coordenada::class, Dia::class, DiaHorario::class, Horario::class, ParadaRuta::class, RutaHorario::class], version = 5, exportSchema = false)
abstract class TransporteDatabase : RoomDatabase() {
    abstract fun paradaDao(): ParadaDao
    abstract fun userDao(): UserDao
    abstract fun rutaDao(): RutaDao

    companion object {
        @Volatile
        private var Instance: TransporteDatabase? = null
        fun getDatabase(context: Context) : TransporteDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TransporteDatabase::class.java, "transporte_database.db")
                    .createFromAsset("database/transporte_app.db")
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}