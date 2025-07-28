package com.example.data

enum class ScreenRoutes(
    val route: String,
    val label: String,
    val iconId: Int
) {
    HOME("homescreen", "Inicio", R.drawable.outline_home_24),
    NEW_TASK("new_task", "Agregar tarea", R.drawable.outline_add_circle_24)
}