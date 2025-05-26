package com.example.onlinecourse.course.step

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.network.LessonStepAnswerViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import okhttp3.ResponseBody
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepView(navController: NavHostController, userId: String, role: String, courseId: String, lessonId: String, stepId: String) {
    val viewModel: LessonStepAnswerViewModel = viewModel()
    val stepDetails = viewModel.stepDetails.firstOrNull()
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val deleteResult = viewModel.deleteResult
    val warningResult = viewModel.warningResult
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }
    var commentStudent by remember { mutableStateOf("") }
    var selectedOptionIds by remember { mutableStateOf<List<Long>>(emptyList()) }
    var selectedFile by remember { mutableStateOf<MultipartBody.Part?>(null) }
    var selectedFileName by remember { mutableStateOf<String>("") }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showWarnConfirmDialog by remember { mutableStateOf(false) }
    var showFileSaveDialog by remember { mutableStateOf(false) }
    var fileToSave by remember { mutableStateOf<Pair<String, ResponseBody>?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = File(context.cacheDir, "uploadFile")
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val multipart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            selectedFile = multipart
            selectedFileName = file.name
        }
    }

    // Загружаем детали шага при первом открытии
    LaunchedEffect(stepId) {
        viewModel.getStepDetails(stepId.toLong(), userId.toLong())
    }

    LaunchedEffect(stepDetails) {
        stepDetails?.let {
            name = it.stepName
            content = it.stepContent
            answerText = it.userAnswerText ?: ""
            commentStudent = it.userCommentStudent ?: ""
            selectedOptionIds = it.answerOptionIds ?: emptyList()
        }
    }

    // Обработка результатов операций
    LaunchedEffect(deleteResult) {
        deleteResult?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            if (it.contains("успешно")) {
                navController.popBackStack()
            }
        }
    }

    LaunchedEffect(warningResult) {
        warningResult?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    // Диалог для сохранения файла
    if (showFileSaveDialog && fileToSave != null) {
        AlertDialog(
            onDismissRequest = { showFileSaveDialog = false },
            title = { Text("Файл скачан") },
            text = { Text("Файл ${fileToSave?.first} успешно сохранен") },
            confirmButton = {
                TextButton(onClick = { showFileSaveDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    OnlineCursesTheme {
        AppBar(
            title = "Просмотр шага",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        errorMessage?.let { Text("Ошибка: $it", color = Color.Red) }

                        stepDetails?.let { step ->
                            // Общие поля для всех типов шагов
                            Text("Название шага:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            if (role == "Учитель") {
                                TextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(name, modifier = Modifier.padding(vertical = 8.dp))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Контент шага:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            if (role == "Учитель") {
                                TextField(
                                    value = content,
                                    onValueChange = { content = it },
                                    modifier = Modifier.fillMaxWidth().height(150.dp)
                                )
                            } else {
                                Text(content, modifier = Modifier.padding(vertical = 8.dp))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Поля, специфичные для типа шага
                            when (step.stepTypeName) {
                                "Лекция" -> {
                                    if (role == "Учитель") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Текст лекции:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                        TextField(
                                            value = content,
                                            onValueChange = { content = it },
                                            modifier = Modifier.fillMaxWidth().height(200.dp)
                                        )
                                    }
                                }

                                "Вопрос без вариантов ответа" -> {
                                    if (role == "Учитель") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Максимальный балл:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                        TextField(
                                            value = step.userScore?.toString() ?: "0",
                                            onValueChange = { step.userScore = it.toLongOrNull() ?: 0L },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    if (role == "Студент") {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Ваш ответ:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)

                                        if (step.userAnswerId != null && step.userScore != null) {
                                            // Показываем ответ и оценку
                                            Text("Ответ: $answerText", modifier = Modifier.padding(vertical = 8.dp))
                                            Text("Оценка: ${step.userScore}", modifier = Modifier.padding(vertical = 8.dp))
                                            step.userCommentTeacher?.let {
                                                Text("Комментарий преподавателя: $it", modifier = Modifier.padding(vertical = 8.dp))
                                            }
                                        } else {
                                            // Поле для ответа
                                            TextField(
                                                value = answerText,
                                                onValueChange = { answerText = it },
                                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                                label = { Text("Введите ваш ответ") }
                                            )

                                            // Поле для комментария
                                            Spacer(modifier = Modifier.height(8.dp))
                                            TextField(
                                                value = commentStudent,
                                                onValueChange = { commentStudent = it },
                                                modifier = Modifier.fillMaxWidth(),
                                                label = { Text("Комментарий (необязательно)") }
                                            )

                                            // Кнопка отправки
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Button(
                                                onClick = {
                                                    if (step.userAnswerId == null) {
                                                        viewModel.answerLessonStep(
                                                            userId.toLong(),
                                                            stepId.toLong(),
                                                            answerText,
                                                            null,
                                                            null,
                                                            null,
                                                            null,
                                                            commentStudent
                                                        )
                                                    } else {
                                                        viewModel.updateLessonStepAnswer(
                                                            step.userAnswerId!!,
                                                            userId.toLong(),
                                                            stepId.toLong(),
                                                            answerText,
                                                            null,
                                                            null,
                                                            null,
                                                            null,
                                                            commentStudent
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.align(Alignment.End)
                                            ) {
                                                Text("Отправить ответ")
                                            }
                                        }
                                    }
                                }

                                "Вопрос с вариантами ответа" -> {
                                    if (role == "Учитель") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Варианты ответов:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)

                                        step.answerOptionTexts?.forEachIndexed { index, optionText ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                TextField(
                                                    value = optionText,
                                                    onValueChange = { newText ->
                                                        step.answerOptionTexts = step.answerOptionTexts?.toMutableList()?.apply {
                                                            set(index, newText)
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Checkbox(
                                                    checked = step.answerOptionScores?.get(index) ?: 0L > 0,
                                                    onCheckedChange = { isChecked ->
                                                        step.answerOptionScores = step.answerOptionScores?.toMutableList()?.apply {
                                                            set(index, if (isChecked) 1L else 0L)
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    } else if (role == "Студент") {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Варианты ответов:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)

                                        if (step.userAnswerId != null && step.userScore != null) {
                                            // Показываем выбранные варианты и оценку
                                            Text("Выбранные варианты:", modifier = Modifier.padding(vertical = 8.dp))
                                            step.answerOptionTexts?.forEachIndexed { index, optionText ->
                                                if (selectedOptionIds.contains(step.answerOptionIds?.get(index))) {
                                                    Text("• $optionText", modifier = Modifier.padding(start = 16.dp))
                                                }
                                            }
                                            Text("Оценка: ${step.userScore}", modifier = Modifier.padding(vertical = 8.dp))
                                            step.userCommentTeacher?.let {
                                                Text("Комментарий преподавателя: $it", modifier = Modifier.padding(vertical = 8.dp))
                                            }
                                        } else {
                                            // Показываем варианты для выбора
                                            step.answerOptionTexts?.forEachIndexed { index, optionText ->
                                                val optionId = step.answerOptionIds?.get(index) ?: 0L
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedOptionIds = if (selectedOptionIds.contains(optionId)) {
                                                                selectedOptionIds - optionId
                                                            } else {
                                                                selectedOptionIds + optionId
                                                            }
                                                        }
                                                        .padding(8.dp)
                                                ) {
                                                    Checkbox(
                                                        checked = selectedOptionIds.contains(optionId),
                                                        onCheckedChange = { isChecked ->
                                                            selectedOptionIds = if (isChecked) {
                                                                selectedOptionIds + optionId
                                                            } else {
                                                                selectedOptionIds - optionId
                                                            }
                                                        }
                                                    )
                                                    Text(optionText, modifier = Modifier.weight(1f))
                                                }
                                            }

                                            // Поле для комментария
                                            Spacer(modifier = Modifier.height(8.dp))
                                            TextField(
                                                value = commentStudent,
                                                onValueChange = { commentStudent = it },
                                                modifier = Modifier.fillMaxWidth(),
                                                label = { Text("Комментарий (необязательно)") }
                                            )

                                            // Кнопка отправки
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Button(
                                                onClick = {
                                                    if (step.userAnswerId == null) {
                                                        viewModel.answerLessonStep(
                                                            userId.toLong(),
                                                            stepId.toLong(),
                                                            null,
                                                            selectedOptionIds,
                                                            null,
                                                            null,
                                                            null,
                                                            commentStudent
                                                        )
                                                    } else {
                                                        viewModel.updateLessonStepAnswer(
                                                            step.userAnswerId!!,
                                                            userId.toLong(),
                                                            stepId.toLong(),
                                                            null,
                                                            selectedOptionIds,
                                                            null,
                                                            null,
                                                            null,
                                                            commentStudent
                                                        )
                                                    }
                                                },
                                                enabled = selectedOptionIds.isNotEmpty(),
                                                modifier = Modifier.align(Alignment.End)
                                            ) {
                                                Text("Отправить ответ")
                                            }
                                        }
                                    }
                                }

                                "Вопрос с приложением" -> {
                                    if (role == "Учитель") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Прикрепленный файл:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)

                                        if (step.filePath != null) {
                                            Text("Текущий файл: ${step.originalName}")
                                            Button(
                                                onClick = {
                                                    viewModel.downloadFile(
                                                        step.filePath!!,
                                                        onSuccess = { responseBody ->
                                                            val fileName = step.originalName ?: "file"
                                                            fileToSave = fileName to responseBody
                                                            showFileSaveDialog = true
                                                        },
                                                        onFailure = { error ->
                                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                                        }
                                                    )
                                                },
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                Text("Скачать файл")
                                            }
                                        }

                                        Button(
                                            onClick = { filePickerLauncher.launch("*/*") },
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        ) {
                                            Text("Заменить файл")
                                        }

                                        if (selectedFileName.isNotEmpty()) {
                                            Text("Выбран новый файл: $selectedFileName")
                                        }
                                    } else if (role == "Студент") {
                                        Spacer(modifier = Modifier.height(16.dp))

                                        if (step.filePath != null) {
                                            Text("Файл для скачивания:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                            Button(
                                                onClick = {
                                                    viewModel.downloadFile(
                                                        step.filePath!!,
                                                        onSuccess = { responseBody ->
                                                            val fileName = step.originalName ?: "file"
                                                            fileToSave = fileName to responseBody
                                                            showFileSaveDialog = true
                                                        },
                                                        onFailure = { error ->
                                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                                        }
                                                    )
                                                },
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                Text("Скачать файл")
                                            }
                                        }

                                        if (step.userAnswerId != null && step.userScore != null) {
                                            // Показываем информацию о сданной работе
                                            Text("Ответ отправлен", modifier = Modifier.padding(vertical = 8.dp))
                                            Text("Оценка: ${step.userScore}", modifier = Modifier.padding(vertical = 8.dp))
                                            step.userCommentTeacher?.let {
                                                Text("Комментарий преподавателя: $it", modifier = Modifier.padding(vertical = 8.dp))
                                            }
                                        } else {
                                            // Поле для загрузки файла ответа
                                            Text("Ваш ответ:", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                            Button(
                                                onClick = { filePickerLauncher.launch("*/*") },
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                Text("Выбрать файл")
                                            }

                                            if (selectedFileName.isNotEmpty()) {
                                                Text("Выбран файл: $selectedFileName")
                                            }

                                            // Поле для комментария
                                            Spacer(modifier = Modifier.height(8.dp))
                                            TextField(
                                                value = commentStudent,
                                                onValueChange = { commentStudent = it },
                                                modifier = Modifier.fillMaxWidth(),
                                                label = { Text("Комментарий (необязательно)") }
                                            )

                                            // Кнопка отправки
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Button(
                                                onClick = {
                                                    selectedFile?.let { file ->
                                                        if (step.userAnswerId == null) {
                                                            viewModel.answerLessonStep(
                                                                userId.toLong(),
                                                                stepId.toLong(),
                                                                null,
                                                                null,
                                                                file.headers?.get("Content-Disposition"),
                                                                file.body?.contentType()?.toString(),
                                                                file.body?.contentLength(),
                                                                commentStudent,
                                                                file
                                                            )
                                                        } else {
                                                            viewModel.updateLessonStepAnswer(
                                                                step.userAnswerId!!,
                                                                userId.toLong(),
                                                                stepId.toLong(),
                                                                null,
                                                                null,
                                                                file.headers?.get("Content-Disposition"),
                                                                file.body?.contentType()?.toString(),
                                                                file.body?.contentLength(),
                                                                commentStudent,
                                                                file
                                                            )
                                                        }
                                                    }
                                                },
                                                enabled = selectedFile != null,
                                                modifier = Modifier.align(Alignment.End)
                                            ) {
                                                Text("Отправить ответ")
                                            }
                                        }
                                    }
                                }
                            }

                            // Общие кнопки для учителя и администратора
                            if (role == "Учитель") {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        when (step.stepTypeName) {
                                            "Лекция" -> viewModel.updateLectureStep(
                                                stepId.toLong(), name, content, step.sequenceNumber, step.obligatory
                                            ) {
                                                Toast.makeText(context, "Лекция обновлена", Toast.LENGTH_SHORT).show()
                                            }
                                            "Вопрос без вариантов ответа" -> viewModel.updateOpenQuestionStep(
                                                stepId.toLong(), name, content, step.sequenceNumber,
                                                step.timePasses ?: "00:00:00", step.obligatory, step.userScore ?: 0L
                                            ) {
                                                Toast.makeText(context, "Вопрос обновлен", Toast.LENGTH_SHORT).show()
                                            }
                                            "Вопрос с вариантами ответа" -> viewModel.updateMultipleChoiceQuestionStep(
                                                stepId.toLong(), name, content, step.sequenceNumber,
                                                step.timePasses ?: "00:00:00", step.obligatory, step.userScore ?: 0L,
                                                step.answerOptionTexts ?: emptyList(),
                                                step.answerOptionScores?.map { it > 0 } ?: emptyList(),
                                                step.answerOptionIds ?: emptyList()
                                            ) {
                                                Toast.makeText(context, "Вопрос обновлен", Toast.LENGTH_SHORT).show()
                                            }
                                            "Вопрос с приложением" -> {
                                                selectedFile?.let { file ->
                                                    viewModel.updateFileUploadQuestionStep(
                                                        stepId.toLong(), name, content, step.sequenceNumber,
                                                        step.timePasses ?: "00:00:00", step.obligatory, step.userScore ?: 0L,
                                                        selectedFileName, "application/octet-stream", file.body?.contentLength() ?: 0L,
                                                        file
                                                    ) {
                                                        Toast.makeText(context, "Вопрос обновлен", Toast.LENGTH_SHORT).show()
                                                    }
                                                } ?: run {
                                                    Toast.makeText(context, "Выберите файл для обновления", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Сохранить изменения")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { showDeleteConfirmDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Удалить шаг", color = Color.White)
                                }
                            }

                            if (role == "Администратор") {
                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { showDeleteConfirmDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Удалить шаг", color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { showWarnConfirmDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Выдать предупреждение", color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы уверены, что хотите удалить этот шаг?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteStep(stepId.toLong(), userId.toLong())
                        showDeleteConfirmDialog = false
                    }
                ) {
                    Text("Да", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Нет")
                }
            }
        )
    }

    // Диалог подтверждения предупреждения
    if (showWarnConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showWarnConfirmDialog = false },
            title = { Text("Выдать предупреждение") },
            text = { Text("Вы действительно хотите выдать предупреждение пользователю?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.warnOnStep(userId.toLong())
                        showWarnConfirmDialog = false
                    }
                ) {
                    Text("Да", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWarnConfirmDialog = false }) {
                    Text("Нет")
                }
            }
        )
    }
}

// Функция для сохранения файла
fun saveFile(context: Context, fileName: String, body: ResponseBody): Boolean {
    return try {
        val file = File(context.getExternalFilesDir(null), fileName)
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            true
        } catch (e: IOException) {
            false
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    } catch (e: Exception) {
        false
    }
}