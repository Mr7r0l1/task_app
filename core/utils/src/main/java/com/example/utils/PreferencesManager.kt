package com.example.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val TASK_DATA = "tasks_data"
        private const val DARK_THEME = "dark_theme_enabled"
        private const val HOUR_FORMAT = "24_hour_format"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveTaskData(data: String){
        prefs.edit{
            putString(TASK_DATA, data)
        }
    }

    fun getTaskData(): String{
        val taskData = prefs.getString(TASK_DATA, "")

        return taskData ?: ""
    }

    fun saveTheme(state: Boolean){
        prefs.edit(){
            putBoolean(DARK_THEME,state)
        }
    }
    fun getTheme(): Boolean{
        val themeData = prefs.getBoolean(DARK_THEME, true)

        return themeData
    }

    fun save24HourFormat(state: Boolean){
        prefs.edit{
            putBoolean(HOUR_FORMAT,state)
        }
    }

    fun get24HourFormat(): Boolean{
        val hourFormat = prefs.getBoolean(HOUR_FORMAT, true)

        return  hourFormat
    }
}