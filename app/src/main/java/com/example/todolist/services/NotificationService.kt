package com.example.todolist.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.todolist.R
import com.example.todolist.activities.EditTaskActivity
import com.example.todolist.managers.AlarmReceiver
import com.example.todolist.managers.TimeManager
import com.example.todolist.model.TaskModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationService {
    companion object{
        const val CHANNEL_ID = "notification_channel"
        const val CHANNEL_NAME = "ToDo List"
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(context: Context, task: TaskModel) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val minutes = prefs.getString("minutes", "1 min")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("TASK_ID", task.id)
        intent.putExtra("TASK_TITLE", task.title)
        val activityPendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeToNotify = TimeManager().getDurationBetweenDates(minutes, task.endDate)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + timeToNotify!!.toMillis(),
            activityPendingIntent
        )
    }
}