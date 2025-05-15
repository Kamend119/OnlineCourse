package com.example.onlinecourse.function

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

fun isValidAge(dateBirthday: String): Boolean {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val birthDate = dateFormat.parse(dateBirthday) ?: return false

    val currentDate = Calendar.getInstance().time
    val currentCalendar = Calendar.getInstance().apply { time = currentDate }
    val birthCalendar = Calendar.getInstance().apply { time = birthDate }

    val age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

    if (currentCalendar.get(Calendar.MONTH) < birthCalendar.get(Calendar.MONTH) ||
        (currentCalendar.get(Calendar.MONTH) == birthCalendar.get(Calendar.MONTH) &&
                currentCalendar.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH))) {
        return age >= 14
    }

    return age >= 14
}