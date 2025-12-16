package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.design.BlockedColor
import com.example.design.DoneColor
import com.example.design.InProgressColor
import com.example.design.PendingColor
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
    var taskTitle: String,
    var taskMessage: String,
    var taskStatus: TaskStates,
    var taskId: String = Uuid.random().toString(),
    var reminderTimeMillis: Long? = null,
    var reminderDays: List<Int> = listOf<Int>(0,0,0,0,0,0,0)
)
@Serializable
data class TaskHolder(
    val list: List<TaskInfo>

)
fun addNewTask(prefsManager: PreferencesManager, taskToAdd: TaskInfo, scheduler: AlarmScheduler){

    val currentTasks = getTasks(prefsManager).list.toMutableList()

    currentTasks.add(taskToAdd)

    saveTasks(prefsManager,currentTasks)
    if (taskToAdd.reminderTimeMillis != null) {
        scheduler.scheduleReminder(taskToAdd)
    }
}

fun saveTasks(prefsManager: PreferencesManager, tasks: List<TaskInfo>){

    val serializedTasks = Json.encodeToString(TaskHolder(tasks))

    prefsManager.saveTaskData(serializedTasks)
}

fun getTasks(prefsManager: PreferencesManager): TaskHolder{
    try {
        return Json.decodeFromString<TaskHolder>(prefsManager.getTaskData())
    } catch (error: Exception){
        println("Error while decoding string $error")
        return TaskHolder(emptyList())
    }
}
fun getTaskById(prefsManager: PreferencesManager, taskId: String): TaskInfo? {
    return getTasks(prefsManager).list.find { task -> task.taskId == taskId }
}
fun getTasks(prefsManager: PreferencesManager, filters: List<TaskStates>): TaskHolder{
    try {
        val list = Json.decodeFromString<TaskHolder>(prefsManager.getTaskData())

        val filteredList = TaskHolder( list.list.filter { task -> task.taskStatus in filters })

        return filteredList

    } catch (error: Exception){

        println("Error while decoding string $error")

        return TaskHolder(emptyList())
    }
}

fun removeTask(prefsManager: PreferencesManager, taskId: String, scheduler: AlarmScheduler) {
    // 1. Get the mutable list of tasks
    val tasks = getTasks(prefsManager).list.toMutableList()

    // 2. Find the taskToRemove BEFORE removing it (we need its reminder details)
    val taskToRemove = tasks.find { (_, _, _, taskID, _, _) -> taskID == taskId }

    // 3. Use removeIf to remove the task (if found)
    tasks.removeIf { (_, _, _, taskID, _, _) -> taskID == taskId }

    // 4. Save the updated list
    saveTasks(prefsManager, tasks)

    // 5. Cancel the reminder (if needed)
    if (taskToRemove?.reminderTimeMillis != null) {
        scheduler.cancelReminder(task = taskToRemove)
    }
}

fun updateTask(prefsManager: PreferencesManager, oldTask: String, newTask: TaskInfo, scheduler: AlarmScheduler){
    removeTask(prefsManager,oldTask, scheduler)

    addNewTask(prefsManager,newTask, scheduler)
}