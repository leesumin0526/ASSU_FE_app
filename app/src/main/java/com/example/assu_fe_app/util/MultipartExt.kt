package com.example.assu_fe_app.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

fun Uri.toMultipartPart(
    context: Context,
    partName: String = "image",
    fallbackFileName: String = "profile_image"
): MultipartBody.Part {
    val cr: ContentResolver = context.contentResolver

    // 파일명
    val fileName = cr.query(this, null, null, null, null)?.use { cursor ->
        val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIdx >= 0 && cursor.moveToFirst()) cursor.getString(nameIdx) else null
    } ?: fallbackFileName

    // MIME
    val mime = cr.getType(this) ?: "image/*"

    // RequestBody: stream 직접 전달
    val body = object : RequestBody() {
        override fun contentType() = mime.toMediaTypeOrNull()
        override fun writeTo(sink: BufferedSink) {
            cr.openInputStream(this@toMultipartPart)?.use { input ->
                sink.writeAll(input.source())
            }
        }
    }
    return MultipartBody.Part.createFormData(partName, fileName, body)
}