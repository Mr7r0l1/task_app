package com.example.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.TaskInfo
import com.example.data.getTasks
import com.example.utils.ReminderBroadcastReceiver
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AlarmScheduler(private val context: Context, private val prefsManager: PreferencesManager) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        // Base request code for PendingIntent to ensure uniqueness per task
        private const val BASE_REQUEST_CODE = 1000
    }

    /**
     * Schedules the next reminder for a specific task.
     * If reminderDays is all 0s, it cancels any existing alarm for this task.
     */
    fun scheduleReminder(task: TaskInfo) {
        // Ensure the task has a reminder time and at least one day set
        if (task.reminderTimeMillis == null || task.reminderDays.all { it == 0 }) {
            cancelReminder(task)
            return
        }

        // 1. Calculate the time for the *next* occurrence
        val nextReminderTime = calculateNextReminderTime(task)
        if (nextReminderTime == null) {
            // Should not happen if days are set, but safety check
            cancelReminder(task)
            return
        }

        val pendingIntent = createPendingIntent(task)

        // 2. Set the repeating alarm
        // Use setExactAndAllowWhileIdle for more precise alarms (may be delayed in Doze mode)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextReminderTime,
            pendingIntent
        )

        // Log for debugging
        println("Alarm scheduled for Task ID: ${task.taskId} at ${Calendar.getInstance().apply { timeInMillis = nextReminderTime }.time}")
    }

    /**
     * Cancels the scheduled reminder for a specific task.
     */
    fun cancelReminder(task: TaskInfo) {
        val pendingIntent = createPendingIntent(task)
        alarmManager.cancel(pendingIntent)
        // Log for debugging
        println("Alarm cancelled for Task ID: ${task.taskId}")
    }

    /**
     * Re-schedules all tasks with reminders (e.g., after boot or update).
     */
    fun scheduleAllReminders() {
        getTasks(prefsManager).list.forEach { task ->
            if (task.reminderTimeMillis != null && task.reminderDays.any { it == 1 }) {
                scheduleReminder(task)
            }
        }
    }

    private fun createPendingIntent(task: TaskInfo): PendingIntent {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("TASK_TITLE", task.taskTitle)
            putExtra("TASK_MESSAGE", task.taskMessage)
            putExtra("TASK_ID", task.taskId)
        }

        // Use a unique request code based on the task's ID hash to ensure separate PendingIntents
        val requestCode = BASE_REQUEST_CODE + task.taskId.hashCode()

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Calculates the millisecond timestamp for the next time this reminder should trigger.
     * @return The next trigger time in milliseconds (RTC_WAKEUP), or null if no valid day is set.
     */
    private fun calculateNextReminderTime(task: TaskInfo): Long? {
        // reminderTimeMillis contains (hour * 60 + minute) * 60 * 1000
        val timeOfDayMillis = task.reminderTimeMillis ?: return null

        // Convert the total milliseconds into hour and minute components
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(timeOfDayMillis)
        val hour = (totalMinutes / 60).toInt()
        val minute = (totalMinutes % 60).toInt()

        val now = Calendar.getInstance()
        val today = now.get(Calendar.DAY_OF_WEEK) // Sunday=1, Monday=2, ..., Saturday=7

        // reminderDays is a list of size 7, where index 0 is Monday and 6 is Sunday.
        // Convert Calendar.DAY_OF_WEEK (1-7) to reminderDays index (0-6).
        // (day - 2 + 7) % 7: Monday(2) -> 0, Sunday(1) -> 6
        fun dayToReminderIndex(day: Int): Int = (day - 2 + 7) % 7

        // Find the next day index (0-6) that is set
        for (i in 0..6) {
            val dayIndex = dayToReminderIndex(today + i) // Check today, tomorrow, etc.

            if (task.reminderDays.getOrNull(dayIndex) == 1) {
                // This is the chosen day: calculate the specific time
                val reminderCalendar = Calendar.getInstance().apply {
                    // Set the day of the week based on 'today + i' offset
                    add(Calendar.DAY_OF_YEAR, i)

                    // Set the hour and minute
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // If the calculated time is in the past (only possible if i=0, checking today),
                // it means the reminder for today has already passed.
                if (i == 0 && reminderCalendar.timeInMillis <= now.timeInMillis) {
                    continue // Skip today, check the next valid day
                }

                // The reminder is set for the earliest valid day/time
                return reminderCalendar.timeInMillis
            }
        }

        return null // No valid reminder day found
    }
}