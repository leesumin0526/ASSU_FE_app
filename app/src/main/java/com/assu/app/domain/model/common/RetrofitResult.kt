package com.assu.app.domain.model.common

sealed class RetrofitResult<out T> {
    data class Success<T>(val data: T) : RetrofitResult<T>()
    data class Failure(val message: String, val code: Int? = null) : RetrofitResult<Nothing>()
    object NetworkError : RetrofitResult<Nothing>()
    object Loading : RetrofitResult<Nothing>()
}
