package com.example.navigation

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
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.data.ScreenRoutes
import com.example.home.HomeScreen
import com.example.newtask.NewTaskScreen
import com.example.settings.SettingsScreen
import com.example.utils.PreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavHostApp(
    navController: NavHostController,
    prefs: PreferencesManager,
    onChangeTheme: (state:Boolean) -> Unit,
    startDestination: ScreenRoutes = ScreenRoutes.HOME
) {

    fun ChangeTheme(value: Boolean){
        onChangeTheme(value)
    }

    var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }
    Scaffold(bottomBar = {
        NavigationBar {
            ScreenRoutes.entries.forEachIndexed { index, routes ->
                NavigationBarItem(
                    selected = selectedDestination == routes,
                    onClick = {
                        if(routes != selectedDestination)
                        navController.navigate(routes.route)
                    },
                    icon = { Icon(painter = painterResource(routes.iconId), "HomeIcon") },
                    label = { Text(routes.label) })
            }
        }
    }, topBar = {
        AnimatedContent(
            targetState = selectedDestination, label = "TopBar Animation"
        ) { targetRoute ->
            when (targetRoute) {
                ScreenRoutes.HOME -> {
                    TopAppBar(
                        title = {
                            Row {
                                Text("Inicio")
                            }

                        }
                    )
                }
                ScreenRoutes.NEW_TASK -> {
                    TopAppBar(
                        title = { Text("Agregar tarea") }
                    )
                }
                ScreenRoutes.SETTINGS -> {
                    TopAppBar(
                        title = { Text("Ajustes") }
                    )
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
                selectedDestination = ScreenRoutes.HOME
                HomeScreen(prefs,innerPadding)
            }
            composable(
                ScreenRoutes.NEW_TASK.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
            ) {
                selectedDestination = ScreenRoutes.NEW_TASK
                NewTaskScreen(prefs,innerPadding)
            }
            composable(
                ScreenRoutes.SETTINGS.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
            ) {
                selectedDestination = ScreenRoutes.SETTINGS
                SettingsScreen(prefs,innerPadding, onChangeTheme = { theme -> onChangeTheme(theme)})
            }
        }
    }
}