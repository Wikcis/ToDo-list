package com.example.todolist.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.todolist.R
import com.example.todolist.activities.EditTaskActivity
import com.example.todolist.model.TaskModel

class NotificationService(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val minutes = prefs.getString("minutes", "1 min")
    companion object{
        const val CHANNEL_ID = "notification_channel"
        const val CHANNEL_NAME = "ToDo List"
    }

    fun showNotification(task: TaskModel){
        val intent = Intent(context, EditTaskActivity::class.java)
        intent.putExtra("TASK_ID", task.id)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.todo_list)
                .setContentTitle("Reminder")
                .setContentText("Your task: \"${task.title}\" will start in $minutes")
                .addAction(
                    R.drawable.tick,
                    "Click here to show details",
                    activityPendingIntent
                )
                .build()
        notificationManager.notify(task.id, notification)
    }
}