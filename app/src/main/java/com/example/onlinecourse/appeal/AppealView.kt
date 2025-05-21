package com.example.onlinecourse.appeal

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.network.AppealViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun AppealView(navController: NavHostController, userId: String, role: String, appealId: String) {
    val viewModel: AppealViewModel = viewModel()
    val context = LocalContext.current
    val isAdmin = role == "Администратор"
    val appealIdLong = appealId.toLongOrNull()
    var answerText by remember { mutableStateOf("") }

    val appealDetail by remember { derivedStateOf { viewModel.appealDetail } }
    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
    val errorMessage by remember { derivedStateOf { viewModel.errorMessage } }
    val operationSuccess by remember { derivedStateOf { viewModel.operationSuccess } }

    LaunchedEffect(appealIdLong) {
        appealIdLong?.let { viewModel.loadAppealDetail(it) }
    }

    LaunchedEffect(operationSuccess) {
        operationSuccess?.let {
            if (it) {
                Toast.makeText(context, "Ответ успешно отправлен", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Ошибка при отправке ответа", Toast.LENGTH_SHORT).show()
            }
            viewModel.operationSuccess = null
        }
    }

    OnlineCursesTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "Поддержка",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                errorMessage != null -> {
                    Text("Ошибка: $errorMessage", color = MaterialTheme.colorScheme.error)
                }

                appealDetail != null -> {
                    val detail = appealDetail!!

                    OutlinedTextField(
                        value = detail.topicName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Тема обращения") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = detail.statusName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Статус обращения") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = detail.headingAppeal,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Заголовок") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = detail.textAppeal,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Текст обращения") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        maxLines = 5
                    )

                    if (!detail.textAnswer.isNullOrEmpty()) {
                        OutlinedTextField(
                            value = "Ответ: ${detail.textAnswer}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ответ администратора") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            maxLines = 5
                        )
                    }

                    if (isAdmin && detail.statusName != "Завершен") {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Ответ на обращение",
                            style = MaterialTheme.typography.titleSmall
                        )

                        OutlinedTextField(
                            value = answerText,
                            onValueChange = { answerText = it },
                            label = { Text("Введите ответ") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            maxLines = 5
                        )

                        Button(
                            onClick = {
                                if (appealIdLong != null) {
                                    viewModel.submitAnswer(
                                        appealId = appealIdLong,
                                        userId = userId.toLong(),
                                        text = answerText
                                    )
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Отправить ответ")
                        }
                    }
                }

                else -> {
                    Text("Обращение не найдено")
                }
            }
        }
    }
}


