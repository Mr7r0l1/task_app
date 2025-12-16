package com.example.model// In :core:utils/ReminderBroadcastReceiver.kt

import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import com.example.utils.NotificationHelper
import com.example.utils.PreferencesManager

class ReminderBroadcastReceiver : BroadcastReceiver() {

    private val MAIN_ACTIVITY_CLASS_NAME = "com.example.task_app.MainActivity"
    private val APPLICATION_PACKAGE_NAME = "com.example.task_app"

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("TASK_ID") ?: return
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Reminder"
        val taskMessage = intent.getStringExtra("TASK_MESSAGE") ?: "It's time for your task."
        val isOneTime = intent.getBooleanExtra("IS_ONE_TIME", false)

        val prefsManager = PreferencesManager(context)
        val scheduler = AlarmScheduler(context, prefsManager)
        val task = getTaskById(prefsManager, taskId)

        val contentPendingIntent = createActivityPendingIntent(context, taskId)

        NotificationHelper.showNotification(
            context = context,
            title = taskTitle,
            message = taskMessage,
            taskId = taskId,
            contentPendingIntent = contentPendingIntent
        )

        if (task != null) {
            if (isOneTime) {
                scheduler.cancelReminder(task)
                val updatedTask = task
                updatedTask.reminderTimeMillis = null
                updateTask(prefsManager, task.taskId,updatedTask,scheduler)
                println("One-time reminder completed and removed.")
            } else {
                scheduler.scheduleReminder(task)
            }
        }
    }

    // Helper function for the click intent
    private fun createActivityPendingIntent(context: Context, taskId: String): PendingIntent {
        val activityIntent = Intent().apply {

            // Use ComponentName to reference the Activity by string name
            // to avoid illegal upward dependency.
            component = ComponentName(
                APPLICATION_PACKAGE_NAME,
                MAIN_ACTIVITY_CLASS_NAME
            )

            putExtra("TASK_ID_TO_VIEW", taskId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Use a unique request code based on taskId
        return PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}