package com.example.onlinecourse.function

import android.net.Uri

fun getRealPathFromUri(context: android.content.Context, contentUri: Uri): String {
    var filePath = ""
    val projection = arrayOf(android.provider.MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(contentUri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
            filePath = it.getString(columnIndex)
        }
    }
    return filePath
}