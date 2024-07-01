package com.example.todolist

import android.content.Context
import android.util.Log
import com.example.todolist.management.DbManager
import com.example.todolist.services.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Reminder(private val context: Context) : Runnable {
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
                }
                delay(5000)
            }
        }
    }

    fun getIsRunning(): Boolean {
        return isRunning
    }
}
