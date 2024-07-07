package com.example.todolist.managers

import java.time.Duration
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

    private fun updateTime(date: String, minutes: Long): String{
        val dateTime = LocalDateTime.parse(date, formatter)
        val updatedDateTime = dateTime.minusMinutes(minutes)
        return formatDate(updatedDateTime)
    }

    fun getDurationBetweenDates(minutes: String?, taskEndDate: String): Duration? {
        val currDate = LocalDateTime.parse(TimeManager().getCurrentDate(), formatter)
        val minutesString = minutes?.filter { it.isDigit() }
        val updatedDate = TimeManager().updateTime(taskEndDate, minutesString!!.toLong())
        val endDate = LocalDateTime.parse(updatedDate, formatter)
        return Duration.between(currDate, endDate)
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