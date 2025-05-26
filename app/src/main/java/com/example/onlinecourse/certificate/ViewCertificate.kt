package com.example.onlinecourse.certificate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.network.CertificatesViewModel
import com.example.onlinecourse.ui.theme.OnlineCursesTheme
import java.io.File
import com.example.onlinecourse.function.PdfCertificateViewer
import com.example.onlinecourse.function.SavePdfWithDialog
import com.example.onlinecourse.function.saveResponseToTempPdfFile

@Composable
fun ViewCertificate(navController: NavHostController, userId: String, role: String, sertificateId: String) {
    val viewModel: CertificatesViewModel = viewModel()
    val isLoading = viewModel.isLoading
    val certificateDetails = viewModel.certificateDetails.firstOrNull()
    val downloadError = viewModel.downloadError

    val context = LocalContext.current

    var pdfFile by remember { mutableStateOf<File?>(null) }
    var isFileLoading by remember { mutableStateOf(false) }

    LaunchedEffect(sertificateId) {
        viewModel.loadCertificateDetails(sertificateId.toLong())
    }

    LaunchedEffect(certificateDetails?.filePath) {
        certificateDetails?.filePath?.let { path ->
            isFileLoading = true
            val response = viewModel.downloadFile(path)
            response?.body()?.byteStream()?.let { stream ->
                pdfFile = saveResponseToTempPdfFile(context, stream)
            }
            isFileLoading = false
        }
    }

    OnlineCursesTheme {
        AppBar(
            title = "Сертификат",
            showTopBar = true,
            showBottomBar = true,
            navController = navController,
            userId = userId,
            role = role
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                when {
                    isLoading -> {
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
                    }

                    downloadError != null -> {
                        Text(
                            text = downloadError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    isFileLoading -> {
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
                    }

                    pdfFile != null -> {
                        PdfCertificateViewer(pdfFile!!)
                        SavePdfWithDialog(
                            context = context,
                            originalFileName = certificateDetails?.originalName ?: "certificate.pdf",
                            mimeType = certificateDetails?.mimeType ?: "application/pdf",
                            pdfFile = pdfFile
                        )
                    }

                    else -> {
                        Text(
                            text = "Не удалось отобразить сертификат",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}


