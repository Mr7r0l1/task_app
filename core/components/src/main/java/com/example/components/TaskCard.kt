package com.example.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.TaskInfo

@Composable
fun TaskCard(
    taskInfo: TaskInfo,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(10.dp)
) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(innerPadding)) {
            Row(
                modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(taskInfo.taskTitle)
                Box(modifier = Modifier
                    .background(taskInfo.taskStatus.displayColor, shape = RoundedCornerShape(25))
                ) {
                    Text(modifier = Modifier.padding(5.dp), text = taskInfo.taskStatus.GetDisplayName())
                }
            }
        }
    }
}