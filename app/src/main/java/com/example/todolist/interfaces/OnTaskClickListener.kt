package com.example.todolist.interfaces

import com.example.todolist.model.TaskModel

interface OnTaskClickListener {
    fun onTaskClick(task: TaskModel)
    fun onTaskLongClick(task: TaskModel, position: Int)
}