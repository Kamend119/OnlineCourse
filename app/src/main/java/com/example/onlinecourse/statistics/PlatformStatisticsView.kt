package com.example.onlinecourse.statistics

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.function.StatisticRow
import com.example.onlinecourse.function.saveStatisticsToExcelWithUri
import com.example.onlinecourse.network.PlatformStatisticsViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlatformStatisticsView(navController: NavHostController, userId: String, role: String) {
    val viewModel: PlatformStatisticsViewModel = viewModel()
    val statistics = viewModel.statistics
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.loadPlatformStatistics()
    }

    OnlineCursesTheme {
        AppBar(
            title = "Статистика платформы",
            showTopBar = true,
            showBottomBar = true,
            navController = navController,
            userId = userId,
            role = role
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when {
                    isLoading ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    !errorMessage.isNullOrEmpty() -> Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    statistics != null -> {
                        val statsList = listOf(
                            "Всего пользователей" to statistics.totalUsers.toString(),
                            "Активные за 7 дней" to statistics.activeUsers7d.toString(),
                            "Новых пользователей сегодня" to statistics.newUsersToday.toString(),
                            "Всего курсов" to statistics.totalCourses.toString(),
                            "Опубликованных курсов" to statistics.publishedCourses.toString(),
                            "Обращений всего" to statistics.totalAppeals.toString(),
                            "Открытых обращений" to statistics.openAppeals.toString(),
                            "Записей на курсы" to statistics.courseEnrollments.toString(),
                            "Завершённых курсов" to statistics.completedCourses.toString(),
                            "Процент завершения" to "${statistics.completionRate}%"
                        )

                        LazyColumn {
                            items(statsList) { (label, value) ->
                                StatisticRow(label = label, value = value)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }

                        val context = LocalContext.current
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        ) { uri: Uri? ->
                            uri?.let {
                                saveStatisticsToExcelWithUri(context, statsList, it)
                            }
                        }
                        Button(
                            onClick = {
                                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                                    Date()
                                )
                                val filename = "PlatformStatistics-$timestamp.xlsx"
                                launcher.launch(filename)
                            },
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Сохранить в Excel")
                        }
                    }
                }
            }
        }
    }
}

