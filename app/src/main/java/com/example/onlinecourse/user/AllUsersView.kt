package com.example.onlinecourse.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.network.UserStatisticsViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.ui.theme.OnlineCursesTheme


@Composable
fun AllUsersView(navController: NavHostController, userId: String, role: String) {
    val viewModel: UserStatisticsViewModel = viewModel()

    val allUsers by remember { derivedStateOf { viewModel.allUsers } }
    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
    val errorMessage by remember { derivedStateOf { viewModel.errorMessage } }

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var sortOption by remember { mutableStateOf("ФИО ↑") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadAllUsers()
    }

    OnlineCursesTheme {
        AppBar(
            title = "Все пользователи",
            showTopBar = true,
            showBottomBar = true,
            navController = navController,
            userId = userId,
            role = role
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Поиск по ФИО") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text("Сортировка: $sortOption")
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("ФИО ↑") },
                            onClick = {
                                sortOption = "ФИО ↑"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("ФИО ↓") },
                            onClick = {
                                sortOption = "ФИО ↓"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Дата ↑") },
                            onClick = {
                                sortOption = "Дата ↑"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Дата ↓") },
                            onClick = {
                                sortOption = "Дата ↓"
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                } else if (!errorMessage.isNullOrEmpty()) {
                    Text(text = errorMessage ?: "", color = Color.Red)
                } else {
                    val filteredUsers = allUsers
                        .filter {
                            val fullName = listOfNotNull(it.lastName, it.firstName, it.patronymic).joinToString(" ")
                            fullName.contains(searchQuery.text, ignoreCase = true)
                        }
                        .sortedWith(
                            when (sortOption) {
                                "ФИО ↑" -> compareBy {
                                    listOfNotNull(it.lastName, it.firstName, it.patronymic).joinToString(" ")
                                }

                                "ФИО ↓" -> compareByDescending {
                                    listOfNotNull(it.lastName, it.firstName, it.patronymic).joinToString(" ")
                                }

                                "Дата ↑" -> compareBy { it.dateRegistration ?: "" }
                                "Дата ↓" -> compareByDescending { it.dateRegistration ?: "" }

                                else -> compareBy { it.userId }
                            }
                        )

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredUsers) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("pageView/$userId/$role/${user.userId}")
                                    }
                                    .padding(vertical = 6.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    val fullName = listOfNotNull(user.lastName, user.firstName, user.patronymic)
                                        .joinToString(" ")
                                    Text(
                                        text = fullName,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "Дата регистрации: ${user.dateRegistration ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
