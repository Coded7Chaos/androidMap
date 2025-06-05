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
        val notiRepo = NotificationRepository("https://run.mocky.io/v3/b38525cb-0685-425e-affe-c0ca72ddc6c8")

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