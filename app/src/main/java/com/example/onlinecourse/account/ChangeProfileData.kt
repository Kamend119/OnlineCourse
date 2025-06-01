package com.example.onlinecourse.account

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.onlinecourse.R
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.function.getRealPathFromUri
import com.example.onlinecourse.network.UserProfileViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Composable
fun ChangeProfileData(navController: NavHostController, userId: String, role: String, viewId: String) {
    val viewModel: UserProfileViewModel = viewModel()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var patronymic by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoPath by remember { mutableStateOf<String?>(null) }

    val isLoading = viewModel.isLoading
    val userProfile = viewModel.userProfile
    val fileResponse = viewModel.fileResponse
    val errorMessage = viewModel.errorMessage

    val contextToast = LocalContext.current

    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId.toLong())
    }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            firstName = it.firstName ?: ""
            lastName = it.lastName ?: ""
            email = it.mail ?: ""
            patronymic = it.patronymic ?: ""
            photoPath = it.filePath
            photoUri = null
            photoUri = null
            photoPath?.let { path ->
                viewModel.downloadFile(path)
            }
        }
    }

    LaunchedEffect(viewModel.saveResult) {
        viewModel.saveResult?.let { success ->
            if (success) {
                Toast.makeText(contextToast, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(contextToast, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
            viewModel.clearSaveResult()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
            photoPath = null
        }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Редактировать профиль",
            showTopBar = true,
            showBottomBar = false,
            navController = navController,
            userId = userId,
            role = role
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .imePadding()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { focusManager.clearFocus() })
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val imageBitmap = remember(fileResponse, photoUri) {
                            when {
                                photoUri != null -> null
                                fileResponse?.body() != null -> {
                                    val bytes = fileResponse!!.body()!!.bytes()
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                        ?.asImageBitmap()
                                }

                                else -> null
                            }
                        }

                        if (photoUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(photoUri),
                                contentDescription = "Фото пользователя",
                                modifier = Modifier.size(150.dp)
                            )
                        } else if (imageBitmap != null) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = "Фото пользователя",
                                modifier = Modifier.size(150.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "Аватар",
                                modifier = Modifier.size(150.dp)
                            )
                        }

                        Button(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text("Изменить фото")
                        }

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Фамилия") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("Имя") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = patronymic,
                            onValueChange = { patronymic = it },
                            label = { Text("Отчество") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (lastName.isBlank() || firstName.isBlank() || patronymic.isBlank() || email.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Заполните все обязательные поля",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                var filePart: MultipartBody.Part? = null
                                var originalName: String? = null
                                var mimeType: String? = null
                                var sizeBytes: Long? = null
                                var fileType: String? = null

                                photoUri?.let { uri ->
                                    val filePath = getRealPathFromUri(context, uri)
                                    val file = File(filePath)
                                    if (file.exists()) {
                                        fileType = file.extension
                                        originalName = file.name
                                        mimeType = context.contentResolver.getType(uri)
                                        sizeBytes = file.length()

                                        val requestFile =
                                            file.asRequestBody(mimeType?.toMediaTypeOrNull())
                                        filePart = MultipartBody.Part.createFormData(
                                            "file",
                                            file.name,
                                            requestFile
                                        )
                                    }
                                }

                                viewModel.updateUserProfile(
                                    userId = userId.toLong(),
                                    email = email,
                                    lastName = lastName,
                                    firstName = firstName,
                                    patronymic = patronymic,
                                    fileType = fileType,
                                    originalName = originalName,
                                    mimeType = mimeType,
                                    sizeBytes = sizeBytes,
                                    file = filePart
                                )
                            },
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                        ) {
                            Text("Сохранить")
                        }
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
