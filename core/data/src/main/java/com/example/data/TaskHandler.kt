package com.example.data

import com.example.utils.PreferencesManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

enum class TaskStates(val displayName: String){
    PENDING(displayName = "Pendiente"),
    IN_PROGRESS(displayName = "En progreso"),
    DONE(displayName = "Terminado"),
    BLOCKED(displayName = "Bloqueado");

    fun GetDisplayName(): String{
        return displayName
    }

    fun GetDisplayList(): List<String>{
        val list = mutableListOf<String>()
        entries.forEach { entry ->
            list.add(entry.displayName)
        }
        return list
    }
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
fun AddTask(prefsManager: PreferencesManager,taskToAdd: TaskInfo){

    val currentTasks = Json.decodeFromString<TaskHolder>(prefsManager.GetTaskData())

    val newTasks = currentTasks.list.plus(taskToAdd)

    val serializedTasks = Json.encodeToString(newTasks)

    prefsManager.SaveTaskData(serializedTasks)
}

fun GetTasks(prefsManager: PreferencesManager): TaskHolder{

    return Json.decodeFromString<TaskHolder>(prefsManager.GetTaskData())
}