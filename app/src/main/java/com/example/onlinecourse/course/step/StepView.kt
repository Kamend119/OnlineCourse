package com.example.onlinecourse.course.step

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import okhttp3.MultipartBody
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import com.example.onlinecourse.network.StepDetailResponse
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import com.example.onlinecourse.function.TimePickerWithHMS
import com.example.onlinecourse.function.getFileName
import com.example.onlinecourse.function.uriToMultipartBody
import com.example.onlinecourse.network.EditableAnswerOption
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun StepView(navController: NavHostController, userId: String, role: String, courseId: String, lessonId: String, stepId: String){
    val viewModel: LessonStepAnswerViewModel = viewModel()
    val context = LocalContext.current

    val stepDetails = viewModel.stepDetails.firstOrNull()
    val isLoading = viewModel.isLoading

    LaunchedEffect(stepId) {
        viewModel.getStepDetails(stepId.toLong(), userId.toLong())
    }

    OnlineCursesTheme {
        AppBar(
            title = "Просмотр шага",
            showTopBar = true,
            showBottomBar = true,
            navController = navController,
            userId = userId,
            role = role
        ) {
            LazyColumn {
                item {
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
                    }else {
                        when (role) {
                            "Учитель" -> {
                                stepDetails?.let { StepEditTeacherView(it, viewModel, context, userId.toLong(), stepId.toLong(), navController) }
                            }
                            "Студент" -> {
                                stepDetails?.let { StudentStepView(it, viewModel, context, userId.toLong(), stepId.toLong(), navController) }
                            }
                            "Администратор" -> {
                                stepDetails?.let { AdministratorStepView(it, viewModel, context, userId.toLong(), stepId.toLong(), navController) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun StepEditTeacherView( step: StepDetailResponse, viewModel: LessonStepAnswerViewModel, context: Context, userId: Long, stedId: Long, navController: NavHostController) {
    var name by remember { mutableStateOf(step.stepName ?: "") }
    var content by remember { mutableStateOf(step.stepContent ?: "") }
    var sequenceNumber by remember { mutableStateOf(step.sequenceNumber.toString()) }
    var timePasses by remember { mutableStateOf(step.timePasses ?: "00:00:00") }
    var maxScore by remember { mutableStateOf(step.maxScore.toString()) }
    var obligatory by remember { mutableStateOf(step.obligatory) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fileUri = uri
    }
    var showDialog by remember { mutableStateOf(false) }

    val updateResult by viewModel.updateResult.collectAsState()
    val deleteResult by viewModel.deleteResult.collectAsState()

    var downloadedFileBytes by remember { mutableStateOf<ByteArray?>(null) }
    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                downloadedFileBytes?.let { outputStream.write(it) }
            }
            Toast.makeText(context, "Файл сохранён", Toast.LENGTH_SHORT).show()
        }
    }
    val answerOptionsState = remember {
        mutableStateOf(
            step.answerOptions?.map {
                EditableAnswerOption(
                    text = it.text,
                    isCorrect = it.correct,
                    score = it.score.toString()
                )
            }?.toMutableList() ?: mutableListOf()
        )
    }
    LaunchedEffect(step.taskFilePath) {
        if (!step.taskFilePath.isNullOrEmpty()) {
            viewModel.downloadFile(step.taskFilePath,
                onSuccess = { responseBody ->
                    downloadedFileBytes = responseBody.bytes()
                },
                onFailure = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    LaunchedEffect(updateResult) {
        updateResult?.let {
            Toast.makeText(context, if (it) "Шаг успешно обновлён" else "Не удалось обновить шаг", Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateResult()
            if (it) {
                navController.popBackStack()
            }
        }
    }
    LaunchedEffect(deleteResult) {
        deleteResult?.let {
            Toast.makeText(context, if (it) "Шаг удалён" else "Не удалось удалить шаг", Toast.LENGTH_SHORT).show()
            viewModel.resetDeleteResult()
            if (it) {
                navController.popBackStack()
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteStep(stedId, userId)
                    showDialog = false
                }) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена")
                }
            },
            title = { Text("Удалить шаг") },
            text = { Text("Вы уверены, что хотите удалить шаг?") }
        )
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Название шага") },
            modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Контент") },
            modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = sequenceNumber,
            onValueChange = { if (it.isEmpty() || it.all(Char::isDigit)) sequenceNumber = it },
            label = { Text("Порядковый номер") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        if (step.stepTypeName != "Лекция") {
            if (step.stepTypeName != "Вопрос с приложением"){
                TimePickerWithHMS(
                    timePasses = timePasses,
                    onTimeSelected = { newTime -> timePasses = newTime }
                )
            }
            OutlinedTextField(
                value = maxScore,
                onValueChange = { if (it.isEmpty() || it.all(Char::isDigit)) maxScore = it },
                label = { Text("Максимальный балл") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth())
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = obligatory, onCheckedChange = { obligatory = it })
            Text("Обязательный шаг")
        }

        if (step.stepTypeName == "Вопрос с вариантами ответа") {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Варианты ответа", style = MaterialTheme.typography.titleMedium)

            answerOptionsState.value.forEachIndexed { index, option ->
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = option.text,
                    onValueChange = { newText ->
                        answerOptionsState.value = answerOptionsState.value.toMutableList().apply {
                            this[index] = this[index].copy(text = newText)
                        }
                    },
                    label = { Text("Текст варианта ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = option.score,
                    onValueChange = { newScore ->
                        if (newScore.all { it.isDigit() }) {
                            answerOptionsState.value = answerOptionsState.value.toMutableList().apply {
                                this[index] = this[index].copy(score = newScore)
                            }
                        }
                    },
                    label = { Text("Баллы за вариант") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = option.isCorrect,
                        onCheckedChange = { checked ->
                            answerOptionsState.value = answerOptionsState.value.toMutableList().apply {
                                this[index] = this[index].copy(isCorrect = checked)
                            }
                        }
                    )
                    Text("Правильный", modifier = Modifier.padding(start = 8.dp))

                    Spacer(modifier = Modifier.weight(1f))

                    Button(onClick = {
                        answerOptionsState.value = answerOptionsState.value.toMutableList().apply {
                            removeAt(index)
                        }
                    }) {
                        Text("Удалить")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                answerOptionsState.value = answerOptionsState.value.toMutableList().apply {
                    add(EditableAnswerOption(text = "", isCorrect = false, score = "0"))
                }
            }) {
                Text("Добавить вариант")
            }
        }

        if (step.stepTypeName == "Вопрос с приложением") {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Файл задания:")
            if (fileUri != null) {
                val fileName = getFileName(fileUri!!, context.contentResolver) ?: "Выбран файл"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(fileName)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("(будет загружен)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            } else if (!step.taskFileName.isNullOrBlank()) {
                Column(Modifier.padding(10.dp)) {
                    Text(step.taskFileName)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        viewModel.downloadFile(step.taskFilePath ?: "", onSuccess = { body ->
                            val bytes = body.bytes()
                            downloadedFileBytes = bytes
                            saveFileLauncher.launch(step.taskFileName ?: "file")
                        })
                    }) {
                        Text("Скачать")
                    }
                }
            }

            Button(onClick = { launcher.launch("*/*") }) {
                Text(if (step.taskFileName != null) "Изменить файл" else "Добавить файл")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            when (step.stepTypeName) {
                "Лекция" -> {
                    if (name.isBlank() || content.isBlank() || sequenceNumber.isBlank()) {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.updateLectureStep(
                        stepId = stedId,
                        name = name,
                        content = content,
                        sequenceNumber = sequenceNumber.toLong(),
                        obligatory = obligatory
                    )
                }
                "Вопрос без вариантов ответа" -> {
                    if (name.isBlank() || content.isBlank() || sequenceNumber.isBlank() || sequenceNumber.isBlank() || timePasses.isBlank() || maxScore.isBlank()) {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.updateOpenQuestionStep(
                        stepId = stedId,
                        name = name,
                        content = content,
                        sequenceNumber = sequenceNumber.toLong(),
                        timePasses = timePasses,
                        obligatory = obligatory,
                        maxScore = maxScore.toLong()
                    )
                }
                "Вопрос с вариантами ответа" -> {
                    if (name.isBlank() || content.isBlank() || sequenceNumber.isBlank() || sequenceNumber.isBlank() || timePasses.isBlank() || maxScore.isBlank() || answerOptionsState.value.isEmpty() || answerOptionsState.value.any { it.text.isBlank() }) {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.updateMultipleChoiceQuestionStep(
                        stepId = stedId,
                        name = name,
                        content = content,
                        sequenceNumber = sequenceNumber.toLong(),
                        timePasses = timePasses,
                        obligatory = obligatory,
                        maxScore = maxScore.toLong(),
                        textOptions = answerOptionsState.value.map { it.text },
                        correct = answerOptionsState.value.map { it.isCorrect },
                        scores = answerOptionsState.value.map { it.score.toLongOrNull() ?: 0L }
                    )
                }
                "Вопрос с приложением" -> {
                    if (name.isBlank() || content.isBlank() || sequenceNumber.isBlank() || sequenceNumber.isBlank() || timePasses.isBlank() || maxScore.isBlank()) {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (fileUri != null) {
                        val inputStream = context.contentResolver.openInputStream(fileUri!!)
                        val fileBytes = inputStream?.readBytes()
                        val fileName = getFileName(fileUri!!, context.contentResolver) ?: "file"
                        val file = MultipartBody.Part.createFormData(
                            "file", fileName,
                            fileBytes!!.toRequestBody("*/*".toMediaTypeOrNull())
                        )
                        viewModel.updateFileUploadQuestionStep(
                            stepId = stedId,
                            name = name,
                            content = content,
                            sequenceNumber = sequenceNumber.toLong(),
                            obligatory = obligatory,
                            maxScore = maxScore.toLong(),
                            originalName = fileName,
                            mimeType = "*/*",
                            sizeBytes = fileBytes.size.toLong(),
                            file = file
                        )
                    } else if (downloadedFileBytes != null) {
                        val fileName = step.taskFileName ?: "file"
                        val file = MultipartBody.Part.createFormData(
                            "file", fileName,
                            downloadedFileBytes!!.toRequestBody("*/*".toMediaTypeOrNull())
                        )
                        viewModel.updateFileUploadQuestionStep(
                            stepId = stedId,
                            name = name,
                            content = content,
                            sequenceNumber = sequenceNumber.toLong(),
                            obligatory = obligatory,
                            maxScore = maxScore.toLong(),
                            originalName = fileName,
                            mimeType = "*/*",
                            sizeBytes = downloadedFileBytes!!.size.toLong(),
                            file = file
                        )
                    } else {
                        Toast.makeText(context, "Ошибка: файл отсутствует", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }) {
            Text("Сохранить")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Удалить")
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun StudentStepView( stepDetail: StepDetailResponse, viewModel: LessonStepAnswerViewModel, context: Context, userId: Long, stedId: Long, navController: NavHostController) {
    val isEditable = stepDetail.userScore == null
    var answerText by remember { mutableStateOf(stepDetail.userAnswerText ?: "") }
    var comment by remember { mutableStateOf(stepDetail.userCommentStudent ?: "") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedOptionIds by remember {
        mutableStateOf(stepDetail.selectedOptions?.map { it.id }?.toMutableSet() ?: mutableSetOf<Long>())
    }
    val answerResult by viewModel.answerResult.collectAsState()
    LaunchedEffect(answerResult) {
        answerResult?.let {
            if (it) {
                Toast.makeText(context, "Ответ сохранен", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Произошла ошибка при отправке", Toast.LENGTH_SHORT).show()
            }
            viewModel.resetAnswerResult()
        }
    }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fileUri = uri
    }
    var downloadedFileBytes by remember { mutableStateOf<ByteArray?>(null) }
    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                downloadedFileBytes?.let { outputStream.write(it) }
            }
            Toast.makeText(context, "Файл сохранён", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Название: ${stepDetail.stepName}", style = MaterialTheme.typography.titleMedium)
        Text("Контент: ${stepDetail.stepContent}", modifier = Modifier.padding(top = 8.dp))
        Text("Обязательный: ${if (stepDetail.obligatory) "Да" else "Нет"}", modifier = Modifier.padding(top = 8.dp))

        if (stepDetail.stepTypeName != "Лекция") {
            Text("Максимальный балл: ${stepDetail.maxScore}", modifier = Modifier.padding(top = 8.dp))
        }

        when (stepDetail.stepTypeName) {
            "Вопрос без вариантов ответа" -> {
                OutlinedTextField(
                    value = answerText,
                    onValueChange = { answerText = it },
                    label = { Text("Ответ") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isEditable
                )
            }
            "Вопрос с вариантами ответа" -> {
                stepDetail.answerOptions?.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isEditable) {
                                selectedOptionIds = if (selectedOptionIds.contains(option.id)) {
                                    (selectedOptionIds - option.id).toMutableSet()
                                } else {
                                    (selectedOptionIds + option.id).toMutableSet()
                                }
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = selectedOptionIds.contains(option.id),
                            onCheckedChange = { checked ->
                                if (isEditable) {
                                    selectedOptionIds = if (checked) {
                                        (selectedOptionIds + option.id).toMutableSet()
                                    } else {
                                        (selectedOptionIds - option.id).toMutableSet()
                                    }
                                }
                            }
                        )
                        Text(option.text, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
            "Вопрос с приложением" -> {
                Text("Файл задания: ${stepDetail.taskFileName ?: "нет"}", modifier = Modifier.padding(top = 8.dp))
                Button(onClick = {
                    viewModel.downloadFile(stepDetail.taskFilePath ?: "", onSuccess = { body ->
                        val bytes = body.bytes()
                        downloadedFileBytes = bytes
                        saveFileLauncher.launch(stepDetail.taskFileName ?: "file")
                    })
                }) {
                    Text("Скачать")
                }

                if (isEditable) {
                    if (fileUri != null) {
                        val fileName =
                            getFileName(fileUri!!, context.contentResolver) ?: "Выбран файл"
                        Row(Modifier.padding(10.dp)) {
                            Text(fileName)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "(будет загружен)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                    Button(onClick = { launcher.launch("*/*") }) {
                        Text(if (stepDetail.answerFileName != null || fileUri != null) "Изменить файл" else "Добавить файл")
                    }
                }
                else {
                    Text("Файл ответа: ${stepDetail.answerFileName ?: "нет"}", modifier = Modifier.padding(top = 8.dp))
                    Button(onClick = {
                        viewModel.downloadFile(stepDetail.answerFilePath ?: "", onSuccess = { body ->
                            val bytes = body.bytes()
                            downloadedFileBytes = bytes
                            saveFileLauncher.launch(stepDetail.answerFileName ?: "file")
                        })
                    }) {
                        Text("Скачать")
                    }
                }
            }
        }

        if (stepDetail.stepTypeName != "Лекция") {
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Комментарий") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                enabled = isEditable
            )

            stepDetail.userCommentTeacher?.let {
                if (!stepDetail.userCommentTeacher.isBlank()) {
                    Text("Комментарий от преподавателя: $it", modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            stepDetail.userScore?.let {
                Text("Оценка: $it", modifier = Modifier.padding(top = 4.dp))
            }

            if (isEditable) {
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Отправить")
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false

                    when (stepDetail.stepTypeName) {
                        "Вопрос без вариантов ответа" -> {
                            if (answerText.isBlank() || comment.isBlank()) {
                                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                                return@TextButton
                            }

                            if (stepDetail.userAnswerId != null) {
                                viewModel.updateLessonStepAnswer(
                                    answerId = stepDetail.userAnswerId,
                                    userId = userId,
                                    stepLessonId = stedId,
                                    answerText = answerText,
                                    commentStudent = comment
                                )
                            } else {
                                viewModel.answerLessonStep(
                                    userId = userId,
                                    stepLessonId = stedId,
                                    answerText = answerText,
                                    commentStudent = comment
                                )
                            }
                        }
                        "Вопрос с вариантами ответа" -> {
                            if (selectedOptionIds.isEmpty() || comment.isBlank()) {
                                Toast.makeText(context, "Выберите хотя бы один вариант и заполните комментарий", Toast.LENGTH_SHORT).show()
                                return@TextButton
                            }

                            if (stepDetail.userAnswerId != null) {
                                viewModel.updateLessonStepAnswer(
                                    answerId = stepDetail.userAnswerId,
                                    userId = userId,
                                    stepLessonId = stedId,
                                    selectedOptionIds = selectedOptionIds.toList(),
                                    commentStudent = comment
                                )
                            } else {
                                viewModel.answerLessonStep(
                                    userId = userId,
                                    stepLessonId = stedId,
                                    selectedOptionIds = selectedOptionIds.toList(),
                                    commentStudent = comment
                                )
                            }
                        }
                        "Вопрос с приложением" -> {
                            val inputStream = context.contentResolver.openInputStream(fileUri!!)
                            val fileBytes = inputStream?.readBytes()
                            val fileName = getFileName(fileUri!!, context.contentResolver) ?: "file"
                            val file = fileUri?.let { uriToMultipartBody(it, context) }
                            if (file == null || comment.isBlank()) {
                                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                                return@TextButton
                            }

                            if (stepDetail.userAnswerId != null) {
                                viewModel.updateLessonStepAnswer(
                                    answerId = stepDetail.userAnswerId,
                                    userId = userId,
                                    stepLessonId = stedId,
                                    commentStudent = comment,
                                    originalName = fileName,
                                    mimeType = "*/*",
                                    sizeBytes = fileBytes!!.size.toLong(),
                                    file = file
                                )
                            } else {
                                viewModel.answerLessonStep(
                                    userId = userId,
                                    stepLessonId = stedId,
                                    commentStudent = comment,
                                    originalName = fileName,
                                    mimeType = "*/*",
                                    sizeBytes = fileBytes!!.size.toLong(),
                                    file = file
                                )
                            }
                        }
                    }
                }) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Отмена")
                }
            },
            title = { Text("Подтверждение") },
            text = { Text("Вы уверены, что хотите отправить ответ?") }
        )
    }
}

@Composable
fun AdministratorStepView( stepDetail: StepDetailResponse, viewModel: LessonStepAnswerViewModel, context: Context, userId: Long, stedId: Long, navController: NavHostController) {
    var showWarningDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val deleteResult by viewModel.deleteResult.collectAsState()
    val warnResult by viewModel.warnResult.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri: Uri? ->
        uri?.let {
            viewModel.downloadFile(stepDetail.taskFilePath ?: "", onSuccess = { body ->
                try {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(body.bytes())
                    }
                    Toast.makeText(context, "Файл сохранён", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Ошибка записи: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }, onFailure = { errorMsg ->
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            })
        }
    }

    LaunchedEffect(deleteResult) {
        deleteResult?.let {
            Toast.makeText(context, if (it) "Шаг удалён" else "Не удалось удалить шаг", Toast.LENGTH_SHORT).show()
            viewModel.resetDeleteResult()
            if (it) {
                navController.popBackStack()
            }
        }
    }

    LaunchedEffect(warnResult) {
        warnResult?.let {
            Toast.makeText(context, if (it) "Предупреждение выдано" else "Произошла ошибка", Toast.LENGTH_SHORT).show()
            viewModel.resetWarnResult()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Название: ${stepDetail.stepName}", style = MaterialTheme.typography.titleMedium)
        Text("Контент: ${stepDetail.stepContent}", modifier = Modifier.padding(top = 8.dp))
        Text("Обязательный: ${if (stepDetail.obligatory) "Да" else "Нет"}", modifier = Modifier.padding(top = 8.dp))

        if (stepDetail.stepTypeName != "Лекция") {
            Text("Максимальный балл: ${stepDetail.maxScore}", modifier = Modifier.padding(top = 8.dp))
        }

        when (stepDetail.stepTypeName) {
            "Вопрос с вариантами ответа" -> {
                Text("Варианты ответа:", modifier = Modifier.padding(top = 8.dp))
                stepDetail.answerOptions?.forEach { option ->
                    Text("• ${option.text}", modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                }
            }
            "Вопрос с приложением" -> {
                Text(
                    "Файл задания: ${stepDetail.taskFileName ?: "нет"}",
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(onClick = {
                    val filename = stepDetail.taskFileName ?: "task_file"
                    launcher.launch(filename)
                }, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Скачать файл")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { showWarningDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Выдать предупреждение")
            }
            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Удалить")
            }
        }
    }

    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            title = { Text("Подтверждение") },
            text = { Text("Вы уверены, что хотите выдать предупреждение?") },
            confirmButton = {
                TextButton(onClick = {
                    showWarningDialog = false
                    viewModel.warnOnStep(stedId)
                }) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWarningDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Подтверждение") },
            text = { Text("Вы уверены, что хотите удалить этот шаг?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteStep(stedId, userId)
                }) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
