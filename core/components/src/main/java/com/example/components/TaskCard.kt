package com.example.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.TaskInfo

@Composable
fun TaskCard(taskInfo: TaskInfo,
             modifier: Modifier = Modifier
                 .fillMaxWidth()
                 .padding(10.dp)
){
    Card(modifier = modifier) {
        Column {
            Row {
                Text(taskInfo.taskTitle)
                Text(
                    modifier = Modifier.background(taskInfo.taskStatus.displayColor),
                    text = taskInfo.taskStatus.GetDisplayName())
            }
        }
    }
}