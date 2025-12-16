package com.example.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.utils.PreferencesManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AlarmScheduler(private val context: Context, private val prefsManager: PreferencesManager) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(task: TaskInfo) {
        if (task.reminderTimeMillis == null) {
            cancelReminder(task)
            return
        }

        val nextReminderTime = calculateNextReminderTime(task)

        if (nextReminderTime == null) {
            cancelReminder(task)
            return
        }

        val pendingIntent = createPendingIntent(task)

        // 2. Set the exact alarm using the appropriate API
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

    fun scheduleAllReminders() {
        getTasks(prefsManager).list.forEach { task ->
            if (task.reminderTimeMillis != null) {
                // Schedule all tasks that have a time, regardless of whether they are one-time or weekly.
                scheduleReminder(task)
            }
        }
    }

    private fun createPendingIntent(task: TaskInfo): PendingIntent {
        val isOneTime = task.reminderDays.all { it == 0 }

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("TASK_TITLE", task.taskTitle)
            putExtra("TASK_MESSAGE", task.taskMessage)
            putExtra("TASK_ID", task.taskId)
            putExtra("IS_ONE_TIME", isOneTime)
        }

        return PendingIntent.getBroadcast(
            context,
            task.taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Calculates the millisecond timestamp for the next time this reminder should trigger.
     * Implements the logic:
     * 1. If days are set, find the next selected day.
     * 2. If no days are set (one-time):
     * a. If time is in the future today, set it for today.
     * b. If time is in the past today, set it for tomorrow.
     * @return The next trigger time in milliseconds (RTC_WAKEUP), or null if no valid time/day is found.
     */
    private fun calculateNextReminderTime(task: TaskInfo): Long? {
        val timeOfDayMillis = task.reminderTimeMillis ?: return null

        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(timeOfDayMillis)
        val hour = (totalMinutes / 60).toInt()
        val minute = (totalMinutes % 60).toInt()

        val now = Calendar.getInstance()
        val isWeekly = task.reminderDays.any { it == 1 }

        if (!isWeekly) {
            // --- ONE-TIME LOGIC ---
            val reminderCalendar = Calendar.getInstance().apply {
                // Set the hour and minute for TODAY
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If the calculated time is NOW or in the future today (e.g., set alarm at 3:48 for 3:48)
            if (reminderCalendar.timeInMillis > now.timeInMillis) {
                return reminderCalendar.timeInMillis
            } else {
                // If the time is in the past today (e.g., set alarm at 3:48 for 3:47),
                // schedule it for the next day.
                reminderCalendar.add(Calendar.DAY_OF_YEAR, 1)
                return reminderCalendar.timeInMillis
            }

        } else {
            // --- WEEKLY LOGIC (Existing) ---
            val today = now.get(Calendar.DAY_OF_WEEK) // Sunday=1, Monday=2, ..., Saturday=7
            fun dayToReminderIndex(day: Int): Int = (day - 2 + 7) % 7

            for (i in 0..6) {
                val dayIndex = dayToReminderIndex(today + i)

                if (task.reminderDays.getOrNull(dayIndex) == 1) {
                    val reminderCalendar = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, i)
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    // For the current day (i=0), skip if the time has passed.
                    if (i == 0 && reminderCalendar.timeInMillis <= now.timeInMillis) {
                        continue
                    }

                    return reminderCalendar.timeInMillis
                }
            }
            return null // Should not happen if isWeekly is true
        }
    }
}