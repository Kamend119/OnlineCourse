package com.example.onlinecourse.course.lesson


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.network.LessonCreateViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme


@Composable
fun LessonCreate(navController: NavHostController, userId: String, role: String, courseId: String) {
    val context = LocalContext.current
    val viewModel: LessonCreateViewModel = viewModel()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var sequenceNumber by remember { mutableStateOf("") }

    OnlineCursesTheme {
        AppBar(
            title = "Создание урока",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название урока") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание урока") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sequenceNumber,
                    onValueChange = { sequenceNumber = it },
                    label = { Text("Номер по порядку") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val parsedSequence = sequenceNumber.toLongOrNull()
                        if (parsedSequence == null) {
                            Toast.makeText(context, "Введите корректный номер по порядку", Toast.LENGTH_SHORT).show()
                        } else if (name.isBlank() || description.isBlank()) {
                            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.createLesson(
                                courseId = courseId.toLong(),
                                name = name,
                                description = description,
                                sequenceNumber = parsedSequence
                            ) { lessonId ->
                                navController.navigate("lessonView/$userId/$role/$courseId/$lessonId")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !viewModel.isLoading
                ) {
                    Text("Создать урок")
                }

                if (viewModel.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                viewModel.errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}