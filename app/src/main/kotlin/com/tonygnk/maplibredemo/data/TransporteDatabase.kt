package com.tonygnk.maplibredemo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tonygnk.maplibredemo.models.Parada

@Database(entities = [Parada::class], version = 1, exportSchema = false)
abstract class TransporteDatabase : RoomDatabase() {
    abstract fun paradaDao(): ParadaDao

    companion object {
        @Volatile
        private var Instance: TransporteDatabase? = null
        fun getDatabase(context: Context) : TransporteDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TransporteDatabase::class.java, "transporte_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}