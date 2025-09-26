package com.example.assu_fe_app.data.dto.partnership.request

data class ContractImageParam(
    val fileName: String,
    val mimeType: String,      // "image/jpeg" 등
    val bytes: ByteArray
)