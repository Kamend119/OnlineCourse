package com.example.onlinecourse.appeal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.R
import com.example.onlinecourse.network.AppealViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun AppealsView(navController: NavHostController, userId: String, role: String) {
    val viewModel: AppealViewModel = viewModel()
    val isAdmin = role == "Администратор"

    LaunchedEffect(userId, role) {
        if (isAdmin) {
            viewModel.loadAllAppeals()
        } else {
            viewModel.loadUserAppeals(userId.toLong())
        }
    }

    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val userAppeals = viewModel.userAppeals
    val allAppeals = viewModel.allAppeals

    val appeals = if (isAdmin) allAppeals else userAppeals

    OnlineCursesTheme {
        Scaffold(
            floatingActionButton = {
                if (!isAdmin) {
                    FloatingActionButton(
                        onClick = { navController.navigate("appealAdd/${userId}/${role}") },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "Добавить",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    "Обращения",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(top = 32.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    errorMessage != null -> {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    appeals.isEmpty() -> {
                        Text("Обращений нет", style = MaterialTheme.typography.bodyMedium)
                    }

                    else -> {
                        if (isAdmin) {
                            LazyColumn {
                                items(allAppeals) { appeal ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .clickable {
                                                navController.navigate("appealView/${userId}/${role}/${appeal.id}")
                                            },
                                        elevation = CardDefaults.cardElevation(1.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text("Обращение №${appeal.id}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Тема: ${appeal.topicName}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Заголовок: ${appeal.headingAppeal}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Статус: ${appeal.statusName}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyColumn {
                                items(userAppeals) { appeal ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .clickable {
                                                navController.navigate("appealView/${userId}/${role}/${appeal.id}")
                                            },
                                        elevation = CardDefaults.cardElevation(1.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text("Обращение №${appeal.id}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Тема: ${appeal.topicName}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Заголовок: ${appeal.headingAppeal}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Статус: ${appeal.statusName}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

