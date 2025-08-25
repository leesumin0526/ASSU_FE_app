package com.example.assu_fe_app.util

import com.example.assu_fe_app.MyApplication
import retrofit2.Response
import com.example.assu_fe_app.di.ServiceModule
import retrofit2.HttpException
import java.io.IOException

suspend fun <T : Any, R : Any> apiHandler(
    execute: suspend () -> T,
    mapper: (T) -> R
): RetrofitResult<R> {
    if (MyApplication.isOnline().not()) {
        return RetrofitResult.Error(Exception(ServiceModule.NETWORK_EXCEPTION_OFFLINE_CASE))
    }

    return try {
        val dto = execute()                 // 예: CreateChatRoomResponseDto
        RetrofitResult.Success(mapper(dto)) // 예: dto.toModel()
    } catch (e: HttpException) {
        val code = e.code()
        val msg = try { e.response()?.errorBody()?.string() } catch (_: Exception) { e.message() }
        RetrofitResult.Fail(statusCode = code, message = msg ?: "HttpException")
    } catch (e: IOException) {
        // 네트워크/타임아웃 등
        RetrofitResult.Error(e)
    } catch (e: Exception) {
        RetrofitResult.Error(e)
    }
}


private fun <T : Any> getFailRetrofitResult(body: T?, response: Response<T>) = body?.let {
    RetrofitResult.Fail(statusCode = response.code(), message = it.toString())
} ?: run {
    RetrofitResult.Fail(statusCode = response.code(), message = response.message())
}