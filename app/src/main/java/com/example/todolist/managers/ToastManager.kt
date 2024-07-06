package com.example.todolist.managers

import android.content.Context
import android.widget.Toast

class ToastManager(private val context: Context){
    companion object{
        const val NOTIFICATIONS_ON = "Notifications are ON!"
        const val NOTIFICATIONS_OFF = "Notifications are OFF!"
        const val NO_TITLE = "There is no title!"
        const val TITLE_UNAVAILABLE = "There is already task with this title!"
        const val SPECIFY_DATE = "You must specify execution date!"
        const val INCORRECT_EXECUTION_DATE = "Execution date is incorrect!"
        const val PAST_EXECUTION_DATE = "Date must be in the future!"
        const val SUCCESS = "Success!"
        const val TASK_NOT_CHANGED = "Task has not been changed!"
        const val NO_FILE = "There isn't any file!"
    }
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}