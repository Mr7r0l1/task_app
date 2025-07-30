package com.example.home

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.components.QuickTaskCard
import com.example.data.TaskHolder
import com.example.data.TaskInfo
import com.example.data.TaskStates
import com.example.data.getTasks
import com.example.utils.PreferencesManager
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalUuidApi::class)
@Composable
fun HomeScreen(
    prefs: PreferencesManager, modifier: Modifier,onTaskView:(TaskInfo) -> Unit
) {
    var tasks by remember { mutableStateOf(TaskHolder(emptyList())) }

    val scope = rememberCoroutineScope()

    fun updateTasks(){
        tasks = getTasks(prefs, listOf(TaskStates.PENDING, TaskStates.IN_PROGRESS))
    }

    updateTasks()

    Column(modifier) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier.padding(10.dp), elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                if(tasks.list.count() >= 1) {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        itemsIndexed(items = tasks.list, key = { index, item -> item.taskId }) {index, task ->
                            QuickTaskCard(
                                taskInfo = task,
                                onView = {onTaskView(task)}
                            )

                            if(index != tasks.list.count() -1)
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
    }
}