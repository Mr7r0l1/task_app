package com.example.task_app

import android.app.Application
import com.example.utils.NotificationHelper // <-- Ensure this import is correct
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    // Add the onCreate method
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(applicationContext)
    }
}