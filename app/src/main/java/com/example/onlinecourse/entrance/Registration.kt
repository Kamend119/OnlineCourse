package com.example.onlinecourse.entrance

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.onlinecourse.R
import com.example.onlinecourse.function.isEmailValid
import com.example.onlinecourse.function.isPasswordValid
import com.example.onlinecourse.function.isValidAge
import com.example.onlinecourse.function.rememberDarkModeStateSystem
import com.example.onlinecourse.network.RegisterUserViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registration(
    navController: NavHostController
) {
    val viewModel: RegisterUserViewModel = viewModel()
    val focusManager = LocalFocusManager.current

    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var patronymic by remember { mutableStateOf("") }
    var dateBirthday by remember { mutableStateOf("") }
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            dateBirthday = dateFormat.format(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val isLoading = viewModel.isLoading
    val registrationResult = viewModel.registrationResult

    val photoUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri.value = uri
    }

    val isDarkMode = rememberDarkModeStateSystem()

    OnlineCursesTheme(darkTheme = isDarkMode) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(75.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Text(
                    text = "Регистрация",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

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
                    registrationResult?.let {
                        Text(
                            text = it,
                            color = if (it.contains("Ошибка")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = login,
                            onValueChange = { login = it },
                            label = { Text("Логин") },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(MaterialTheme.colorScheme.surface)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Почта") },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(MaterialTheme.colorScheme.surface)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Пароль") },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(MaterialTheme.colorScheme.surface),
                            visualTransformation = PasswordVisualTransformation()
                        )

                        OutlinedTextField(
                            value = repeatPassword,
                            onValueChange = { repeatPassword = it },
                            label = { Text("Повторите пароль") },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(MaterialTheme.colorScheme.surface),
                            visualTransformation = PasswordVisualTransformation()
                        )

                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("Имя") },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(MaterialTheme.colorScheme.surface)
                        )

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Фамилия") },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(MaterialTheme.colorScheme.surface)
                        )

                        OutlinedTextField(
                            value = patronymic,
                            onValueChange = { patronymic = it },
                            label = { Text("Отчество") },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(MaterialTheme.colorScheme.surface)
                        )

                        OutlinedTextField(
                            value = dateBirthday,
                            onValueChange = {},
                            label = { Text("Дата рождения") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.calendar),
                                        contentDescription = "Выберите дату",
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Кнопка выбора фото
                        Button(onClick = {
                            launcher.launch("image/*")
                        }) {
                            Text(text = if (photoUri.value == null) "Загрузить фото" else "Изменить фото")
                        }

                        // Отображение превью фото, если выбрано
                        photoUri.value?.let { uri ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "Фото пользователя",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            )
                        }

                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank() || repeatPassword.isBlank() ||
                                    firstName.isBlank() || lastName.isBlank() || patronymic.isBlank() || dateBirthday.isBlank()
                                ) {
                                    Toast.makeText(context, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (!isEmailValid(email)) {
                                    Toast.makeText(context, "Введите корректную почту", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (!isPasswordValid(password)) {
                                    Toast.makeText(
                                        context,
                                        "Пароль должен содержать не менее 8 символов, включая строчные и заглавные буквы, а также хотя бы одну цифру",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@Button
                                }

                                if (password != repeatPassword) {
                                    Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (!isValidAge(dateBirthday)) {
                                    Toast.makeText(context, "Вам должно быть не менее 14 лет", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                var filePart: MultipartBody.Part? = null
                                var fileType: String? = null
                                var originalName: String? = null
                                var mimeType: String? = null
                                var sizeBytes: String? = null

                                photoUri.value?.let { uri ->
                                    val filePath = getRealPathFromUri(context, uri)
                                    val file = File(filePath)

                                    if (file.exists()) {
                                        fileType = file.extension
                                        originalName = file.name
                                        mimeType = context.contentResolver.getType(uri)
                                        sizeBytes = file.length().toString()

                                        val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
                                        filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                                    }
                                }

                                viewModel.registerUser(
                                    login = login,
                                    email = email,
                                    password = password,
                                    lastName = lastName,
                                    firstName = firstName,
                                    patronymic = patronymic,
                                    fileType = fileType,
                                    originalName = originalName,
                                    mimeType = mimeType,
                                    sizeBytes = sizeBytes,
                                    file = filePart,
                                    onSuccess = {
                                        navController.navigate("test")
                                    }
                                )
                            },
                            modifier = Modifier.padding(top = 20.dp)
                        ) {
                            Text(
                                text = "Зарегистрироваться",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getRealPathFromUri(context: Context, contentUri: Uri): String {
    var filePath = ""
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(contentUri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            filePath = it.getString(columnIndex)
        }
    }
    return filePath
}
