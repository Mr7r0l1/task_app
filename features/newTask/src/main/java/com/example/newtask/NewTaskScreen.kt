package com.example.newtask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskInfo
import com.example.data.TaskStates
import com.example.data.addNewTask
import com.example.design.DynamicText
import com.example.utils.PreferencesManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    prefs: PreferencesManager, modifier: Modifier, onAddTask: () -> Unit
) {

    fun addTask(title: String, message: String, status: TaskStates) {
        addNewTask(prefs, TaskInfo(title, message, status))
    }


    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf(TaskStates.PENDING) }

    Box(
        modifier
    ) {

        Column(
            Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Column(
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
                            if(titleError)
                                Text("Ingrese un titulo")
                        },
                        onValueChange = { newVal ->
                            title = newVal
                            titleError = false
                        },
                        label = { Text("Titulo") })
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = message,
                        onValueChange = { newVal -> message = newVal },
                        label = { Text("Mensaje") })
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estatus", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                        Box(modifier = Modifier) {
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = selectedState.displayColor),
                                modifier = Modifier,
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
                                    Icon(Icons.Rounded.KeyboardArrowDown, "Descripcion")
                                }
                            }
                            DropdownMenu(
                                modifier = Modifier.fillMaxWidth(),
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                TaskStates.entries.forEach { states ->
                                    DropdownMenuItem(
                                        text = { Text(states.GetDisplayName()) },
                                        onClick = {
                                            expanded = false
                                            selectedState = states
                                        })
                                }
                            }
                        }

                        Button(onClick = {
                            if (!title.isEmpty()) {
                                addTask(title, message, selectedState)
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

}
