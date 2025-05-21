package com.example.onlinecourse.administration.topicsAppeals

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import com.example.onlinecourse.network.AppealTopicViewModel


@Composable
fun AppealTopicsAdd(navController: NavHostController, userId: String, role: String) {
    val viewModel: AppealTopicViewModel = viewModel()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val isLoading = viewModel.isLoading
    val operationResult = viewModel.operationResult
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(operationResult) {
        if (operationResult == true) {
            Toast.makeText(context, "Тема обращения успешно создана", Toast.LENGTH_SHORT).show()
            navController.popBackStack()  // Возврат назад после создания
            viewModel.operationResult = null
        } else if (operationResult == false) {
            Toast.makeText(context, errorMessage ?: "Ошибка при создании темы", Toast.LENGTH_SHORT).show()
            viewModel.operationResult = null
        }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Добавить тему",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название темы") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание темы") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(24.dp))

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
                } else {
                    Button(
                        onClick = {
                            if (name.isNotBlank() && description.isNotBlank()) {
                                viewModel.addTopic(name.trim(), description.trim())
                            } else {
                                Toast.makeText(context, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Создать тему")
                    }
                }
            }
        }
    }
}