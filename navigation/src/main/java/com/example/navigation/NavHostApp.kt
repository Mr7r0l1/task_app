package com.example.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.data.ScreenRoutes
import com.example.home.HomeScreen
import com.example.newtask.NewTaskScreen
import com.example.settings.SettingsScreen
import com.example.tasks.TasksScreen
import com.example.utils.PreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavHostApp(
    navController: NavHostController,
    prefs: PreferencesManager,
    onChangeTheme: (Boolean) -> Unit,
    startDestination: ScreenRoutes = ScreenRoutes.HOME
) {
    val context = LocalContext.current

    var currentRoute by rememberSaveable { mutableStateOf(startDestination) }

    BackHandler(enabled = currentRoute != ScreenRoutes.HOME){
        if (currentRoute == ScreenRoutes.HOME) {
            (context as? Activity)?.finish()
        } else {
            navController.popBackStack(ScreenRoutes.HOME.route, false)
        }
    }

    Scaffold(bottomBar = {
        NavigationBar {
            ScreenRoutes.entries.forEachIndexed { index, routes ->
                NavigationBarItem(
                    selected = currentRoute == routes,
                    onClick = {
                        if (routes != currentRoute) {
                            navController.navigate(routes.route)
                        }
                    },
                    icon = { Icon(painter = painterResource(routes.iconId), "HomeIcon") },
                    label = { Text(routes.label) })
            }
        }
    }, topBar = {
        AnimatedContent(
            targetState = currentRoute, label = "TopBar Animation"
        ) { targetRoute ->
            when (targetRoute) {
                ScreenRoutes.HOME -> {
                    TopAppBar(
                        title = {
                            Row {
                                Text("Inicio")
                            }

                        })
                }

                ScreenRoutes.NEW_TASK -> {
                    TopAppBar(
                        title = { Text("Agregar tarea") })
                }

                ScreenRoutes.SETTINGS -> {
                    TopAppBar(
                        title = { Text("Ajustes") })
                }

                ScreenRoutes.TASKS -> {
                    TopAppBar(
                        title = { Text("Tareas") })
                }
            }
        }
    }) { innerPadding ->
        NavHost(
            navController = navController, startDestination = startDestination.route
        ) {
            composable(
                ScreenRoutes.HOME.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
            ) {
                currentRoute = ScreenRoutes.HOME
                HomeScreen(prefs, innerPadding)
            }
            composable(
                ScreenRoutes.NEW_TASK.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
            ) {
                currentRoute = ScreenRoutes.NEW_TASK
                NewTaskScreen(prefs, navController, innerPadding)
            }
            composable(
                ScreenRoutes.SETTINGS.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
            ) {
                currentRoute = ScreenRoutes.SETTINGS
                SettingsScreen(
                    prefs, innerPadding, onChangeTheme = { theme -> onChangeTheme(theme) })
            }
            composable(
                ScreenRoutes.TASKS.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
            ) {
                currentRoute = ScreenRoutes.TASKS
                TasksScreen(prefs, innerPadding)
            }
        }
    }
}