package com.example.onlinecourse.statistics

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.function.StatisticRow
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.Composable
import com.example.onlinecourse.function.saveCourseStatisticsToExcelWithUri
import com.example.onlinecourse.network.TeacherCoursesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCoursesView(navController: NavHostController, userId: String, role: String) {
    val viewModel: TeacherCoursesViewModel = viewModel()
    val context = LocalContext.current
    val courses = viewModel.courses
    val statistics = viewModel.selectedCourseStatistics
    val isLoading = viewModel.isLoading
    val issuedCertificate = viewModel.issuedCertificate
    val errorMessage = viewModel.errorMessage
    var showDialog by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf<Long?>(null) }

    var expanded by remember { mutableStateOf(false) }
    var selectedCourseName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadCoursesByTeacher(userId.toLong())
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri: Uri? ->
        uri?.let {
            saveCourseStatisticsToExcelWithUri(context, statistics, selectedCourseName, it)
        }
    }

    if (showDialog && selectedUserId != null) {
        OnlineCursesTheme{
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.issueCertificate(
                                selectedUserId!!,
                                courses.first { it.courseName == selectedCourseName }.courseId
                            )
                            showDialog = false
                        }
                    ) {
                        Text("Да", color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Нет", color = MaterialTheme.colorScheme.secondary)
                    }
                },
                title = {
                    Text(
                        text = "Выдача сертификата",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        text = "Вы уверены, что хотите выдать сертификат этому студенту?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            )
        }

    }

    LaunchedEffect(issuedCertificate, errorMessage) {
        if (issuedCertificate) {
            Toast.makeText(context, "Сертификат выдан", Toast.LENGTH_SHORT).show()
            viewModel.resetCertificateState()
        } else if (!errorMessage.isNullOrEmpty()) {
            Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Статистика курса",
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

                if (courses.isNotEmpty()) {
                    Text(text = "Выберите курс", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCourseName,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            label = { Text("Курс") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            courses.forEach { course ->
                                DropdownMenuItem(
                                    text = { Text(course.courseName) },
                                    onClick = {
                                        selectedCourseName = course.courseName
                                        expanded = false
                                        viewModel.loadCourseStatistics(course.courseId)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading ->
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

                    !errorMessage.isNullOrEmpty() -> Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )

                    selectedCourseName.isNotBlank() && statistics.isEmpty() -> Text(
                        text = "Статистика по выбранному курсу отсутствует",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    statistics.isNotEmpty() -> {
                        LazyColumn {
                            items(statistics) { stat ->
                                Column(
                                    Modifier
                                        .clickable {
                                            selectedUserId = stat.userId
                                            showDialog = true
                                        }
                                ) {
                                    StatisticRow("Студент", stat.studentFullName)
                                    StatisticRow("Выполнено заданий", stat.completedTasksCount.toString())
                                    StatisticRow("Средняя оценка", String.format("%.2f", stat.averageScore))
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                }
                            }
                            item {
                                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                val filename = "CourseStats-${selectedCourseName}-${timestamp}.xlsx"

                                Button(
                                    onClick = { launcher.launch(filename) },
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .fillMaxWidth()
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Сохранить в Excel")
                                }

                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}