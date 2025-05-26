package com.example.onlinecourse.function

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


fun uriToMultipartBody(uri: Uri, context: Context): MultipartBody.Part? {
    val contentResolver = context.contentResolver
    val fileName = getFileName(uri, contentResolver) ?: return null

    val inputStream = contentResolver.openInputStream(uri) ?: return null
    val tempFile = File(context.cacheDir, fileName)
    tempFile.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    val requestFile = tempFile.asRequestBody(getMimeType(uri, contentResolver).toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("file", fileName, requestFile)
}

fun getFileName(uri: Uri, contentResolver: ContentResolver): String? {
    var name: String? = null
    val returnCursor = contentResolver.query(uri, null, null, null, null)
    returnCursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst() && nameIndex >= 0) {
            name = it.getString(nameIndex)
        }
    }
    return name
}

fun getMimeType(uri: Uri, contentResolver: ContentResolver): String {
    return contentResolver.getType(uri) ?: "application/octet-stream"
}

