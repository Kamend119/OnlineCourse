package com.example.onlinecourse.function

import java.util.regex.Pattern

fun isEmailValid(email: String): Boolean {
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return Pattern.matches(emailPattern, email)
}