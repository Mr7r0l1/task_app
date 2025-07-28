package com.example.newtask

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.components.DropdownSelector
import com.example.data.TaskStates


@Composable
fun NewTaskScreen(
    padding: PaddingValues
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("") }
    Column(
        Modifier
            .padding(padding)
            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nueva tarea")
        TextField(
            value = title,
            onValueChange = { newVal -> title = newVal },
            label = { Text("Titulo") })
        TextField(
            value = message,
            onValueChange = { newVal -> message = newVal },
            label = { Text("Mensaje") })
    }
    DropdownSelector(
        options = TaskStates.PENDING.GetDisplayList(),
        selectedOption = selectedOption,
        onOptionSelected = { option -> selectedOption = option },
        modifier = Modifier.height(100.dp)
    )

    Button(
        onClick = {}) {
        Text("Agregar")
    }
}
