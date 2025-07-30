package com.example.taskviewer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskInfo
import com.example.data.TaskStates
import com.example.data.updateTask
import com.example.design.DynamicText
import com.example.design.darker
import com.example.design.isLight
import com.example.utils.PreferencesManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskViewer(
    taskInfo: TaskInfo, onClose: () -> Unit, prefsManager: PreferencesManager
) {
    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { onClose() }) {
                    Icon(Icons.Outlined.Close, contentDescription = "")
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(taskInfo.taskTitle)
                    }
                }
            })
    }) { innerPadding ->

        var expanded by remember { mutableStateOf(false) }
        var selectedState by remember { mutableStateOf(taskInfo.taskStatus) }

        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                Modifier.fillMaxSize()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(containerColor = selectedState.displayColor),
                        onClick = { expanded = !expanded }) {
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DynamicText(
                                backgroundColor = selectedState.displayColor,
                                text = selectedState.GetDisplayName()
                            )

                            val iconColor =
                                if (selectedState.displayColor.isLight()) Color.Black else Color.White

                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "Descripcion",
                                tint = iconColor
                            )
                        }
                    }
                    DropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        TaskStates.entries.forEach { states ->
                            DropdownMenuItem(text = {
                                Text(
                                    text = states.GetDisplayName(), style = TextStyle(
                                        color = states.displayColor.darker(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                            }, onClick = {
                                expanded = false
                                selectedState = states

                                val newTask = TaskInfo(
                                    taskInfo.taskTitle,
                                    taskInfo.taskMessage,
                                    taskInfo.taskStatus,
                                    taskInfo.taskId
                                )

                                updateTask(
                                    prefsManager = prefsManager,
                                    oldTask = taskInfo,
                                    newTask = newTask
                                )
                            })
                        }
                    }
                }
                Card(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {

                }
            }
        }

    }

}
