package com.example.onlinecourse.entrance

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
import com.example.onlinecourse.function.UserPreferences
import com.example.onlinecourse.function.rememberDarkModeStateSystem
import com.example.onlinecourse.network.LoginViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun Authorization(navController: NavHostController) {
    val viewModel: LoginViewModel = viewModel()

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val isLoading = viewModel.isLoading
    val context = LocalContext.current
    val isDarkMode = rememberDarkModeStateSystem()

    OnlineCursesTheme(darkTheme = isDarkMode) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .padding(75.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Авторизация",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
                    OutlinedTextField(
                        value = login,
                        onValueChange = { login = it },
                        label = {
                            Text(
                                text = "Логин",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                text = "Пароль",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .background(MaterialTheme.colorScheme.surface),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Button(
                        onClick = {
                            if (login.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            viewModel.login(login, password) { userId, roleName ->
                                val userPreferences = UserPreferences(context)
                                userPreferences.setUser(userId.toString(), roleName)

                                when (roleName) {
                                    "Студент" -> navController.navigate("test")
                                        //navController.navigate("mainStudent/$userId/$roleName")
                                    else -> Toast.makeText(context, "Роль не распознана", Toast.LENGTH_SHORT).show()
                                }

                                Toast.makeText(context, "Добро пожаловать, $roleName!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(text = "Войти")
                    }

                    viewModel.loginResult?.let { message ->
                        Text(
                            text = message,
                            color = if (message.startsWith("Ошибка")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
