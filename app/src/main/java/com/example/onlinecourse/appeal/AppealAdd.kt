package com.example.onlinecourse.appeal

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
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
import com.example.onlinecourse.network.AppealViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppealAdd(navController: NavHostController, userId: String, role: String) {
    val focusManager = LocalFocusManager.current
    val viewModel: AppealViewModel = viewModel()

    val topics = viewModel.topics
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage
    val operationSuccess = viewModel.operationSuccess

    var expanded by remember { mutableStateOf(false) }
    var selectedTopicName by remember { mutableStateOf("Выберите тему обращения") }
    var selectedTopicId by remember { mutableStateOf<Long?>(null) }

    var heading by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(operationSuccess) {
        if (operationSuccess == true) {
            Toast.makeText(context, "Обращение успешно создано", Toast.LENGTH_SHORT).show()
            navController.navigate("appealsView/${userId}/${role}") {
                popUpTo("appealAdd/${userId}/${role}") { inclusive = true }
            }
        } else if (operationSuccess == false) {
            Toast.makeText(context, "Ошибка при создании обращения", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadTopics()
    }

    OnlineCursesTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .padding(75.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Создать обращение",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedTopicName,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            "Тема обращения",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    topics.forEach { topic ->
                        DropdownMenuItem(
                            text = { Text(topic.name) },
                            onClick = {
                                selectedTopicName = topic.name
                                selectedTopicId = topic.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = heading,
                onValueChange = { heading = it },
                label = {
                    Text(
                        text = "Заголовок",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = {
                    Text(
                        text = "Опишите проблему",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Button(
                onClick = {
                    if (selectedTopicId != null && heading.isNotBlank() && text.isNotBlank()) {
                        viewModel.submitAppeal(
                            userId = userId.toLong(),
                            topicAppealId = selectedTopicId!!,
                            heading = heading,
                            text = text
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp),
                enabled = !isLoading
            ) {
                Text("Создать")
            }

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

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}