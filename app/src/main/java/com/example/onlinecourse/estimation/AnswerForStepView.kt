package com.example.onlinecourse.estimation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.network.StepAnswersViewModel
import com.example.onlinecourse.network.StepDetailResponse
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import kotlinx.coroutines.launch

@Composable
fun AnswerForStepView(navController: NavHostController, userId: String, role: String, answerId: String, stepId: String) {
    val viewModel: StepAnswersViewModel = viewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var step by remember { mutableStateOf<StepDetailResponse?>(null) }

    var scoreInput by remember { mutableStateOf(TextFieldValue("")) }
    var commentInput by remember { mutableStateOf(TextFieldValue("")) }

    val isLoading by remember { viewModel::isLoading }
    val operationSuccess by viewModel.operationSuccess.collectAsState()

    LaunchedEffect(stepId) {
        viewModel.getStepDetails(stepId.toLong(), answerId.toLong()) { result ->
            step = result
            result?.userScore?.let {
                scoreInput = TextFieldValue(it.toString())
            }
            commentInput = TextFieldValue(result?.userCommentTeacher.toString())
        }
    }

    LaunchedEffect(operationSuccess) {
        operationSuccess?.let {
            Toast.makeText(context, if (it) "Ответ оценен" else "Ошибка при оценивании", Toast.LENGTH_SHORT).show()
            viewModel.resetOperationSuccess()
        }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Ответ",
            showTopBar = true,
            showBottomBar = true,
            navController = navController,
            userId = userId,
            role = role
        ) {
            if (isLoading && step == null) {
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
                return@AppBar
            }

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                step?.let { s ->
                    item {
                        Text("Название шага:", style = MaterialTheme.typography.titleMedium)
                        Text(s.stepName)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Тип шага:", style = MaterialTheme.typography.titleMedium)
                        Text(s.stepTypeName)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Содержание:", style = MaterialTheme.typography.titleMedium)
                        Text(s.stepContent)

                        if (s.stepTypeName == "Вопрос с вариантами ответа") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Варианты ответов:", style = MaterialTheme.typography.titleMedium)
                            s.answerOptions?.forEach {
                                Text("- ${it.text} (Баллы: ${it.score}) ${if (it.correct) "[✔]" else ""}")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Ответ пользователя:", style = MaterialTheme.typography.titleLarge)

                        if (s.stepTypeName == "Вопрос без вариантов ответа") {
                            Text("Текст ответа: ${s.userAnswerText.orEmpty()}")
                        }

                        if (s.stepTypeName == "Вопрос с вариантами ответа") {
                            Text("Выбранные опции:", style = MaterialTheme.typography.titleMedium)
                            s.selectedOptions?.forEach {
                                Text("- ${it.text} (Баллы: ${it.score})")
                            }
                        }

                        if (s.stepTypeName == "Вопрос с приложением" && !s.answerFileName.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Файл ответа: ${s.answerFileName}")
                            Text(
                                text = "Скачать",
                                modifier = Modifier
                                    .clickable {
                                        viewModel.downloadFile(s.answerFilePath ?: "")
                                        Toast.makeText(context, "Загрузка файла...", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (!s.userCommentStudent.isNullOrEmpty()) {
                            Text("Комментарий студента: ${s.userCommentStudent}")
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Оценка:", style = MaterialTheme.typography.titleLarge)
                        OutlinedTextField(
                            value = scoreInput,
                            onValueChange = { newValue ->
                                if (newValue.text.all { it.isDigit() }) {
                                    scoreInput = newValue
                                }
                            },
                            label = { Text("Баллы") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            label = { Text("Комментарий преподавателя") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            val score = scoreInput.text.toLongOrNull()
                            if (score == null) {
                                Toast.makeText(context, "Введите корректную оценку", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (commentInput.text.isBlank()) {
                                Toast.makeText(context, "Введите комментарий", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            coroutineScope.launch {
                                viewModel.evaluateAnswer(
                                    answerUserId = s.userAnswerId ?: return@launch,
                                    score = score,
                                    commentTeacher = commentInput.text
                                )
                            }
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text(if (s.userScore != null) "Изменить оценку" else "Оценить")
                        }
                    }
                }
            }
        }
    }
}