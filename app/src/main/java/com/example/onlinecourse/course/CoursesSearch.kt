package com.example.onlinecourse.course

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import androidx.compose.runtime.Composable
import com.example.onlinecourse.network.CourseByTeacherResponse
import com.example.onlinecourse.network.CourseDataViewModel
import com.example.onlinecourse.network.CourseResponse
import com.example.onlinecourse.network.UserCourseResponse
import kotlinx.coroutines.launch

@Composable
fun CoursesSearch(navController: NavHostController, userId: String, role: String) {
    val viewModel: CourseDataViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val drawerState = remember { DrawerState(initialValue = DrawerValue.Closed) }

    var selectedStatus by remember { mutableStateOf<String?>(null) }

    var selectedCategories by remember { mutableStateOf<List<String>>(emptyList()) }
    val tempSelectedCategories = remember { mutableStateListOf<String>() }

    val categories = viewModel.courseCategories
    val isLoading = viewModel.isLoading

    val courses = when (role) {
        "Студент" -> if (selectedStatus == null || selectedStatus == "Новые") viewModel.allCourses else viewModel.coursesByUserAndStatus
        "Учитель" -> viewModel.coursesByTeacher
        else -> viewModel.allCourses
    }

    LaunchedEffect(role) {
        viewModel.loadCourseCategories()
        when (role) {
            "Студент", "Администратор" -> viewModel.loadAllCourses()
            "Учитель" -> viewModel.loadCoursesByTeacher(userId.toLong())
        }
    }

    LaunchedEffect(selectedStatus) {
        if (role == "Студент") {
            if (selectedStatus != null && selectedStatus != "Новые") {
                viewModel.loadCoursesByUserAndStatus(userId.toLong(), selectedStatus!!)
            } else {
                viewModel.loadAllCourses()
            }
        }
    }

    val filteredCourses = remember(courses, selectedCategories) {
        if (selectedCategories.isEmpty()) courses
        else courses.filter { course ->
            val category = when (course) {
                is CourseResponse -> course.categoryName
                is UserCourseResponse -> course.categoryName
                is CourseByTeacherResponse -> course.courseCategoryName
                else -> null
            }
            category != null && selectedCategories.contains(category)
        }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Поиск курсов",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(300.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Фильтры", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text("Категории")

                            // При открытии drawer копируем выбранные категории в temp
                            LaunchedEffect(drawerState.currentValue) {
                                if (drawerState.currentValue == DrawerValue.Open) {
                                    tempSelectedCategories.clear()
                                    tempSelectedCategories.addAll(selectedCategories)
                                }
                            }

                            LazyColumn(
                                Modifier.height(400.dp)
                            ) {
                                item {
                                    categories.forEach { category ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable {
                                                    if (tempSelectedCategories.contains(category.categoryName)) {
                                                        tempSelectedCategories.remove(category.categoryName)
                                                    } else {
                                                        tempSelectedCategories.add(category.categoryName)
                                                    }
                                                }
                                        ) {
                                            Checkbox(
                                                checked = tempSelectedCategories.contains(category.categoryName),
                                                onCheckedChange = { checked ->
                                                    if (checked) tempSelectedCategories.add(category.categoryName)
                                                    else tempSelectedCategories.remove(category.categoryName)
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = category.categoryName)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = {
                                selectedCategories = tempSelectedCategories.toList()
                                scope.launch { drawerState.close() }
                            }) {
                                Text("Применить")
                            }
                        }
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (role == "Студент") {
                        DropdownMenuBox(
                            label = "Статус",
                            options = listOf("Новые", "В прохождении", "Отложен", "Завершен"),
                            selectedOption = selectedStatus
                        ) {
                            selectedStatus = it
                        }
                    }
                        Button(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Фильтры")
                            Text("Фильтры", modifier = Modifier.padding(start = 8.dp))
                        }
                    }



                    Spacer(modifier = Modifier.height(12.dp))

                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                            items(filteredCourses) { course ->
                                when (course) {
                                    is CourseResponse -> {
                                        CourseItem(
                                            name = course.name,
                                            category = course.categoryName,
                                            teacher = "${course.teacherLastName} ${course.teacherFirstName} ${course.teacherPatronymic.orEmpty()}".trim(),
                                            date = course.datePublication,
                                            onClick = { navController.navigate("courseView/$userId/$role/${course.id}") }
                                        )
                                    }
                                    is UserCourseResponse -> {
                                        CourseItem(
                                            name = course.courseName,
                                            category = course.categoryName,
                                            teacher = "${course.teacherLastName} ${course.teacherFirstName} ${course.teacherPatronymic.orEmpty()}".trim(),
                                            date = course.datePublication,
                                            onClick = { navController.navigate("courseView/$userId/$role/${course.courseId}") }
                                        )
                                    }
                                    is CourseByTeacherResponse -> {
                                        CourseItem(
                                            name = course.courseName,
                                            category = course.courseCategoryName,
                                            teacher = null,
                                            date = course.datePublication,
                                            onClick = { navController.navigate("courseView/$userId/$role/${course.courseId}") }
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
}

@Composable
fun CourseItem(
    name: String,
    category: String,
    teacher: String?,
    date: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Название: $name", style = MaterialTheme.typography.titleMedium)
            teacher?.let {
                Text("Преподаватель: $it", style = MaterialTheme.typography.bodyMedium)
            }
            Text("Категория: $category", style = MaterialTheme.typography.bodySmall)
            Text("Дата публикации: $date", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun DropdownMenuBox(label: String, options: List<String?>, selectedOption: String?, onSelected: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = selectedOption ?: label)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option ?: "Все") }, onClick = {
                    onSelected(option)
                    expanded = false
                })
            }
        }
    }
}
