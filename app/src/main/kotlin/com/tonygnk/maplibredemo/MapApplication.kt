package com.tonygnk.maplibredemo

import android.app.Application
import com.tonygnk.maplibredemo.data.AppContainer
import com.tonygnk.maplibredemo.data.AppDataContainer

class MapApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}