package com.example.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.components.AnimatedTaskCard
import com.example.data.TaskHolder
import com.example.data.getTasks
import com.example.data.removeTask
import com.example.utils.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalUuidApi::class)
@Composable
fun TasksScreen(
    prefs: PreferencesManager, padding: PaddingValues
) {
    var tasks by remember { mutableStateOf(TaskHolder(emptyList())) }

    val scope = rememberCoroutineScope()

    fun updateTasks(){
        tasks = getTasks(prefs)
    }

    updateTasks()

    Column(
        Modifier
            .padding(padding)
            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier.padding(10.dp), elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Column(Modifier.padding(top = 10.dp)){

                val erasedTaskIds = remember { mutableStateListOf<String>() } // use Int or UUID based on your id type

                LazyColumn(
                    contentPadding = PaddingValues(20.dp)
                ) {
                    items(items = tasks.list, key = {it.taskId}) { task ->
                        AnimatedTaskCard(
                            task = task,
                            onErase = {
                                scope.launch {
                                    erasedTaskIds.add(task.taskId)
                                    delay(300)
                                    removeTask(prefs, task)
                                    updateTasks()
                                    erasedTaskIds.remove(task.taskId)
                                }
                            },
                            isErased = erasedTaskIds.contains(task.taskId)
                        )
                    }
                }
            }
        }
    }
}