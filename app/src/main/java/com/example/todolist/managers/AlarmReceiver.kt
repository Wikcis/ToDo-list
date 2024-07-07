package com.example.todolist.managers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.todolist.R
import com.example.todolist.activities.EditTaskActivity
import com.example.todolist.services.NotificationService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("TASK_ID", -1)
        val taskTitle = intent.getStringExtra("TASK_TITLE")

        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val minutes = prefs.getString("minutes", "1 min")

        val tempIntent = Intent(context, EditTaskActivity::class.java)
        tempIntent.putExtra("TASK_ID", taskId)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            tempIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(context, NotificationService.CHANNEL_ID)
            .setSmallIcon(R.drawable.todo_list)
            .setContentTitle("Reminder")
            .setContentText("Your task: \"$taskTitle\" will start in $minutes")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                R.drawable.tick,
                "Click here to show details",
                activityPendingIntent
            )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(taskId, notificationBuilder.build())
    }
}
