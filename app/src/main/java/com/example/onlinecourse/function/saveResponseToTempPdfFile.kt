package com.example.onlinecourse.function

import android.content.Context
import java.io.File
import java.io.InputStream


fun saveResponseToTempPdfFile(context: Context, inputStream: InputStream, fileName: String = "temp_cert.pdf"): File {
    val file = File(context.cacheDir, fileName)
    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return file
}