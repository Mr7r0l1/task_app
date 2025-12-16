package com.example.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.utils.PreferencesManager

// Assuming you have a PreferencesManager and ReminderScheduler class in this package (com.example.utils)
class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Check if the received action is the system boot completion signal
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            // 1. Instantiate your PreferencesManager
            // You will need to ensure PreferencesManager is accessible and can be instantiated
            // with just the application context here.
            val prefsManager = PreferencesManager(context)

            // 2. Instantiate your ReminderScheduler
            val scheduler = AlarmScheduler(context, prefsManager)

            // 3. Re-schedule ALL existing alarms
            scheduler.scheduleAllReminders()

            println("Device boot detected. All pending task reminders have been re-scheduled.")
        }
    }
}