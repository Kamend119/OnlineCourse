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
import com.example.onlinecourse.function.saveUserActivityStatsToExcelWithUri
import com.example.onlinecourse.network.UserActivityStatsViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserActivityStatsView(navController: NavHostController, userId: String, role: String) {
    val viewModel: UserActivityStatsViewModel = viewModel()
    val stats = viewModel.activityStats
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val context = LocalContext.current

    var selectedDays by remember { mutableIntStateOf(14) }
    val daysOptions = listOf(7, 14, 30)
    var expanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri: Uri? ->
        uri?.let {
            saveUserActivityStatsToExcelWithUri(context, stats, it)
        }
    }

    LaunchedEffect(selectedDays) {
        viewModel.loadUserActivityStats(daysBack = selectedDays)
    }

    OnlineCursesTheme {
        AppBar(
            title = "Пользователи",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = "$selectedDays дней",
                        onValueChange = {},
                        label = { Text("Промежуток") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        daysOptions.forEach { days ->
                            DropdownMenuItem(
                                text = { Text("$days дней") },
                                onClick = {
                                    selectedDays = days
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    !errorMessage.isNullOrEmpty() -> Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    stats.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(stats) { item ->
                                val items = listOf(
                                    "Дата" to item.day,
                                    "Активные пользователи" to item.activeUsers.toString(),
                                    "Новые пользователи" to item.newUsers.toString(),
                                    "Отвеченные вопросы" to item.answeredQuestions.toString(),
                                    "Новые записи на курсы" to item.newEnrollments.toString()
                                )
                                items.forEach { (label, value) ->
                                    StatisticRow(label = label, value = value)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }

                        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        val filename = "UserActivityStats-$timestamp.xlsx"

                        Button(
                            onClick = { launcher.launch(filename) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
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
