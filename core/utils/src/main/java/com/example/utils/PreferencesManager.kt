package com.example.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val TASK_DATA = "tasks_data"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun SaveTaskData(data: String){
        prefs.edit{
            putString(TASK_DATA, data)
        }
    }

    fun GetTaskData(): String{
        val taskData = prefs.getString(TASK_DATA, "")

        return taskData ?: ""
    }
}