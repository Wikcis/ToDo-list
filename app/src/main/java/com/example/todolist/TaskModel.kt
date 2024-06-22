package com.example.todolist
data class TaskModel(
    val title: String,
    val description: String,
    val category: String,
    val creationDate: String,
    val endDate: String,
    val attachment: Boolean
)
