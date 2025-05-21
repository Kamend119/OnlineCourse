package com.example.onlinecourse.administration.courseCategories

import android.widget.Toast
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
import com.example.onlinecourse.network.CourseCategoryViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme


@Composable
fun CourseCategoriesEdit(navController: NavHostController, userId: String, role: String, categoryId: String, categoryName: String, categoryDescription: String) {
    val viewModel: CourseCategoryViewModel = viewModel()
    val context = LocalContext.current

    // Инициализируем поля из параметров, но только при первом запуске (LaunchedEffect + remember)
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        name = categoryName
        description = categoryDescription
    }

    val isLoading = viewModel.isLoading
    val operationResult = viewModel.operationResult
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(operationResult) {
        if (operationResult == true) {
            Toast.makeText(context, "Категория обновлена", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            viewModel.operationResult = null
        } else if (operationResult == false) {
            Toast.makeText(context, errorMessage ?: "Ошибка при обновлении категории", Toast.LENGTH_SHORT).show()
            viewModel.operationResult = null
        }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Редактировать категорию",
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название категории") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание категории") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (name.isNotBlank() && description.isNotBlank()) {
                                viewModel.updateCategory(
                                    courseId = categoryId.toLong(),
                                    name = name.trim(),
                                    description = description.trim()
                                )
                            } else {
                                Toast.makeText(context, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Сохранить изменения")
                    }
                }
            }
        }
    }
}