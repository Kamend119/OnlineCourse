package com.example.onlinecourse.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.ThemePreferences
import com.example.onlinecourse.function.UserPreferences
import com.example.onlinecourse.function.rememberDarkModeState
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun Settings(navController: NavHostController, userId: String, role: String) {
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences(context) }
    val (isDarkMode, setIsDarkMode) = rememberDarkModeState()

    OnlineCursesTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            IconButton(
                onClick = {
                    navController.navigate("main/${userId}/${role}")
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Закрыть",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Настройки",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(
                    Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )

                Text(
                    text = "Профиль",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Button(
                    onClick = {
                        navController.navigate("changingThePassword/$userId/$role")
                    },
                    Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Сменить пароль", style = MaterialTheme.typography.labelMedium)
                }

                if (role != "Администратор") {
                    Spacer(
                        Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )

                    Text(
                        text = "Поддержка",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Button(
                        onClick = {
                            navController.navigate("appealsView/$userId/$role")
                        },
                        Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Обращения", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Spacer(
                    Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )

                Text(
                    text = "Темная тема",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { isChecked ->
                        setIsDarkMode(isChecked)
                        themePreferences.setDarkModeState(isChecked)
                    }
                )

                Spacer(
                    Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Button(
                    onClick = {
                        val userPreferences = UserPreferences(context)
                        userPreferences.deleteUser()
                        navController.navigate("entrance")
                    },
                    Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Выйти из аккаунта", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}