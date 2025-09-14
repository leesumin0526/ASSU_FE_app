package com.example.assu_fe_app.util

import com.example.assu_fe_app.MyApplication
import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.di.ServiceModule
import retrofit2.HttpException
import java.io.IOException

suspend fun <T : Any, R : Any> apiHandler(
    execute: suspend () -> BaseResponse<T>,
    mapper: (T) -> R
): RetrofitResult<R> {
    if (MyApplication.isOnline().not()) {
        return RetrofitResult.Error(Exception(ServiceModule.NETWORK_EXCEPTION_OFFLINE_CASE))
    }

    return try {
        val base = execute()

        if (base.isSuccess) {
            val data = base.result
            if (data == null) {
                // 성공인데 result가 비어 있는 경우를 방어
                RetrofitResult.Fail(
                    statusCode = base.code.toIntOrNull() ?: -1,
                    message = "Empty result"
                )
            } else {
                RetrofitResult.Success(mapper(data))
            }
        } else {
            RetrofitResult.Fail(
                statusCode = base.code.toIntOrNull() ?: -1,
                message = base.message
            )
        }
    } catch (e: HttpException) {
        val code = e.code()
        val msg = try { e.response()?.errorBody()?.string() } catch (_: Exception) { e.message() }
        RetrofitResult.Fail(statusCode = code, message = msg ?: "HttpException")
    } catch (e: IOException) {
        RetrofitResult.Error(e) // 네트워크/타임아웃
    } catch (e: Exception) {
        RetrofitResult.Error(e)
    }

}

