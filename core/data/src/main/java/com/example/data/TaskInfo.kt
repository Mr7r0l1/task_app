package com.example.data

enum class TaskStates(val displayName: String){
    PENDING(displayName = "Pendiente"),
    IN_PROGRESS(displayName = "En progreso"),
    DONE(displayName = "Terminado"),
    BLOCKED(displayName = "Bloqueado");

    fun GetDisplayName(): String{
        return displayName
    }
}

data class TaskInfo(
    val taskTitle: String,
    val taskMessage: String,
    val taskStatus: TaskStates
)