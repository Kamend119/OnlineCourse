package com.example.onlinecourse.account

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.R
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.network.UserProfileViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun PageView(navController: NavHostController, userId: String, role: String, viewId: String) {
    val viewModel: UserProfileViewModel = viewModel()
    val isLoading = viewModel.isLoading
    val userProfile = viewModel.userProfile
    val fileResponse = viewModel.fileResponse
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(viewId) {
        viewModel.loadUserProfile(viewId.toLong())
    }

    LaunchedEffect(userProfile?.filePath) {
        userProfile?.filePath?.let { viewModel.downloadFile(it) }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Профиль пользователя",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )
                } else if (errorMessage != null) {
                    Text("Ошибка: $errorMessage", color = MaterialTheme.colorScheme.error)
                } else {
                    val imageBitmap = remember(fileResponse) {
                        try {
                            fileResponse?.body()?.bytes()?.let { byteArray ->
                                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)?.asImageBitmap()
                            }
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "Аватар",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(16.dp)
                                .size(150.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Аватар",
                            modifier = Modifier
                                .padding(16.dp)
                                .size(150.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                        )
                    }

                    if (userProfile != null) {
                        Text(
                            "Имя: ${userProfile.firstName}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (userProfile != null) {
                        Text(
                            "Фамилия: ${userProfile.lastName}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (userProfile != null) {
                        Text(
                            "Почта: ${userProfile.mail}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    if (viewId == userId) {
                        Button(
                            onClick = {
                                navController.navigate("changeProfileData/${userId}/${role}/${viewId}")
                            },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                "Редактировать",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    if (role == "Студент") {
                        Button(
                            onClick = {
                                navController.navigate("viewYourCertificates/${userId}/${role}")
                            },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                "Мои сертификаты",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    if (role == "Администратор" && viewId != userId) {
                        Button(
                            onClick = {
                                viewModel.issueWarning(viewId.toLong())
                            },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                "Выдать предупреждение",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.deleteUser(viewId.toLong()) {
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                "Удалить пользователя",
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