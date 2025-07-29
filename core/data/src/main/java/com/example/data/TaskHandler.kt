package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.design.BlockedColor
import com.example.design.DoneColor
import com.example.design.InProgressColor
import com.example.design.PendingColor
import com.example.utils.PreferencesManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

enum class TaskStates(val displayName: String, val displayColor: Color){
    PENDING(displayName = "Pendiente", displayColor = PendingColor),
    IN_PROGRESS(displayName = "En progreso", displayColor = InProgressColor),
    DONE(displayName = "Terminado", displayColor = DoneColor),
    BLOCKED(displayName = "Bloqueado", displayColor = BlockedColor);

    fun GetDisplayName(): String{
        return displayName
    }
/*
    fun GetDisplayList(): List<String>{
        val list = mutableListOf<String>()
        entries.forEach { entry ->
            list.add(entry.displayName)
        }
        return list
    }*/
}

@Serializable
data class TaskInfo(
    val taskTitle: String,
    val taskMessage: String,
    val taskStatus: TaskStates
)
@Serializable
data class TaskHolder(
    val list: List<TaskInfo>

)
fun AddNewTask(prefsManager: PreferencesManager,taskToAdd: TaskInfo){

    var currentTasks = GetTasks(prefsManager).list
        //Json.decodeFromString<TaskHolder>(prefsManager.GetTaskData())

    val newTasks = currentTasks.plus(taskToAdd)

    val serializedTasks = Json.encodeToString(TaskHolder(newTasks))

    prefsManager.SaveTaskData(serializedTasks)
}

fun GetTasks(prefsManager: PreferencesManager): TaskHolder{
    try {
        return Json.decodeFromString<TaskHolder>(prefsManager.GetTaskData())
    } catch (error: Exception){
        println("Error while decoding string $error")
        return TaskHolder(emptyList())
    }
}