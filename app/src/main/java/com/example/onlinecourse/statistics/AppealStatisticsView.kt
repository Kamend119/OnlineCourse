package com.example.onlinecourse.statistics

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.function.StatisticRow
import com.example.onlinecourse.function.saveAppealStatisticsToExcelWithUri
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.Composable
import com.example.onlinecourse.network.AppealStatisticsViewModel

@Composable
fun AppealStatisticsView(navController: NavHostController, userId: String, role: String) {
    val viewModel: AppealStatisticsViewModel = viewModel()
    val statistics = viewModel.statistics
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.loadAppealStatistics()
    }

    OnlineCursesTheme {
        AppBar(
            title = "Обращения",
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
                    statistics.isNotEmpty() -> {
                        val context = LocalContext.current
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        ) { uri: Uri? ->
                            uri?.let {
                                saveAppealStatisticsToExcelWithUri(context, statistics, it)
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                        ) {
                            items(statistics) { stat ->
                                val fields = listOf(
                                    "Статус" to stat.status,
                                    "Кол-во обращений" to stat.appealCount.toString(),
                                    "Среднее время ответа (ч)" to String.format("%.2f", stat.avgResponseHours),
                                    "Просроченные обращения" to stat.overdueAppeals.toString(),
                                    "Частая тема" to stat.mostCommonTopic,
                                    "Среднее файлов на обращение" to String.format("%.2f", stat.avgFilesPerAppeal)
                                )

                                fields.forEach { (label, value) ->
                                    StatisticRow(label = label, value = value)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }

                        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        val filename = "AppealStatistics-$timestamp.xlsx"

                        Button(
                            onClick = { launcher.launch(filename) },
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