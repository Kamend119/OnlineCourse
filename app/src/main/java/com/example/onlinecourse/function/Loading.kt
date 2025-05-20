package com.example.onlinecourse.function

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController


@Composable
fun Loading(navController: NavHostController, userId: String, role: String) {
    val (isLoading, setIsLoading) = remember { mutableStateOf(true) }
    val user = remember { mutableStateOf(User(userId, role)) }

    val context = LocalContext.current
    val userPreferences = UserPreferences(context)

    LaunchedEffect(key1 = Unit) {
        user.value = userPreferences.getUser()
        setIsLoading(false)
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        when {
            user.value.role == "Студент" && user.value.userId.isNotEmpty() -> navController.navigate("")
            user.value.role == "Преподаватель" && user.value.userId.isNotEmpty() -> navController.navigate("")
            user.value.role == "Администратор" && user.value.userId.isNotEmpty() -> navController.navigate("")
            user.value.role == "Не распознано" && user.value.userId == "-1" -> navController.navigate("entrance")
        }
    }
}