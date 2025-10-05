package com.assu.app.data.dto.partnership.request

data class ContractImageParam(
    val fileName: String,
    val mimeType: String,      // "image/jpeg" 등
    val bytes: ByteArray
)