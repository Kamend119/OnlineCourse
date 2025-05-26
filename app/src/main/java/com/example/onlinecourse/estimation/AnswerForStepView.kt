package com.example.onlinecourse.estimation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.network.AnswerForStepResponse
import com.example.onlinecourse.network.StepAnswersViewModel
import com.example.onlinecourse.network.StepDetailResponse
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import kotlinx.coroutines.launch

@Composable
fun AnswerForStepView(navController: NavHostController, userId: String, role: String, answerId: String, stepId: String) {
    val viewModel: StepAnswersViewModel = viewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var stepDetails by remember { mutableStateOf<StepDetailResponse?>(null) }
    var answer by remember { mutableStateOf<AnswerForStepResponse?>(null) }

    var scoreInput by remember { mutableStateOf(TextFieldValue("")) }
    var commentInput by remember { mutableStateOf(TextFieldValue("")) }

    val isLoading by remember { viewModel::isLoading }
    val errorMessage by remember { viewModel::errorMessage }
    val operationSuccess by remember { viewModel::operationSuccess }

    LaunchedEffect(answerId) {
        viewModel.loadAnswerDetail(answerId.toLong())
        snapshotFlow { viewModel.selectedAnswer }.collect { loadedAnswer ->
            if (loadedAnswer != null) {
                answer = loadedAnswer
                viewModel.getStepDetails(stepId.toLong(), userId.toLong()) { details ->
                    if (details.isNotEmpty()) stepDetails = details[0]
                }
                loadedAnswer.score?.let {
                    scoreInput = TextFieldValue(it.toString())
                }
                commentInput = TextFieldValue(loadedAnswer.commentTeacher.orEmpty())
            }
        }
    }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess == true) {
            Toast.makeText(context, "Ответ оценен", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            viewModel.operationSuccess = null
        } else if (operationSuccess == false) {
            Toast.makeText(context, "Ошибка при оценивании", Toast.LENGTH_SHORT).show()
            viewModel.operationSuccess = null
        }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Ответ",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
            if (isLoading && stepDetails == null && answer == null) {
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
                return@AppBar
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                stepDetails?.let { step ->
                    item {
                        Text("Название шага:", style = MaterialTheme.typography.titleMedium)
                        Text(step.stepName, modifier = Modifier.padding(bottom = 8.dp))

                        Text("Тип шага:", style = MaterialTheme.typography.titleMedium)
                        Text(step.stepTypeName, modifier = Modifier.padding(bottom = 8.dp))

                        Text("Содержание шага:", style = MaterialTheme.typography.titleMedium)
                        Text(step.stepContent, modifier = Modifier.padding(bottom = 16.dp))

                        if (!step.answerOptionTexts.isNullOrEmpty()
                            && !step.answerOptionScores.isNullOrEmpty()
                            && step.answerOptionTexts!!.size == step.answerOptionScores!!.size
                            && step.stepTypeName == "Вопрос с вариантами ответа"
                        ) {
                            Text("Варианты ответов:", style = MaterialTheme.typography.titleMedium)
                            step.answerOptionTexts!!.forEachIndexed { idx, optionText ->
                                val score = step.answerOptionScores!!.getOrNull(idx) ?: 0
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = optionText,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Баллы: $score")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ответ студента:", style = MaterialTheme.typography.titleLarge)
                }

                answer?.let { ans ->
                    item {
                        Text(
                            "ФИО студента: ${ans.studentLastName.orEmpty()} ${ans.studentFirstName.orEmpty()} ${ans.studentPatronymic.orEmpty()}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (stepDetails?.stepTypeName == "Вопрос без вариантов ответа") {
                        item {
                            Text("Ответ студента:", style = MaterialTheme.typography.titleMedium)
                            Text(ans.answerText.orEmpty(), modifier = Modifier.padding(bottom = 16.dp))
                        }
                    }

                    if (stepDetails?.stepTypeName == "Вопрос с вариантами ответа"
                        && !ans.optionText.isNullOrEmpty()
                        && !ans.optionScore.isNullOrEmpty()
                    ) {
                        item {
                            Text("Ответ студента (варианты):", style = MaterialTheme.typography.titleMedium)
                        }
                        itemsIndexed(ans.optionText) { idx, option ->
                            val score = ans.optionScore.getOrNull(idx) ?: 0
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = option,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Баллы: $score")
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    if (stepDetails?.stepTypeName == "Вопрос с приложением"
                        && !ans.filePath.isNullOrEmpty()
                    ) {
                        item {
                            Text("Приложенные файлы:", style = MaterialTheme.typography.titleMedium)
                        }
                        items(ans.filePath) { path ->
                            Text(
                                text = path,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.downloadFile(path)
                                        Toast.makeText(context, "Загрузка файла...", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }

                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Оценка ответа:", style = MaterialTheme.typography.titleLarge)
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
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val scoreLong = scoreInput.text.toLongOrNull()
                            if (scoreLong == null) {
                                Toast.makeText(context, "Введите корректное число для оценки", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (commentInput.text.isBlank()) {
                                Toast.makeText(context, "Введите комментарий", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val answerUserId = answerId.toLong()
                            coroutineScope.launch {
                                viewModel.evaluateAnswer(
                                    answerUserId,
                                    scoreLong,
                                    commentInput.text.takeIf { it.isNotBlank() }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (answer?.score != null) "Изменить оценку" else "Оценить"
                        )
                    }
                }
            }
        }
    }
}

