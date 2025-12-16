package com.example.taskviewer

import ReminderPopup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskInfo
import com.example.model.TaskStates
import com.example.model.removeTask
import com.example.model.updateTask
import com.example.design.DeleteButtonColor
import com.example.design.DoneColor
import com.example.design.DynamicText
import com.example.design.isLight
import com.example.model.AlarmScheduler
import com.example.utils.PreferencesManager
import com.example.utils.formatTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskViewer(
    taskInfo: TaskInfo, onClose: () -> Unit, prefsManager: PreferencesManager, scheduler: AlarmScheduler
) {
    var task by remember { mutableStateOf(taskInfo) }
    var expanded by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf(taskInfo.taskStatus) }
    var message by remember {mutableStateOf(task.taskMessage)}
    var taskTitle by remember {mutableStateOf(task.taskTitle)}
    var titleError by remember {mutableStateOf(false)}
    var buttonWidth by remember { mutableIntStateOf(0) }
    val widthInDp: Dp
    LocalDensity.current.run { widthInDp = buttonWidth.toDp() }

    var reminderChecked by remember {mutableStateOf(taskInfo.reminderTimeMillis != null)}
    var popupShown by remember {mutableStateOf(false)}
    var selectedTime by remember {mutableStateOf(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))}
    taskInfo.reminderTimeMillis?.let { nonNullMillis ->
        selectedTime = LocalTime.ofNanoOfDay(nonNullMillis * 1_000_000L)
    }
    val daysOfWeek = listOf("L", "M", "M", "J", "V", "S", "D")
    val initialDays = taskInfo.reminderDays

    val selectedDays = remember { mutableStateListOf<Int>().apply { addAll(initialDays) } }

    Scaffold(topBar = {
        TopAppBar(
            title = {
            },
            navigationIcon = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    IconButton(onClick = {
                        if(!titleError) {
                            val timeToSet = if(reminderChecked) selectedTime?.get(ChronoField.MILLI_OF_DAY)?.toLong() else taskInfo.reminderTimeMillis
                            val daysToSet = if(reminderChecked) selectedDays else taskInfo.reminderDays
                            val updatedTask = TaskInfo(taskTitle,message,selectedState, reminderTimeMillis = timeToSet, reminderDays = daysToSet)
                            updateTask(prefsManager,taskInfo.taskId,updatedTask,scheduler)
                            onClose()
                        }
                    }) {
                        Icon(Icons.Outlined.Done, contentDescription = "", tint = DoneColor)
                    }

                    Text(modifier = Modifier.fillMaxWidth(0.5f).alpha(.75f),text = taskTitle, fontStyle = FontStyle.Italic, fontSize = 30.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)

                    IconButton(onClick = {
                        if(!titleError) {
                            removeTask(prefsManager,taskInfo.taskId,scheduler)
                            onClose()
                        }
                    }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "", tint = DeleteButtonColor)
                    }
                }}
            )
    }) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding).padding(20.dp)
        ) {
            Column(
                Modifier.fillMaxSize()
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Spacer(Modifier.height(10.dp))
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = taskTitle,
                            isError = titleError,
                            singleLine = true,
                            supportingText = {
                                if (titleError) Text("No se permiten titulos vacios")
                            },
                            onValueChange = { newVal ->
                                taskTitle = newVal
                                titleError = newVal.isEmpty()
                            },
                            label = { Text("Titulo") }
                        )

                        Spacer(Modifier.height(10.dp))
                        Column(modifier = Modifier.padding(5.dp).weight(1f)) {
                            TextField(
                                modifier = Modifier.fillMaxSize(),
                                value = message,
                                onValueChange = { newText ->
                                    message = newText
                                },
                                label = { Text("Mensaje") }
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Recordatorio")
                                Checkbox(
                                    checked = reminderChecked,
                                    onCheckedChange = { newVal -> reminderChecked = newVal }
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier) {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(containerColor = selectedState.displayColor),
                                        modifier = Modifier.onGloballyPositioned { coordinates ->
                                            buttonWidth = coordinates.size.width
                                        },
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
                                        modifier = Modifier.width(widthInDp),
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }) {
                                        TaskStates.entries.forEach { states ->
                                            DropdownMenuItem(text = {
                                                Text(
                                                    text = states.GetDisplayName(),
                                                    style = TextStyle(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp
                                                    )
                                                )
                                            }, onClick = {
                                                expanded = false
                                                selectedState = states
                                            })
                                        }
                                    }
                                }
                            }
                        }
                        AnimatedVisibility(reminderChecked) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                TextButton(onClick = {
                                    popupShown = true
                                }) {

                                    val textToShow = formatTime(selectedTime,prefsManager)
                                    Text(text = textToShow, fontSize = 60.sp)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    daysOfWeek.forEachIndexed { index, day ->

                                        val isChecked = selectedDays[index] == 1

                                        Button(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                            ,

                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (!isChecked)
                                                    MaterialTheme.colorScheme.surfaceVariant
                                                else
                                                    MaterialTheme.colorScheme.primary
                                            ),
                                            onClick = {
                                                selectedDays[index] = if (isChecked) 0 else 1
                                            }
                                        ) {
                                            Text(
                                                text = day,
                                                textAlign = TextAlign.Center,
                                                color = if (!isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                            AnimatedVisibility(popupShown) {
                                ReminderPopup(
                                    onDismissRequest = {popupShown = false},
                                    onTimeSelected = {time -> selectedTime = time}
                                )
                            }
                        }
                    }
                }
            }
        }

    }

}
