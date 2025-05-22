package com.example.onlinecourse.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.onlinecourse.R
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.function.FeatureButton
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun Users(navController: NavHostController, userId: String, role: String) {
    OnlineCursesTheme {
        AppBar(
            title = "Пользователи",
            showTopBar = true,
            showBottomBar = false,
            navController,
            userId = userId,
            role = role
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                FeatureButton(
                    text = "Все пользователи",
                    icon = R.drawable.user,
                    onClick = { navController.navigate("allUsersView/${userId}/${role}") }
                )

                FeatureButton(
                    text = "Новые пользователи",
                    icon = R.drawable.user,
                    onClick = { navController.navigate("newUsersView/${userId}/${role}") }
                )
            }
        }
    }
}