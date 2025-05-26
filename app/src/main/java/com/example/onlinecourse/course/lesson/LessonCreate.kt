package com.example.onlinecourse.course.lesson

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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

    val parsedSequence = sequenceNumber.toLongOrNull()
    val allFieldsValid = name.isNotBlank() && description.isNotBlank() && parsedSequence != null

    OnlineCursesTheme {
        AppBar(
            title = "Создание урока",
            showTopBar = true,
            showBottomBar = true,
            navController = navController,
            userId = userId,
            role = role
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название урока") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            sequenceNumber = input
                        }
                    },
                    label = { Text("Номер по порядку") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.createLesson(
                            courseId = courseId.toLong(),
                            name = name,
                            description = description,
                            sequenceNumber = parsedSequence!!
                        ) { lessonId ->
                            if (lessonId != null) {
                                Toast.makeText(context, "Урок успешно создан", Toast.LENGTH_SHORT).show()
                                navController.navigate("lessonView/$userId/$role/$courseId/$lessonId")
                            } else {
                                Toast.makeText(context, "Не удалось создать урок", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = allFieldsValid && !viewModel.isLoading
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
