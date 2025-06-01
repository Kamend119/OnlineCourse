package com.example.onlinecourse.setting

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.isPasswordValid
import com.example.onlinecourse.network.ChangePasswordViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun ChangingThePassword(navController: NavHostController, userId: String, role: String) {
    val viewModel: ChangePasswordViewModel = viewModel()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val newPassword = viewModel.newPassword.value
    val currentPassword = viewModel.currentPassword.value
    val errorMessage = viewModel.errorMessage.value
    val successMessage = viewModel.successMessage.value
    val isLoading = viewModel.isLoading.value
    val passwordUpdated = viewModel.passwordUpdated.value

    LaunchedEffect(passwordUpdated) {
        if (passwordUpdated && successMessage.isNotEmpty()) {
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            navController.navigate("settings/${userId}/${role}") {
                popUpTo("settings/${userId}/${role}") { inclusive = true }
            }
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    OnlineCursesTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures { focusManager.clearFocus() }
                }
        ) {
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Настройки",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Смена пароля",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { viewModel.currentPasswordChanged(it) },
                        label = { Text("Текущий пароль") },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { viewModel.onNewPasswordChanged(it) },
                        label = { Text("Новый пароль") },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (!isPasswordValid(newPassword)) {
                                Toast.makeText(
                                    context,
                                    "Пароль должен содержать не менее 8 символов, включая строчные и заглавные буквы, а также хотя бы одну цифру",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@Button
                            }

                            viewModel.updateUserPassword(
                                userId = userId.toLong(),
                                currentPassword = currentPassword,
                                newPassword = newPassword
                            )
                        },
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .align(Alignment.CenterHorizontally),
                        enabled = !isLoading
                    ) {
                        Text("Сохранить", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
