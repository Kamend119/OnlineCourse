package com.example.onlinecourse.course

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.function.DropdownMenuBox
import com.example.onlinecourse.function.InfoSection
import com.example.onlinecourse.network.CourseDetailsViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import kotlinx.coroutines.launch

@Composable
fun CourseView(navController: NavHostController, userId: String, role: String, courseId: String, statusName: String) {
    val viewModel: CourseDetailsViewModel = viewModel()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }
    val warnResult by viewModel.warnResult.collectAsState()
    val enrollmentResult by viewModel.enrollmentResult.collectAsState()
    val course = viewModel.courseDetail
    val lessons = viewModel.lessons

    LaunchedEffect(warnResult) {
        warnResult?.let {
            Toast.makeText(context, if (it) "Предупреждение выдано" else "Произошла ошибка", Toast.LENGTH_SHORT).show()
            viewModel.resetWarnResult()
        }
    }
    LaunchedEffect(enrollmentResult) {
        enrollmentResult?.let {
            Toast.makeText(context, if (it) "Курс добавлен" else "Произошла ошибка", Toast.LENGTH_SHORT).show()
            viewModel.resetEnrollmentResult()
        }
    }

    LaunchedEffect(courseId) {
        viewModel.loadCourseDetails(courseId.toLong())
        viewModel.loadLessons(courseId.toLong())
        viewModel.loadCourseCategories()
    }

    OnlineCursesTheme {
        AppBar(
            title = "Просмотр курса",
            showTopBar = true,
            showBottomBar = true,
            navController = navController,
            userId = userId,
            role = role
        ) {
            if (viewModel.isLoading) {
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
            } else {
                LazyColumn {
                    item {
                        course?.let {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (role == "Учитель") {
                                    var name by remember { mutableStateOf(it.name) }
                                    var description by remember { mutableStateOf(it.description) }
                                    var selectedCategoryName by remember {
                                        mutableStateOf(it.categoryName)
                                    }

                                    OutlinedTextField(
                                        value = name,
                                        onValueChange = { name = it },
                                        label = { Text("Название курса") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = description,
                                        onValueChange = { description = it },
                                        label = { Text("Описание курса") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    DropdownMenuBox(
                                        label = "Категория",
                                        options = viewModel.categories.map { cat -> cat.categoryName },
                                        selectedOption = selectedCategoryName
                                    ) {
                                        selectedCategoryName = it.toString()
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            val selectedCategoryId = viewModel.categories.find { cat -> cat.categoryName == selectedCategoryName }?.categoryId
                                            if (selectedCategoryId != null) {
                                                coroutineScope.launch {
                                                    viewModel.updateCourse(
                                                        courseId = courseId.toLong(),
                                                        courseCategoryId = selectedCategoryId,
                                                        userId = userId.toLong(),
                                                        name = name,
                                                        description = description
                                                    )
                                                    viewModel.loadCourseDetails(courseId.toLong())
                                                }
                                            } else {
                                                Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Text("Сохранить изменения")
                                    }
                                } else {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = course.name,
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            InfoSection(label = "Описание", value = course.description)
                                            InfoSection(label = "Категория", value = course.categoryName)
                                            InfoSection(label = "Дата публикации", value = course.datePublication)
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))
                                if (role != "Студент" || statusName == "В прохождении") {
                                    Text("Уроки:", style = MaterialTheme.typography.titleMedium)

                                    if (lessons.isEmpty()) {
                                        Spacer(Modifier.height(8.dp))
                                        Text("Уроков ещё нет", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    } else {
                                        lessons.forEach { lesson ->
                                            CourseItem(
                                                name = lesson.lessonName,
                                                category = "",
                                                teacher = null,
                                                date = lesson.datePublication,
                                                onClick = {
                                                    navController.navigate("lessonView/$userId/$role/$courseId/${lesson.lessonId}")
                                                }
                                            )
                                        }
                                    }

                                    if (role == "Учитель") {
                                        Spacer(Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                navController.navigate("lessonCreate/$userId/$role/$courseId")
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Добавить урок")
                                        }
                                    }
                                }

                                if (role == "Администратор") {
                                    Spacer(Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                viewModel.warnOnCourse(courseId.toLong())
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Выдать предупреждение")
                                    }

                                    Spacer(Modifier.height(16.dp))
                                    Button(
                                        onClick = { openDialog.value = true },
                                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Удалить курс", color = Color.White)
                                    }
                                }

                                if (role == "Учитель") {
                                    Spacer(Modifier.height(16.dp))
                                    Button(
                                        onClick = { openDialog.value = true },
                                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Удалить курс", color = Color.White)
                                    }
                                }

                                if (role == "Студент" && statusName != "В прохождении" && statusName != "Пройден") {
                                    Spacer(Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                viewModel.enrollOrDeferCourse(userId.toLong(), courseId.toLong(), "В прохождении")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Поступить на курс")
                                    }

                                    Spacer(Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                viewModel.enrollOrDeferCourse(userId.toLong(), courseId.toLong(), "Отложен")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Отложить курс")
                                    }
                                }
                            }
                        } ?: run {
                            Text(
                                text = viewModel.errorMessage ?: "Нет данных",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = { openDialog.value = false },
                    title = { Text("Удалить курс") },
                    text = { Text("Вы уверены, что хотите удалить курс? Это действие необратимо.") },
                    confirmButton = {
                        TextButton(onClick = {
                            openDialog.value = false
                            coroutineScope.launch {
                                val resultMessage = viewModel.deleteCourse(courseId.toLong(), userId.toLong())
                                Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }) {
                            Text("Удалить", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { openDialog.value = false }) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
}
