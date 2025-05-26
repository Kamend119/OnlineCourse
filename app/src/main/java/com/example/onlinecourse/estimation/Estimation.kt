package com.example.onlinecourse.estimation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.network.StepAnswersViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Estimation(navController: NavHostController, userId: String, role: String) {
    val viewModel: StepAnswersViewModel = viewModel()
    val teacherId = userId.toLong()
    val courses = viewModel.teacherCourses
    val lessons = viewModel.lessonsByCourse
    val steps = viewModel.steps

    var selectedCourseName by remember { mutableStateOf("") }
    var selectedLessonName by remember { mutableStateOf("") }

    var selectedCourseId by remember { mutableStateOf<Long?>(null) }
    var selectedLessonId by remember { mutableStateOf<Long?>(null) }

    var expandedCourse by remember { mutableStateOf(false) }
    var expandedLesson by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCoursesByTeacher(teacherId)
    }

    OnlineCursesTheme {
        AppBar(
            title = "Оценка знаний",
            showTopBar = true,
            showBottomBar = true,
            navController = navController,
            userId = userId,
            role = role
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {

                ExposedDropdownMenuBox(
                    expanded = expandedCourse,
                    onExpandedChange = { expandedCourse = !expandedCourse }
                ) {
                    OutlinedTextField(
                        value = selectedCourseName,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text("Курс") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCourse)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCourse,
                        onDismissRequest = { expandedCourse = false }
                    ) {
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.courseName) },
                                onClick = {
                                    selectedCourseName = course.courseName
                                    selectedCourseId = course.courseId
                                    selectedLessonName = ""
                                    selectedLessonId = null
                                    expandedCourse = false
                                    viewModel.loadLessonsByCourse(course.courseId)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedLesson,
                    onExpandedChange = {
                        if (selectedCourseId != null) expandedLesson = !expandedLesson
                    }
                ) {
                    OutlinedTextField(
                        value = selectedLessonName,
                        onValueChange = {},
                        readOnly = true,
                        enabled = selectedCourseId != null,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text("Урок") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLesson)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedLesson,
                        onDismissRequest = { expandedLesson = false }
                    ) {
                        lessons.forEach { lesson ->
                            DropdownMenuItem(
                                text = { Text(lesson.lessonName) },
                                onClick = {
                                    selectedLessonName = lesson.lessonName
                                    selectedLessonId = lesson.lessonId
                                    expandedLesson = false
                                    viewModel.loadStepsByLesson(lesson.lessonId)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (steps.isNotEmpty()) {
                    Text("Шаги урока:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(steps) { step ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("answersForStepView/$userId/$role/${step.stepId}")
                                    }
                                    .padding(vertical = 6.dp),
                                elevation = cardElevation(2.dp),
                                colors = cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Шаг: ${step.stepName}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Дата: ${step.datePublication}",
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