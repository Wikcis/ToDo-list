package com.example.todolist.managers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeManager {
    private val pattern = "HH:mm:ss yyyy-MM-dd"
    private val formatter = DateTimeFormatter.ofPattern(pattern)

    fun getCurrentDate(): String{
        return LocalDateTime.now().format(formatter)
    }

    fun formatDate(date: LocalDateTime): String{
        return formatter.format(date)
    }

    fun updateTime(date: String, minutes: Long): String{
        val dateTime = LocalDateTime.parse(date, formatter)
        val updatedDateTime = dateTime.plusMinutes(minutes)
        return formatDate(updatedDateTime)
    }

    fun validateDate(date: String): String {
        return when {
            date.isEmpty() -> ToastManager.SPECIFY_DATE
            else -> try {
                val parsedDate = LocalDateTime.parse(date, formatter)
                when {
                    !formatter.format(parsedDate)
                        .equals(date) -> ToastManager.INCORRECT_EXECUTION_DATE

                    parsedDate.isBefore(LocalDateTime.now()) -> ToastManager.PAST_EXECUTION_DATE
                    else -> ToastManager.SUCCESS
                }
            } catch (e: Exception) {
                ToastManager.INCORRECT_EXECUTION_DATE
            }
        }
    }

    fun isDatePast(date: String): Boolean {
        val dateTime = LocalDateTime.parse(date, formatter)
        return dateTime.isBefore(LocalDateTime.now())
    }
}