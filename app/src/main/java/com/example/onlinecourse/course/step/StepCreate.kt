package com.example.onlinecourse.course.step

import android.content.ContentResolver
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.network.StepCreationViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException


enum class StepType(val displayName: String) {
    LECTURE("Лекция"),
    OPEN_QUESTION("Вопрос без вариантов ответа"),
    MULTIPLE_CHOICE("Вопрос с вариантами ответа"),
    FILE_UPLOAD("Вопрос с приложением")
}

data class AnswerOption(
    var text: String,
    var isCorrect: Boolean,
    var score: String // для удобства ввода в текстовом поле, потом конвертим в Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepCreate(navController: NavHostController, userId: String, role: String, courseId: String, lessonId: String) {
    val context = LocalContext.current
    val viewModel: StepCreationViewModel = viewModel()

    var selectedStepType by remember { mutableStateOf<StepType?>(null) }

    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var sequenceNumber by remember { mutableStateOf("") }
    var obligatory by remember { mutableStateOf(false) }
    var timePasses by remember { mutableStateOf("") }
    var maxScore by remember { mutableStateOf("") }

    val parsedSequence = sequenceNumber.toLongOrNull()
    val parsedMaxScore = maxScore.toLongOrNull()

    var answerOptions by remember { mutableStateOf(mutableListOf<AnswerOption>()) }

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedFileSize by remember { mutableStateOf(0L) }
    var selectedFileMimeType by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            selectedFileName = it.lastPathSegment ?: "Выбран файл"
            val contentResolver = context.contentResolver
            selectedFileMimeType = contentResolver.getType(it) ?: ""
            val cursor = contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                val sizeIndex = c.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (c.moveToFirst() && sizeIndex >= 0) {
                    selectedFileSize = c.getLong(sizeIndex)
                }
            }
        }
    }

    val isCommonValid = name.isNotBlank() && content.isNotBlank() && parsedSequence != null
    val isQuestionValid = timePasses.isNotBlank()
    val isScoredValid = parsedMaxScore != null

    val isFormValid = when (selectedStepType) {
        StepType.LECTURE -> isCommonValid
        StepType.OPEN_QUESTION -> isCommonValid && isQuestionValid
        StepType.MULTIPLE_CHOICE, StepType.FILE_UPLOAD -> isCommonValid && isQuestionValid && isScoredValid
        null -> false
    }

    OnlineCursesTheme {
        AppBar(
            title = "Создание шага",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
            LazyColumn {
                item{
                    Column(modifier = Modifier.padding(16.dp)) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedStepType?.displayName ?: "Выберите тип шага",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                label = { Text("Тип шага") }
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                StepType.values().forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.displayName) },
                                        onClick = {
                                            selectedStepType = type
                                            expanded = false
                                            if (type != StepType.MULTIPLE_CHOICE) {
                                                answerOptions.clear()
                                            }
                                            if (type != StepType.FILE_UPLOAD) {
                                                selectedFileUri = null
                                                selectedFileName = ""
                                                selectedFileSize = 0L
                                                selectedFileMimeType = ""
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (selectedStepType != null) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Название") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = content,
                                onValueChange = { content = it },
                                label = { Text("Описание / Контент") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = sequenceNumber,
                                onValueChange = { if (it.all(Char::isDigit)) sequenceNumber = it },
                                label = { Text("Номер по порядку") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Checkbox(
                                    checked = obligatory,
                                    onCheckedChange = { obligatory = it }
                                )
                                Text("Обязательный шаг", modifier = Modifier.padding(start = 8.dp))
                            }

                            if (selectedStepType != StepType.LECTURE) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = timePasses,
                                    onValueChange = { timePasses = it },
                                    label = { Text("Время выполнения (например, 00:10:00)") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (selectedStepType == StepType.MULTIPLE_CHOICE || selectedStepType == StepType.FILE_UPLOAD) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = maxScore,
                                    onValueChange = { if (it.all(Char::isDigit)) maxScore = it },
                                    label = { Text("Максимальный балл") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (selectedStepType == StepType.MULTIPLE_CHOICE) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Варианты ответа", style = MaterialTheme.typography.titleMedium)

                                answerOptions.forEachIndexed { index, option ->
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = option.text,
                                        onValueChange = { newText ->
                                            answerOptions[index] = option.copy(text = newText)
                                        },
                                        label = { Text("Текст варианта ${index + 1}") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = option.score,
                                        onValueChange = { newScore ->
                                            if (newScore.all { it.isDigit() }) {
                                                answerOptions[index] = option.copy(score = newScore)
                                            }
                                        },
                                        label = { Text("Баллы за вариант") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = option.isCorrect,
                                            onCheckedChange = { checked ->
                                                answerOptions[index] = option.copy(isCorrect = checked)
                                            }
                                        )
                                        Text("Правильный", modifier = Modifier.padding(start = 8.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(onClick = {
                                    answerOptions = answerOptions.toMutableList().apply {
                                        add(AnswerOption(text = "", isCorrect = false, score = "0"))
                                    }
                                }) {
                                    Text("Добавить вариант")
                                }
                            }

                            if (selectedStepType == StepType.FILE_UPLOAD) {
                                Spacer(modifier = Modifier.height(12.dp))

                                Button(onClick = { filePickerLauncher.launch("*/*") }) {
                                    Text(
                                        text = if (selectedFileUri == null)
                                            "Выбрать файл"
                                        else
                                            "Выбран файл: $selectedFileName"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    when (selectedStepType) {
                                        StepType.LECTURE -> {
                                            val obligatoryInt = if (obligatory) 1 else 0
                                            viewModel.createLectureStep(
                                                lessonId = lessonId.toLong(),
                                                name = name,
                                                content = content,
                                                sequenceNumber = parsedSequence!!,
                                                obligatory = obligatoryInt.toString(),
                                                onSuccess = {
                                                    Toast.makeText(context, "Шаг «Лекция» создан", Toast.LENGTH_SHORT).show()
                                                    navController.popBackStack()
                                                }
                                            )
                                        }
                                        StepType.OPEN_QUESTION -> {
                                            val obligatoryInt = if (obligatory) 1 else 0
                                            viewModel.createOpenQuestionStep(
                                                lessonId = lessonId.toLong(),
                                                name = name,
                                                content = content,
                                                sequenceNumber = parsedSequence!!,
                                                timePasses = timePasses,
                                                obligatory = obligatoryInt.toString(),
                                                onSuccess = {
                                                    Toast.makeText(context, "Открытый вопрос создан", Toast.LENGTH_SHORT).show()
                                                    navController.popBackStack()
                                                }
                                            )
                                        }
                                        StepType.MULTIPLE_CHOICE -> {
                                            if (answerOptions.isEmpty()) {
                                                Toast.makeText(context, "Добавьте хотя бы один вариант ответа", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            val optionsValid = answerOptions.all { it.text.isNotBlank() && it.score.toLongOrNull() != null }
                                            if (!optionsValid) {
                                                Toast.makeText(context, "Заполните все варианты корректно", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }

                                            val obligatoryInt = if (obligatory) 1 else 0
                                            viewModel.createMultipleChoiceQuestionStep(
                                                lessonId = lessonId.toLong(),
                                                name = name,
                                                content = content,
                                                sequenceNumber = parsedSequence!!,
                                                timePasses = timePasses,
                                                obligatory = obligatoryInt.toString(),
                                                maxScore = parsedMaxScore!!,
                                                textOptions = answerOptions.map { it.text },
                                                correct = answerOptions.map { it.isCorrect },
                                                scores = answerOptions.map { it.score.toLong() },
                                                onSuccess = {
                                                    Toast.makeText(context, "Вопрос с вариантами создан", Toast.LENGTH_SHORT).show()
                                                    navController.popBackStack()
                                                }
                                            )
                                        }
                                        StepType.FILE_UPLOAD -> {
                                            if (selectedFileUri == null) {
                                                Toast.makeText(context, "Выберите файл", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }

                                            val filePart = selectedFileUri?.let { uri ->
                                                val contentResolver = context.contentResolver
                                                val requestFile = uri.toRequestBody(contentResolver)
                                                MultipartBody.Part.createFormData(
                                                    "file",
                                                    selectedFileName,
                                                    requestFile
                                                )
                                            }

                                            val obligatoryInt = if (obligatory) 1 else 0
                                            viewModel.createFileUploadStep(
                                                lessonId = lessonId.toLong(),
                                                name = name,
                                                content = content,
                                                sequenceNumber = parsedSequence!!,
                                                timePasses = timePasses,
                                                obligatory = obligatoryInt.toString(),
                                                maxScore = parsedMaxScore!!,
                                                originalName = selectedFileName,
                                                mimeType = selectedFileMimeType,
                                                sizeBytes = selectedFileSize,
                                                file = filePart,
                                                onSuccess = {
                                                    Toast.makeText(context, "Шаг с загрузкой файла создан", Toast.LENGTH_SHORT).show()
                                                    navController.popBackStack()
                                                }
                                            )
                                        }
                                        else -> {}
                                    }
                                },
                                enabled = isFormValid,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Создать")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Uri.toRequestBody(contentResolver: ContentResolver): RequestBody {
    return contentResolver.openInputStream(this)?.use { inputStream ->
        val byteArray = inputStream.readBytes()
        byteArray.toRequestBody(null, 0, byteArray.size)
    } ?: throw IOException("Could not open input stream for URI")
}