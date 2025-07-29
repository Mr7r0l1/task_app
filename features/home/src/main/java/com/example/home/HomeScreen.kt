package com.example.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.components.TaskCard
import com.example.data.GetTasks
import com.example.data.TaskHolder
import com.example.utils.PreferencesManager


@Composable
fun HomeScreen(
    prefs: PreferencesManager,
    padding: PaddingValues
) {
    var tasks by remember { mutableStateOf(TaskHolder(emptyList())) }

    tasks = GetTasks(prefs)

    Column(Modifier
        .padding(padding)
        .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
        ) {
            items(items = tasks.list) { task ->
                TaskCard(
                    task, modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}