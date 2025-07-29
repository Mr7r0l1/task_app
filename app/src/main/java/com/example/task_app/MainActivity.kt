package com.example.task_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.data.ScreenRoutes
import com.example.navigation.NavHostApp
import com.example.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferencesManager = PreferencesManager(this)
        setContent {
            val navController = rememberNavController()
            NavHostApp(
                navController = navController,
                preferencesManager,
                ScreenRoutes.HOME
            )
        }
    }
}