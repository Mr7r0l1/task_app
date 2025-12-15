package com.example.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.data.ScreenRoutes
import com.example.home.HomeScreen
import com.example.newtask.NewTaskScreen
import com.example.settings.SettingsScreen
import com.example.tasks.TasksScreen
import com.example.utils.AlarmScheduler
import com.example.utils.PreferencesManager
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagerApp(
    prefs: PreferencesManager,
    scheduler: AlarmScheduler,
    onChangeTheme: (Boolean) -> Unit,
    onViewTask: (String, ScreenRoutes) -> Unit,
    startDestination: ScreenRoutes = ScreenRoutes.HOME
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Coroutine scope for pager animations

    // Pager state to manage swipeable pages
    val pagerState = rememberPagerState(
        initialPage = ScreenRoutes.entries.indexOf(startDestination)
    ) {
        ScreenRoutes.entries.size
    }

    val currentRoute = ScreenRoutes.entries[pagerState.currentPage]


    BackHandler(enabled = currentRoute != ScreenRoutes.HOME) {
        val homePageIndex = ScreenRoutes.entries.indexOf(ScreenRoutes.HOME)
        if (pagerState.currentPage != homePageIndex) {
            scope.launch {
                pagerState.animateScrollToPage(
                    homePageIndex,
                    animationSpec = tween(durationMillis = 600)
                )
            }
        }
    }


    Scaffold(bottomBar = {
        NavigationBar {
            ScreenRoutes.entries.forEachIndexed { index, routes ->
                NavigationBarItem(selected = currentRoute == routes, onClick = {
                    if (pagerState.currentPage != index) {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                index,
                                animationSpec = tween(durationMillis = 600, easing = EaseOutCubic)
                            ) // Directly tell the pager to animate
                        }
                    }
                }, icon = {
                    Icon(
                        painter = painterResource(routes.iconId), contentDescription = routes.label
                    )
                }, label = { Text(routes.label) })
            }
        }
    }, topBar = {
        AnimatedContent(
            targetState = currentRoute, label = "TopBar Animation", transitionSpec = {
                ContentTransform(
                    targetContentEnter = expandVertically(
                        animationSpec = tween(
                            durationMillis = 800, easing = EaseOutExpo
                        )
                    ), initialContentExit = shrinkVertically(
                        animationSpec = tween(
                            durationMillis = 800, easing = EaseOutExpo
                        )
                    )
                )
            }) { targetRoute ->
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
        HorizontalPager(
            state = pagerState, modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (ScreenRoutes.entries[page]) {
                ScreenRoutes.HOME -> HomeScreen(
                    prefs, Modifier.fillMaxSize(), onTaskView = { task, prevPage ->
                        onViewTask(Json.encodeToString(task),prevPage)
                    })

                ScreenRoutes.NEW_TASK -> NewTaskScreen(
                    prefs, scheduler, Modifier.fillMaxSize(), onAddTask = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    })

                ScreenRoutes.TASKS -> TasksScreen(
                    prefs, scheduler,Modifier.fillMaxSize(), onView = { task, prevPage ->
                        onViewTask(Json.encodeToString(task),prevPage)
                    })

                ScreenRoutes.SETTINGS -> SettingsScreen(
                    prefs,
                    Modifier.fillMaxSize(),
                    onChangeTheme = { theme -> onChangeTheme(theme) })

            }
        }
    }
}