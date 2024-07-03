package com.example.todolist.managers

import com.example.todolist.interfaces.TaskStatusListener
import com.example.todolist.model.TaskModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskStatusChanger(private val task: TaskModel, private val listener: TaskStatusListener) : Runnable {
    override fun run() {
        CoroutineScope(Dispatchers.IO).launch {
            while (!TimeManager().isDatePast(task.endDate)) {
                delay(5000)
            }
            withContext(Dispatchers.Main) {
                listener.onTaskStatusChanged()
            }
        }
    }
}