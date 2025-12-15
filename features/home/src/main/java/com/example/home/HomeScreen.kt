package com.example.home

import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.components.QuickTaskCard
import com.example.data.ScreenRoutes
import com.example.data.TaskHolder
import com.example.data.TaskInfo
import com.example.data.TaskStates
import com.example.data.getTasks
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

    fun updateTasks(){
        pendingTasks = getTasks(prefs, listOf(TaskStates.PENDING, TaskStates.IN_PROGRESS))
        Log.d("ComposeLog", pendingTasks.toString())
        finishedTasks = getTasks(prefs,listOf(TaskStates.DONE))
        blockedTasks = getTasks(prefs,listOf(TaskStates.BLOCKED))
    }

    updateTasks()

    Column(modifier.padding(10.dp)) {

        Text(text = "Pendientes")
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if(pendingTasks.list.count() >= 1) {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        itemsIndexed(items = pendingTasks.list.reversed(), key = { index, item -> item.taskId }) { index, task ->
                            QuickTaskCard(
                                taskInfo = task,
                                onView = {onTaskView(task, ScreenRoutes.HOME)}
                            )

                            if(index != pendingTasks.list.count() -1)
                                Spacer(Modifier.height(10.dp))
                        }
                    }
                } else{
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                        ,
                        text = "No hay tareas pendientes",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(text = "Terminadas")
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if(finishedTasks.list.count() >= 1) {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        itemsIndexed(items = finishedTasks.list.reversed(), key = { index, item -> item.taskId }) { index, task ->
                            QuickTaskCard(
                                taskInfo = task,
                                onView = {onTaskView(task, ScreenRoutes.HOME)}
                            )

                            if(index != finishedTasks.list.count() -1)
                                Spacer(Modifier.height(10.dp))
                        }
                    }
                } else{
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                        ,
                        text = "No hay tareas terminadas",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(text = "Bloqueadas")
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if(blockedTasks.list.count() >= 1) {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        itemsIndexed(items = blockedTasks.list.reversed(), key = { index, item -> item.taskId }) { index, task ->
                            QuickTaskCard(
                                taskInfo = task,
                                onView = {onTaskView(task, ScreenRoutes.HOME)}
                            )

                            if(index != blockedTasks.list.count() -1)
                                Spacer(Modifier.height(10.dp))
                        }
                    }
                } else{
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                        ,
                        text = "No hay tareas bloqueadas",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}