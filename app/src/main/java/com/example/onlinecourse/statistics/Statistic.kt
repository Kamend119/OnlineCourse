package com.example.onlinecourse.statistics

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
fun Statistic(navController: NavHostController, userId: String, role: String) {
    OnlineCursesTheme {
        AppBar(
            title = "Статистика",
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
                    text = "Статистика платформы",
                    icon = R.drawable.graph,
                    onClick = { navController.navigate("platformStatisticsView/${userId}/${role}") }
                )

                FeatureButton(
                    text = "Статистика по обращениям",
                    icon = R.drawable.person,
                    onClick = { navController.navigate("appealStatisticsView/${userId}/${role}") }
                )

                FeatureButton(
                    text = "Активность пользователей",
                    icon = R.drawable.user,
                    onClick = { navController.navigate("userActivityStatsView/${userId}/${role}") }
                )
            }
        }
    }
}