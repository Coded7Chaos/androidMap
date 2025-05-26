package com.tonygnk.maplibredemo

import android.app.Application
import android.util.Log
import com.tonygnk.maplibredemo.data.AppContainer
import com.tonygnk.maplibredemo.data.AppDataContainer
import com.tonygnk.maplibredemo.data.TransporteDatabase

class MapApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        //TransporteDatabase.create(this)
        val db = TransporteDatabase.getDatabase(this)
        Log.d("DB_TEST", "Database created: ${db.isOpen}")

        container = AppDataContainer(this)
    }
}