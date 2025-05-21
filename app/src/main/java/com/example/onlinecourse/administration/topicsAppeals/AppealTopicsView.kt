package com.example.onlinecourse.administration.topicsAppeals


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.R
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.network.AppealTopicResponse
import com.example.onlinecourse.network.AppealTopicViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme


@Composable
fun AppealTopicsView(navController: NavHostController, userId: String, role: String) {
    val viewModel: AppealTopicViewModel = viewModel()
    val topics = viewModel.topics
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val operationResult = viewModel.operationResult
    val context = LocalContext.current

    var topicToDelete by remember { mutableStateOf<AppealTopicResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTopics()
    }

    LaunchedEffect(operationResult) {
        if (operationResult == true) {
            Toast.makeText(context, "Тема успешно удалена", Toast.LENGTH_SHORT).show()
            viewModel.operationResult = null
        } else if (operationResult == false) {
            Toast.makeText(context, errorMessage ?: "Ошибка при удалении темы", Toast.LENGTH_SHORT).show()
            viewModel.operationResult = null
        }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Темы обращений",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("appealTopicsAdd/$userId/$role")
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Создать тему",
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (isLoading) {
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
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else if (topics.isEmpty()) {
                    Text(
                        text = "Темы обращений не найдены",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(topics.size) { index ->
                            val topic = topics[index]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                navController.navigate("appealTopicsEdit/$userId/$role/${topic.id}/${topic.name}/${topic.description}")
                                            }
                                    ) {
                                        Text(
                                            text = topic.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = topic.description,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Удалить тему",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable {
                                                topicToDelete = topic
                                            }
                                    )
                                }
                            }
                        }
                    }
                }

                if (topicToDelete != null) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { topicToDelete = null },
                        title = { Text("Подтверждение удаления") },
                        text = { Text("Вы действительно хотите удалить тему \"${topicToDelete?.name}\"?") },
                        confirmButton = {
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    topicToDelete?.let {
                                        viewModel.deleteTopic(it.id)
                                    }
                                    topicToDelete = null
                                }
                            ) {
                                Text("Да")
                            }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(
                                onClick = { topicToDelete = null }
                            ) {
                                Text("Нет")
                            }
                        }
                    )
                }
            }
        }
    }
}