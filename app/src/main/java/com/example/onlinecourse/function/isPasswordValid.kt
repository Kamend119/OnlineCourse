package com.example.onlinecourse.function

import java.util.regex.Pattern

fun isPasswordValid(password: String): Boolean {
    val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$"
    return Pattern.matches(passwordPattern, password)
}
