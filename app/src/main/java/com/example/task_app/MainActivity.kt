package com.example.task_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.ScreenRoutes
import com.example.data.TaskInfo
import com.example.navigation.PagerApp
import com.example.task_app.ui.theme.Task_appTheme
import com.example.taskviewer.TaskViewer
import com.example.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferencesManager = PreferencesManager(this)
        setContent {
            val navController = rememberNavController()
            var darkTheme by remember { mutableStateOf(preferencesManager.GetTheme()) }
            Task_appTheme(darkTheme) {
                NavHost(navController = navController, startDestination = "pager") {
                    composable(route = "pager") {
                        PagerApp(
                            prefs = preferencesManager,
                            onChangeTheme = { state -> darkTheme = state },
                            onViewTask = { task -> navController.navigate("task_viewer/$task") },
                            startDestination = ScreenRoutes.HOME,
                        )
                    }
                    composable(route = "task_viewer/{task}") { backstackEntry ->
                        val rawInfo = backstackEntry.arguments?.getString("task")
                        if (rawInfo != null){
                            val task = Json.decodeFromString<TaskInfo>(rawInfo)
                            TaskViewer(
                                taskInfo = task,
                                prefsManager = preferencesManager,
                                onClose = {
                                    navController.navigate("pager"){
                                        popUpTo("task_viewer/{task}")
                                    }
                                }
                            )
                        }
                        else{
                            navController.navigate("pager"){
                                popUpTo("task_viewer/{task}")
                            }
                        }

                    }
                }
            }
        }
    }
}