package com.example.todolist.management

import android.content.Context
import android.util.Log
import com.example.todolist.interfaces.TaskStatusListener
import com.example.todolist.model.TaskModel
import com.example.todolist.services.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationManager(private val context: Context, private val listener: TaskStatusListener) : Runnable {
    private var isRunning: Boolean = false
    private var dbManager: DbManager? = null

    override fun run() {
        isRunning = true

        dbManager = DbManager(context)

        CoroutineScope(Dispatchers.IO).launch {
            while (isRunning) {
                val tasksList = dbManager!!.getTasksCloseToEndDate()

                tasksList.forEach {
                    task -> NotificationService(context).showNotification(task)
                    val tempTask = TaskModel(
                        task.id,
                        task.title,
                        task.description,
                        task.category,
                        task.creationDate,
                        task.endDate,
                        task.attachment,
                        0
                    )
                    dbManager!!.updateTask(tempTask)
                    val taskStatusChanger = TaskStatusChanger(task, listener)
                    Thread(taskStatusChanger).start()
                    delay(2000)
                }
                delay(5000)
            }
        }
    }

    fun getIsRunning(): Boolean {
        return isRunning
    }
}
