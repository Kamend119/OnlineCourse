package com.example.onlinecourse.course.lesson

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import kotlinx.coroutines.launch
import com.example.onlinecourse.network.LessonDetailsViewModel

@Composable
fun LessonView(navController: NavHostController, userId: String, role: String, courseId: String, lessonId: String) {
    val viewModel: LessonDetailsViewModel = viewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(lessonId) {
        viewModel.loadLessonDetails(lessonId.toLong())
        viewModel.loadStepsByLesson(lessonId.toLong())
    }

    val lesson = viewModel.lessonDetail
    val steps = viewModel.steps

    OnlineCursesTheme {
        AppBar(
            title = "Просмотр урока",
            showTopBar = true,
            showBottomBar = true,
            navController = navController,
            userId = userId,
            role = role
        ) {
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        lesson?.let {
                            if (role == "Учитель") {
                                var name by remember { mutableStateOf(it.lessonName) }
                                var description by remember { mutableStateOf(it.lessonDescription) }
                                var sequenceNumberText by remember { mutableStateOf(it.sequenceNumber.toString()) }

                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Название урока") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    label = { Text("Описание") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = sequenceNumberText,
                                    onValueChange = {
                                        if (it.all { char -> char.isDigit() }) {
                                            sequenceNumberText = it
                                        }
                                    },
                                    label = { Text("Порядковый номер") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "Дата публикации: ${it.datePublication}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Button(onClick = {
                                    val seqNum = sequenceNumberText.toLongOrNull() ?: it.sequenceNumber
                                    coroutineScope.launch {
                                        viewModel.updateLesson(
                                            lessonId = lessonId.toLong(),
                                            courseId = courseId.toLong(),
                                            name = name,
                                            description = description,
                                            sequenceNumber = seqNum
                                        )
                                        Toast.makeText(
                                            context,
                                            viewModel.updateResult ?: "Ошибка",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        viewModel.loadLessonDetails(lessonId.toLong())
                                    }
                                }) {
                                    Text("Сохранить изменения")
                                }
                            } else {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = it.lessonName, style = MaterialTheme.typography.titleLarge)
                                        Spacer(Modifier.height(8.dp))
                                        Text(text = it.lessonDescription)
                                        Spacer(Modifier.height(8.dp))
                                        Text(text = "Порядковый номер: ${it.sequenceNumber}")
                                        Text(text = "Дата публикации: ${it.datePublication}")
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            Text("Шаги урока:", style = MaterialTheme.typography.titleMedium)
                            if (steps.isEmpty()) {
                                Text("Шагов нет")
                            } else {
                                steps.forEach { step ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                navController.navigate("stepView/${userId}/${role}/${courseId}/${lessonId}/${step.stepId}")
                                            },
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(text = step.stepName)
                                            Text(text = step.datePublication)
                                        }
                                    }
                                }
                            }
                            if (role == "Учитель") {
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        navController.navigate("stepCreate/${userId}/${role}/${courseId}/${lessonId}")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Добавить шаг")
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            if (role == "Администратор" || role == "Учитель") {
                                Button(
                                    onClick = { openDialog.value = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Удалить урок", color = Color.White)
                                }
                            }
                        } ?: run {
                            Text(
                                text = viewModel.errorMessage ?: "Нет данных об уроке",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                if (openDialog.value) {
                    AlertDialog(
                        onDismissRequest = { openDialog.value = false },
                        title = { Text("Удалить урок") },
                        text = { Text("Вы уверены, что хотите удалить урок? Это действие необратимо.") },
                        confirmButton = {
                            TextButton(onClick = {
                                coroutineScope.launch {
                                    val result = viewModel.deleteLesson(lessonId.toLong(), userId.toLong())
                                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                                    navController.navigate("courseView/$userId/$role/$courseId/Нет")
                                }
                                openDialog.value = false
                            }) {
                                Text("Удалить", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { openDialog.value = false }) {
                                Text("Отмена")
                            }
                        }
                    )
                }
            }
        }
    }
}
