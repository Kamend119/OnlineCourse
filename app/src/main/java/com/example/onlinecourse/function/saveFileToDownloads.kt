package com.example.onlinecourse.function

import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun saveFileToDownloads(fileName: String, mimeType: String, byteStream: InputStream) {
    try {
        val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsFolder.exists()) {
            downloadsFolder.mkdirs()
        }

        val file = File(downloadsFolder, fileName)
        val outputStream = FileOutputStream(file)

        byteStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}