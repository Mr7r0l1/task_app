package com.example.task_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.data.ScreenRoutes
import com.example.navigation.NavHostApp
import com.example.task_app.ui.theme.Task_appTheme
import com.example.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferencesManager = PreferencesManager(this)
        setContent {
            var darkTheme by remember { mutableStateOf(preferencesManager.GetTheme()) }
            Task_appTheme(darkTheme){
                val navController = rememberNavController()
                NavHostApp(
                    navController = navController,
                    preferencesManager,
                    onChangeTheme = {state -> darkTheme = state},
                    startDestination = ScreenRoutes.HOME,
                )
            }
        }
    }
}