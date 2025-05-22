package com.example.onlinecourse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.function.FeatureButton
import com.example.onlinecourse.network.DailyStatisticsViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun MainPage(navController: NavHostController, userId: String, role: String) {
    when (role) {
        "Студент" -> {
            val viewModel: DailyStatisticsViewModel = viewModel()
            val isLoading by remember { viewModel::isLoading }
            val errorMessage by remember { viewModel::errorMessage }
            val statistics by remember { viewModel::dailyStatistics }

            LaunchedEffect(userId) {
                viewModel.loadDailyStatistics(userId.toLong())
            }

            var showDialog by remember { mutableStateOf(false) }
            var selectedTaskCount by remember { mutableIntStateOf(0) }
            var selectedDate by remember { mutableStateOf("") }

            fun getCellColor(taskCount: Int): Color {
                return when (taskCount) {
                    in 0..2 -> Color(0xFFE0E0E0)
                    in 3..5 -> Color(0xFFD9A7FF)
                    in 6..8 -> Color(0xFFBF68FF)
                    in 9..10 -> Color(0xFFB645FF)
                    else -> Color(0xFFD175FF)
                }
            }

            OnlineCursesTheme {
                AppBar(
                    title = "Главная",
                    showTopBar = true,
                    showBottomBar = true,
                    navController,
                    userId = userId,
                    role = role
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = "Статистика по дням",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        when {
                            isLoading -> {
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
                            }

                            errorMessage != null -> {
                                Text("Ошибка: $errorMessage", color = MaterialTheme.colorScheme.error)
                            }

                            else -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 32.dp)
                                ) {
                                    statistics.chunked(7).forEach { weekStats ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            weekStats.forEach { dateStep ->
                                                val taskCount = dateStep.answersCount
                                                val date = dateStep.dateDay

                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .border(1.dp, Color.Gray)
                                                        .background(getCellColor(taskCount.toInt()))
                                                        .clickable {
                                                            selectedTaskCount = taskCount.toInt()
                                                            selectedDate = date
                                                            showDialog = true
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = taskCount.toString(),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Black
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }

                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                confirmButton = {
                                    TextButton(onClick = { showDialog = false }) {
                                        Text("Ок", color = MaterialTheme.colorScheme.onSurface)
                                    }
                                },
                                title = {
                                    Text(
                                        "Количество заданий",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                text = {
                                    Text(
                                        "В день $selectedDate выполнено $selectedTaskCount заданий.",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
        "Учитель" -> {
            OnlineCursesTheme {
                AppBar(
                    title = "Главная",
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
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        FeatureButton(
                            text = "Мои курсы",
                            icon = R.drawable.book,
                            onClick = { navController.navigate("") }
                        )

                        FeatureButton(
                            text = "Создать курс",
                            icon = R.drawable.add,
                            onClick = { navController.navigate("") }
                        )

                        FeatureButton(
                            text = "Статистика",
                            icon = R.drawable.graph,
                            onClick = { navController.navigate("teacherCoursesView/${userId}/${role}") }
                        )

                        FeatureButton(
                            text = "Проверка выполнения",
                            icon = R.drawable.check_mark,
                            onClick = { navController.navigate("") }
                        )
                    }
                }
            }
        }
        "Администратор" -> {
            OnlineCursesTheme {
                AppBar(
                    title = "Главная",
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
                            text = "Администрирование",
                            icon = R.drawable.settings,
                            onClick = { navController.navigate("administration/${userId}/${role}") }
                        )

                        FeatureButton(
                            text = "Поддержка",
                            icon = R.drawable.person,
                            onClick = { navController.navigate("appealsView/$userId/$role") }
                        )

                        FeatureButton(
                            text = "Статистика",
                            icon = R.drawable.graph,
                            onClick = { navController.navigate("statistic/${userId}/${role}") }
                        )

                        FeatureButton(
                            text = "Пользователи",
                            icon = R.drawable.user,
                            onClick = { navController.navigate("allUsersView/${userId}/${role}") }
                        )

                        FeatureButton(
                            text = "Курсы",
                            icon = R.drawable.book,
                            onClick = { navController.navigate("") }
                        )

                    }
                }
            }
        }
    }
}
