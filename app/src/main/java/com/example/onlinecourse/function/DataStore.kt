package com.example.onlinecourse.function

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

class ThemePreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("theme_preferences", Context.MODE_PRIVATE)
    private val DARK_MODE_KEY = "dark_mode_key"
    fun getDarkModeState(): Boolean {
        return preferences.getBoolean(DARK_MODE_KEY, false)
    }
    fun setDarkModeState(isDarkMode: Boolean) {
        preferences.edit().putBoolean(DARK_MODE_KEY, isDarkMode).apply()
    }
}

@Composable
fun rememberDarkModeState(): Pair<Boolean, (Boolean) -> Unit> {
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences(context) }
    val isSystemDarkMode = isSystemInDarkTheme()
    val (isDarkMode, setIsDarkMode) = remember { mutableStateOf(isSystemDarkMode) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        val darkModeState = themePreferences.getDarkModeState()
        setIsDarkMode(darkModeState)
    }
    val setDarkMode: (Boolean) -> Unit = { value: Boolean ->
        setIsDarkMode(value)
        coroutineScope.launch {
            themePreferences.setDarkModeState(value)
        }
    }
    return isDarkMode to setDarkMode
}

@Composable
fun rememberDarkModeStateSystem(): Boolean {
    val isDarkMode = isSystemInDarkTheme()
    return remember { mutableStateOf(isDarkMode) }.value
}


data class User(
    val userId: String,
    val role: String
)

class UserPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    private val USERID_KEY = "userId"
    private val ROLE_KEY = "role"

    fun getUser(): User {
        val userId = preferences.getString(USERID_KEY, "-1")?: "-1"
        val role = preferences.getString(ROLE_KEY, "Не распознано")?: "Не распознано"
        return User(userId, role)
    }
    fun setUser(userId: String, role: String) {
        preferences.edit().putString(USERID_KEY, userId).apply()
        preferences.edit().putString(ROLE_KEY, role).apply()
    }
    fun deleteUser() {
        preferences.edit().remove(USERID_KEY).apply()
        preferences.edit().remove(ROLE_KEY).apply()
    }
}