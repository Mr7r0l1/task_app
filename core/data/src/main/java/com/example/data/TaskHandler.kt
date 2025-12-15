package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.design.BlockedColor
import com.example.design.DoneColor
import com.example.design.InProgressColor
import com.example.design.PendingColor
import com.example.utils.AlarmScheduler
import com.example.utils.PreferencesManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
data class TaskInfo @OptIn(ExperimentalUuidApi::class) constructor(
    val taskTitle: String,
    val taskMessage: String,
    val taskStatus: TaskStates,
    val taskId: String = Uuid.random().toString(),
    val reminderTimeMillis: Long? = null,
    val reminderDays: List<Int> = listOf<Int>(0,0,0,0,0,0,0)
)
@Serializable
data class TaskHolder(
    val list: List<TaskInfo>

)
fun addNewTask(prefsManager: PreferencesManager, taskToAdd: TaskInfo, scheduler: AlarmScheduler){

    val currentTasks = getTasks(prefsManager).list.toMutableList()

    currentTasks.add(taskToAdd)

    saveTasks(prefsManager,currentTasks)
    if (taskToAdd.reminderTimeMillis != null && taskToAdd.reminderDays.any { it == 1 }) {
        scheduler.scheduleReminder(taskToAdd)
    }
}

fun saveTasks(prefsManager: PreferencesManager, tasks: List<TaskInfo>){

    val serializedTasks = Json.encodeToString(TaskHolder(tasks))

    prefsManager.SaveTaskData(serializedTasks)
}

fun getTasks(prefsManager: PreferencesManager): TaskHolder{
    try {
        return Json.decodeFromString<TaskHolder>(prefsManager.GetTaskData())
    } catch (error: Exception){
        println("Error while decoding string $error")
        return TaskHolder(emptyList())
    }
}

fun getTasks(prefsManager: PreferencesManager, filters: List<TaskStates>): TaskHolder{
    try {
        val list = Json.decodeFromString<TaskHolder>(prefsManager.GetTaskData())

        val filteredList = TaskHolder( list.list.filter { task -> task.taskStatus in filters })

        return filteredList

    } catch (error: Exception){

        println("Error while decoding string $error")

        return TaskHolder(emptyList())
    }
}

fun removeTask(prefsManager: PreferencesManager, task: TaskInfo, scheduler: AlarmScheduler){
    val tasks = getTasks(prefsManager).list.toMutableList()

    tasks.remove(task)

    saveTasks(prefsManager,tasks)

    // NEW: Cancel the reminder
    if (task.reminderTimeMillis != null) {
        scheduler.cancelReminder(task)
    }
}

fun updateTask(prefsManager: PreferencesManager, oldTask: TaskInfo, newTask: TaskInfo, scheduler: AlarmScheduler){
    removeTask(prefsManager,oldTask, scheduler)

    addNewTask(prefsManager,newTask, scheduler)
}