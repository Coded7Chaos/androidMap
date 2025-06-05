package com.tonygnk.maplibredemo

import android.app.Application
import android.util.Log
import com.tonygnk.maplibredemo.data.AppContainer
import com.tonygnk.maplibredemo.data.AppDataContainer
import com.tonygnk.maplibredemo.data.TransporteDatabase
import com.tonygnk.maplibredemo.api.notifications.NotificationRepository
import com.tonygnk.maplibredemo.api.notifications.PollingManager

class MapApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        //TransporteDatabase.create(this)
        val db = TransporteDatabase.getDatabase(this)
        Log.d("DB_TEST", "Database created: ${db.isOpen}")
        container = AppDataContainer(this)

        val paradaRepository = container.paradaRepository
        val notiRepo = NotificationRepository("http://127.0.0.1:3000/axios/paradas")

        PollingManager.startPolling(
            context = applicationContext,
            paradaRepository = paradaRepository,
            notificationRepository = notiRepo
        )

    }

    override fun onTerminate() {
        super.onTerminate()
        PollingManager.stopPolling()
    }
}