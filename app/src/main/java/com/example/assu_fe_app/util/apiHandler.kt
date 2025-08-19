package com.example.assu_fe_app.util

import com.example.assu_fe_app.MyApplication
import retrofit2.Response
import com.example.assu_fe_app.di.ServiceModule

suspend fun <T : Any, R : Any> apiHandler(
    execute: suspend () -> Response<T>,
    mapper: (T) -> R
): RetrofitResult<R> {
//    if (MyApplication.isOnline().not()) {
//        return RetrofitResult.Error(Exception(ServiceModule.NETWORK_EXCEPTION_OFFLINE_CASE))
//    }

    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful) {
            body?.let {
                RetrofitResult.Success(mapper(it))
            } ?: run {
                throw NullPointerException(ServiceModule.NETWORK_EXCEPTION_BODY_IS_NULL)
            }
        } else {
            getFailRetrofitResult(body, response)
        }
    } catch (e: Exception) {
        RetrofitResult.Error(e)
    }
}


private fun <T : Any> getFailRetrofitResult(body: T?, response: Response<T>) = body?.let {
    RetrofitResult.Fail(statusCode = response.code(), message = it.toString())
} ?: run {
    RetrofitResult.Fail(statusCode = response.code(), message = response.message())
}