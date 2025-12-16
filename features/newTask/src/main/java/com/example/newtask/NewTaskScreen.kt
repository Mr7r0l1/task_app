package com.example.newtask

import ReminderPopup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskInfo
import com.example.model.TaskStates
import com.example.model.addNewTask
import com.example.model.AlarmScheduler
import com.example.utils.PreferencesManager
import com.example.utils.formatTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Locale


@Composable
fun NewTaskScreen(
    prefs: PreferencesManager,scheduler: AlarmScheduler, modifier: Modifier, onAddTask: () -> Unit
) {

    fun addTask(title: String, message: String, status: TaskStates,hour: LocalTime?, daysToRepeat: List<Int>) {
        addNewTask(prefs, TaskInfo(title, message, status, reminderTimeMillis = hour?.get(ChronoField.MILLI_OF_DAY)?.toLong(), reminderDays = daysToRepeat),scheduler)
    }


    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    var reminderChecked by remember {mutableStateOf(false)}
    var popupShown by remember {mutableStateOf(false)}
    var selectedTime by remember {mutableStateOf(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))}
    val daysOfWeek = listOf("L", "M", "M", "J", "V", "S", "D")
    val selectedDays = remember { mutableStateListOf (0,0,0,0,0,0,0) }

    Box(
        modifier
    ) {

        Column(
            Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
        ) {

            Card(
                modifier = Modifier.padding(horizontal = 20.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("Nueva tarea")
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = title,
                        isError = titleError,
                        singleLine = true,
                        supportingText = {
                            if (titleError) Text("Ingrese un titulo")
                        },
                        onValueChange = { newVal ->
                            title = newVal
                            titleError = false
                        },
                        label = { Text("Titulo") })
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                        ,
                        value = message,
                        onValueChange = { newVal -> message = newVal },
                        label = { Text("Mensaje") })


                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                        Text("Recordatorio")
                        Checkbox(
                            checked = reminderChecked,
                            onCheckedChange = { newVal -> reminderChecked = newVal }
                        )
                    }

                    AnimatedVisibility(reminderChecked) {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            TextButton(onClick = {
                                popupShown = true
                            }) {

                                val textToShow = formatTime(selectedTime,prefs)
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



                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                        if (!title.isEmpty()) {
                            val time = if(reminderChecked) selectedTime else null
                            addTask(title, message, TaskStates.PENDING,hour = time, daysToRepeat = selectedDays)
                            title = ""
                            message = ""
                            titleError = false
                            onAddTask()
                        } else titleError = true
                    }) {
                        Text("Agregar")
                    }
                }
            }
        }
    }

}
