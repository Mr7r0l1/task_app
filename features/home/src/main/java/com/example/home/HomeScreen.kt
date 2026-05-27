package com.example.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.QuickTaskCard
import com.example.data.ScreenRoutes
import com.example.model.TaskHolder
import com.example.model.TaskInfo
import com.example.model.TaskStates
import com.example.model.getTasks
import com.example.utils.PreferencesManager
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalUuidApi::class)
@Composable
fun HomeScreen(
    prefs: PreferencesManager, modifier: Modifier,onTaskView:(TaskInfo, ScreenRoutes) -> Unit
) {
    var pendingTasks by remember { mutableStateOf(TaskHolder(emptyList())) }
    var finishedTasks by remember { mutableStateOf(TaskHolder(emptyList())) }
    var blockedTasks by remember { mutableStateOf(TaskHolder(emptyList())) }


    var pendingShown by remember {mutableStateOf(true)}

    var blockedShown by remember {mutableStateOf(false)}

    var finishedShown by remember {mutableStateOf(false)}

    fun updateTasks(){
        pendingTasks = getTasks(prefs, listOf(TaskStates.PENDING, TaskStates.IN_PROGRESS))
        Log.d("ComposeLog", pendingTasks.toString())
        finishedTasks = getTasks(prefs,listOf(TaskStates.DONE))
        blockedTasks = getTasks(prefs,listOf(TaskStates.BLOCKED))
    }

    updateTasks()


    Column(modifier.padding(10.dp)) {

        TextButton(modifier = Modifier.fillMaxWidth(), onClick = { pendingShown = !pendingShown }) {Text(modifier = Modifier.fillMaxWidth(),text = stringResource(
            R.string.pending
        ), textAlign = TextAlign.Start)}
        AnimatedVisibility(pendingShown) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (pendingTasks.list.count() >= 1) {
                        LazyColumn(
                            contentPadding = PaddingValues(20.dp)
                        ) {
                            itemsIndexed(
                                items = pendingTasks.list.reversed(),
                                key = { index, item -> item.taskId }) { index, task ->
                                QuickTaskCard(
                                    taskInfo = task,
                                    onView = { onTaskView(task, ScreenRoutes.HOME) },
                                    preferencesManager = prefs
                                )

                                if (index != pendingTasks.list.count() - 1)
                                    Spacer(Modifier.height(10.dp))
                            }
                        }
                    } else {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            text = stringResource(R.string.no_pending_tasks),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        TextButton(modifier = Modifier.fillMaxWidth(), onClick = { finishedShown = !finishedShown }) {Text(modifier = Modifier.fillMaxWidth(),text = stringResource(
            R.string.finished
        ), textAlign = TextAlign.Start)}
        AnimatedVisibility(finishedShown) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (finishedTasks.list.count() >= 1) {
                        LazyColumn(
                            contentPadding = PaddingValues(20.dp)
                        ) {
                            itemsIndexed(
                                items = finishedTasks.list.reversed(),
                                key = { index, item -> item.taskId }) { index, task ->
                                QuickTaskCard(
                                    taskInfo = task,
                                    onView = { onTaskView(task, ScreenRoutes.HOME) },
                                    preferencesManager = prefs
                                )

                                if (index != finishedTasks.list.count() - 1)
                                    Spacer(Modifier.height(10.dp))
                            }
                        }
                    } else {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            text = stringResource(R.string.no_finished_tasks),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        TextButton(modifier = Modifier.fillMaxWidth(), onClick = { blockedShown = !blockedShown }) {Text(modifier = Modifier.fillMaxWidth(),text = "Bloqueadas", textAlign = TextAlign.Start)}
        AnimatedVisibility(blockedShown) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (blockedTasks.list.count() >= 1) {
                        LazyColumn(
                            contentPadding = PaddingValues(20.dp)
                        ) {
                            itemsIndexed(
                                items = blockedTasks.list.reversed(),
                                key = { index, item -> item.taskId }) { index, task ->
                                QuickTaskCard(
                                    taskInfo = task,
                                    onView = { onTaskView(task, ScreenRoutes.HOME) },
                                    preferencesManager = prefs
                                )

                                if (index != blockedTasks.list.count() - 1)
                                    Spacer(Modifier.height(10.dp))
                            }
                        }
                    } else {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            text = "No hay tareas bloqueadas",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}