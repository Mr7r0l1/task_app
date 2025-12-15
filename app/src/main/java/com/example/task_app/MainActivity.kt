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
import com.example.utils.AlarmScheduler
import com.example.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferencesManager = PreferencesManager(this)
        val scheduler = AlarmScheduler(this,preferencesManager)
        var startRoute = 0
        setContent {
            val navController = rememberNavController()
            var darkTheme by remember { mutableStateOf(preferencesManager.GetTheme()) }
            Task_appTheme(darkTheme) {
                NavHost(navController = navController, startDestination = "pager") {
                    composable(route = "pager") {
                        PagerApp(
                            prefs = preferencesManager,
                            scheduler = scheduler,
                            onChangeTheme = { state -> darkTheme = state },
                            onViewTask = { task, prevPage -> navController.navigate("task_viewer/${prevPage.ordinal}/$task") },
                            startDestination = ScreenRoutes.entries[startRoute],
                        )
                    }
                    composable(route = "task_viewer/{prevPage}/{task}") { backstackEntry ->
                        val rawTaskInfo = backstackEntry.arguments?.getString("task")
                        val rawPrevPageInfo = backstackEntry.arguments?.getString("prevPage")
                        if (rawTaskInfo != null){
                            val task = Json.decodeFromString<TaskInfo>(rawTaskInfo)
                            val prevPage = rawPrevPageInfo?.toIntOrNull()
                            if(prevPage != null){
                                startRoute = prevPage
                            }
                            TaskViewer(
                                taskInfo = task,
                                prefsManager = preferencesManager,
                                onClose = {
                                    navController.navigate("pager"){
                                        popUpTo("task_viewer/{prevPage}/{task}"){
                                            inclusive = true
                                        }
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