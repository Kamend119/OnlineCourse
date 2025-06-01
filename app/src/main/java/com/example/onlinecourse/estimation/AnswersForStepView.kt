package com.example.onlinecourse.estimation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.network.StepAnswersViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun AnswersForStepView(navController: NavHostController, userId: String, role: String, stepId: String) {
    val viewModel: StepAnswersViewModel = viewModel()
    val isLoading by remember { viewModel::isLoading }
    val errorMessage by remember { viewModel::errorMessage }
    val answers by remember { viewModel::answersForStep }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(stepId) {
        viewModel.loadAnswersForStep(stepId.toLong())
    }

    // Фильтрация по ФИО
    val filteredAnswers = answers.filter {
        val fio = listOfNotNull(it.studentLastName, it.studentFirstName, it.studentPatronymic).joinToString(" ").lowercase()
        fio.contains(searchQuery.text.lowercase())
    }

    OnlineCursesTheme {
        AppBar(
            title = "Ответы на шаг",
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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Поиск по ФИО") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                if (isLoading) {
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
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (!isLoading && filteredAnswers.isNotEmpty()) {
                    LazyColumn {
                        items(filteredAnswers) { answer ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        navController.navigate("answerForStepView/$userId/$role/${answer.answerId}/${stepId}")
                                    },
                                elevation = cardElevation(4.dp),
                                colors = cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "ФИО: ${answer.studentLastName ?: ""} ${answer.studentFirstName ?: ""} ${answer.studentPatronymic ?: ""}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Дата: ${answer.answerDate ?: "—"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                } else if (!isLoading && filteredAnswers.isEmpty()) {
                    Text(
                        text = "Совпадений не найдено.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}