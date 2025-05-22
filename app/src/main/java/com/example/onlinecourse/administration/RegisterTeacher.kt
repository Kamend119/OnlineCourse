package com.example.onlinecourse.administration

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.function.isEmailValid
import com.example.onlinecourse.function.isPasswordValid
import com.example.onlinecourse.network.RegisterTeacherViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun RegistrationTeacher(navController: NavHostController, userId: String, role: String) {
    val viewModel: RegisterTeacherViewModel = viewModel()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var patronymic by remember { mutableStateOf("") }

    val isLoading = viewModel.isLoading
    val registrationResult = viewModel.registrationResult

    OnlineCursesTheme {
        AppBar(
            title = "Регистрация",
            showTopBar = true,
            showBottomBar = false,
            navController,
            userId = userId,
            role = role
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    registrationResult?.let { result ->
                        if (result.toLong() > 0) {
                            Toast.makeText(context, "Преподаватель зарегистрирован (ID: $result)", Toast.LENGTH_SHORT).show()
                            viewModel.clearResult()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Ошибка регистрации. Повторите попытку.", Toast.LENGTH_SHORT).show()
                            viewModel.clearResult()
                        }
                    }
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

                            Button(
                                onClick = {
                                    if (email.isBlank() || password.isBlank() || repeatPassword.isBlank() ||
                                        firstName.isBlank() || lastName.isBlank() || patronymic.isBlank() || login.isBlank()
                                    ) {
                                        Toast.makeText(
                                            context,
                                            "Пожалуйста, заполните все поля",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }

                                    if (!isEmailValid(email)) {
                                        Toast.makeText(
                                            context,
                                            "Введите корректную почту",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
                                        Toast.makeText(
                                            context,
                                            "Пароли не совпадают",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }

                                    viewModel.registerTeacher(
                                        login = login,
                                        mail = email,
                                        password = password,
                                        lastName = lastName,
                                        firstName = firstName,
                                        patronymic = patronymic
                                    ) {
                                    }
                                },
                                modifier = Modifier.padding(top = 20.dp)
                            ) {
                                Text(
                                    text = "Зарегистрировать",
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
}