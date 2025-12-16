package com.example.task_app

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.ScreenRoutes
import com.example.model.TaskInfo
import com.example.navigation.PagerApp
import com.example.task_app.ui.theme.Task_appTheme
import com.example.taskviewer.TaskViewer
import com.example.model.AlarmScheduler
import com.example.model.getTaskById
import com.example.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json

@Composable
fun NotificationPermissionRequester() {
    // Only proceed if the device is running Android 13 (API 33) or higher
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU is API 33

        val context = LocalContext.current

        // Define the launcher for the permission request dialog
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                if (isGranted) {
                    println("Notification permission granted.")
                } else {
                    println("Notification permission denied.")
                    // Optional: Show a message to the user explaining why it's needed
                }
            }
        )

        // Use LaunchedEffect to run the check when the Composable first enters the composition
        LaunchedEffect(Unit) {
            val permissionStatus = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)

            if (permissionStatus != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Launch the system permission dialog
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    // If API < 33, notifications are allowed by default, so no action is needed here.
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var taskToViewId by mutableStateOf<String?>("")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        val preferencesManager = PreferencesManager(this)
        val scheduler = AlarmScheduler(this,preferencesManager)
        var startRoute = 0
        setContent {
            val navController = rememberNavController()
            var darkTheme by remember { mutableStateOf(preferencesManager.getTheme()) }
            NotificationPermissionRequester()
            val localTaskId = taskToViewId
            var taskToView: TaskInfo? = null
            if(localTaskId != null)
                taskToView = getTaskById(preferencesManager,localTaskId)

            val startDest = if(taskToView == null) "pager" else "task_viewer/${ScreenRoutes.HOME.ordinal}/${Json.encodeToString(taskToViewId)}"
            Task_appTheme(darkTheme) {
                NavHost(navController = navController, startDestination = startDest) {
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
                                }, scheduler = scheduler
                            )
                        }
                        else{
                            navController.navigate("pager"){
                                popUpTo("task_viewer/{prevPage}/{task}"){
                                    inclusive = true
                                }
                            }
                        }

                    }
                }
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        // Read the extra using the key from ReminderBroadcastReceiver
        taskToViewId = intent.getStringExtra("TASK_ID_TO_VIEW")
    }
}

