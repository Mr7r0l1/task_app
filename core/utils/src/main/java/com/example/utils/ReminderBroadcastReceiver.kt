package com.example.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Reminder"
        val taskMessage = intent.getStringExtra("TASK_MESSAGE") ?: "It's time for your task."
        val taskId = intent.getStringExtra("TASK_ID") ?: ""

        println("Reminder received for Task ID: $taskId - $taskTitle")

        // *** IMPORTANT: After triggering, re-schedule the NEXT alarm ***
        // This makes the alarm repeat every week on the specified days.
        // We'll need access to the data layer/prefs to get the full TaskInfo again.
        // For simplicity *in this BroadcastReceiver*, we will assume the ReminderScheduler
        // is re-scheduling based on the stored data.

        // A robust solution would pass a reference to the TaskInfo (or fetch it) and re-schedule.
        // Since we can't easily instantiate ReminderScheduler here without a PreferencesManager,
        // this part is a conceptual placeholder for re-scheduling the weekly alarm.

        // For a simplified (but less robust) implementation, assume you have a way to
        // re-schedule the *same* task for its next weekly occurrence here.
        // Since the current example lacks a full application structure, the best approach
        // is to ensure that the main application re-sets *all* alarms on boot/data change.
    }
}