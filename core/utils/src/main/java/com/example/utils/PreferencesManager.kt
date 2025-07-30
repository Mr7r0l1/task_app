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

    fun SaveTheme(state: Boolean){
        prefs.edit(){
            putBoolean(DARK_THEME,state)
        }
    }
    fun GetTheme(): Boolean{
        val taskData = prefs.getBoolean(DARK_THEME, true)

        return taskData
    }
}