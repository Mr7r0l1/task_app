package com.example.task_app

import android.app.Application
import com.example.utils.NotificationHelper // <-- Ensure this import is correct
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    // Add the onCreate method
    override fun onCreate() {
        super.onCreate()

        // --- Place the channel creation here ---

        // This ensures the notification channel is registered
        // with the Android system as soon as the app process starts.
        NotificationHelper.createNotificationChannel(applicationContext)

        // Note: You do NOT need to call super.onCreate() again.
        // Hilt will initialize after this method completes.
    }
}