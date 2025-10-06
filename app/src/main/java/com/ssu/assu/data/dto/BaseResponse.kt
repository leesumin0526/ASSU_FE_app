package com.ssu.assu.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T?
)