package com.example.onlinecourse.function

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
        }
    } else {
        when {
            user.value.role == "Студент" && user.value.userId.isNotEmpty() -> navController.navigate("main/${user.value.userId}/${user.value.role}")
            user.value.role == "Учитель" && user.value.userId.isNotEmpty() -> navController.navigate("main/${user.value.userId}/${user.value.role}")
            user.value.role == "Администратор" && user.value.userId.isNotEmpty() -> navController.navigate("main/${user.value.userId}/${user.value.role}")
            user.value.role == "Не распознано" && user.value.userId == "-1" -> navController.navigate("entrance")
        }
    }
}