package com.example.onlinecourse.course

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.onlinecourse.function.DropdownMenuBox
import com.example.onlinecourse.network.CourseCreateViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun CourseCreate(navController: NavHostController, userId: String, role: String) {
    val viewModel: CourseCreateViewModel = viewModel()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadCourseCategories()
    }

    val isLoading = viewModel.isLoading
    val categories = viewModel.categories
    val error = viewModel.errorMessage

    // Показываем тост, если ошибка есть
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Создание курса",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Название курса") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Описание курса") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5
                        )

                        Column {
                            Text(
                                text = "Категория",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            DropdownMenuBox(
                                label = "Выберите категорию",
                                options = categories.map { it.categoryName },
                                selectedOption = selectedCategoryName
                            ) {
                                selectedCategoryName = it.toString()
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val selectedCategory =
                                    categories.find { it.categoryName == selectedCategoryName }
                                if (selectedCategory == null) {
                                    Toast.makeText(context, "Выберите категорию", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.createCourse(
                                        courseCategoryId = selectedCategory.categoryId,
                                        userId = userId.toLong(),
                                        name = name,
                                        description = description
                                    ) { newCourseId ->
                                        Toast.makeText(context, "Курс создан", Toast.LENGTH_SHORT).show()
                                        navController.navigate("courseView/$userId/$role/$newCourseId/Учитель") {
                                            popUpTo("main") { inclusive = false }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = name.isNotBlank() && description.isNotBlank() && selectedCategoryName.isNotBlank()
                        ) {
                            Text("Создать курс")
                        }
                    }
                }
            }
        }
    }
}