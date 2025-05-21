package com.example.onlinecourse.function

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.io.File


@Composable
fun SavePdfWithDialog(
    context: Context,
    originalFileName: String,
    mimeType: String,
    pdfFile: File?
) {
    val launcher = rememberLauncherForActivityResult(CreateDocument(mimeType)) { uri ->
        if (uri == null) {
            Toast.makeText(context, "Сохранение отменено", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        try {
            if (pdfFile == null || !pdfFile.exists()) {
                Toast.makeText(context, "Файл для сохранения отсутствует", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }
            context.contentResolver.openOutputStream(uri).use { outputStream ->
                pdfFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream!!)
                }
            }
            Toast.makeText(context, "Файл успешно сохранён", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Ошибка при сохранении файла", Toast.LENGTH_SHORT).show()
        }
    }

    Button(onClick = { launcher.launch(originalFileName) }) {
        Text("Скачать сертификат")
    }
}
