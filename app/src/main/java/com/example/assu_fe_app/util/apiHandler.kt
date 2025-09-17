package com.example.assu_fe_app.util

import android.util.Log
import com.example.assu_fe_app.MyApplication
import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.di.ServiceModule
import retrofit2.HttpException
import java.io.IOException

suspend fun <T : Any, R : Any> apiHandler(
    execute: suspend () -> BaseResponse<T>,
    mapper: (T) -> R
): RetrofitResult<R> {
    Log.d("apiHandler", "apiHandler 시작")
    
    if (MyApplication.isOnline().not()) {
        Log.e("apiHandler", "네트워크 연결 없음")
        return RetrofitResult.Error(Exception(ServiceModule.NETWORK_EXCEPTION_OFFLINE_CASE))
    }

    return try {
        Log.d("apiHandler", "API 실행 시작")
        val base = execute()
        Log.d("apiHandler", "API 응답 받음: isSuccess=${base.isSuccess}, code=${base.code}, message=${base.message}")

        if (base.isSuccess) {
            val data = base.result
            if (data == null) {
                Log.w("apiHandler", "성공했지만 result가 null")
                // 성공인데 result가 비어 있는 경우를 방어
                RetrofitResult.Fail(
                    statusCode = base.code.toIntOrNull() ?: -1,
                    message = "Empty result"
                )
            } else {
                Log.d("apiHandler", "API 성공, 데이터 매핑 시작")
                RetrofitResult.Success(mapper(data))
            }
        } else {
            Log.e("apiHandler", "API 실패: code=${base.code}, message=${base.message}")
            RetrofitResult.Fail(
                statusCode = base.code.toIntOrNull() ?: -1,
                message = base.message
            )
        }
    } catch (e: HttpException) {
        Log.e("apiHandler", "HttpException 발생: ${e.code()}, ${e.message()}")
        val code = e.code()
        val msg = try { e.response()?.errorBody()?.string() } catch (_: Exception) { e.message() }
        RetrofitResult.Fail(statusCode = code, message = msg ?: "HttpException")
    } catch (e: IOException) {
        Log.e("apiHandler", "IOException 발생: ${e.message}", e)
        RetrofitResult.Error(e) // 네트워크/타임아웃
    } catch (e: Exception) {
        Log.e("apiHandler", "Exception 발생: ${e.message}", e)
        RetrofitResult.Error(e)
    }

}

// Unit 반환 API용 별도 함수
suspend fun <R : Any> apiHandlerForUnit(
    execute: suspend () -> BaseResponse<Any>,
    mapper: (Any?) -> R
): RetrofitResult<R> {
    Log.d("apiHandler", "apiHandlerForUnit 시작")
    
    if (MyApplication.isOnline().not()) {
        Log.e("apiHandler", "네트워크 연결 없음")
        return RetrofitResult.Error(Exception(ServiceModule.NETWORK_EXCEPTION_OFFLINE_CASE))
    }

    return try {
        Log.d("apiHandler", "API 실행 시작")
        val base = execute()
        Log.d("apiHandler", "API 응답 받음: isSuccess=${base.isSuccess}, code=${base.code}, message=${base.message}")

        if (base.isSuccess) {
            val data = base.result
            Log.d("apiHandler", "API 성공, 데이터 매핑 시작")
            RetrofitResult.Success(mapper(data))
        } else {
            Log.e("apiHandler", "API 실패: code=${base.code}, message=${base.message}")
            RetrofitResult.Fail(
                statusCode = base.code.toIntOrNull() ?: -1,
                message = base.message
            )
        }
    } catch (e: HttpException) {
        Log.e("apiHandler", "HttpException 발생: ${e.code()}, ${e.message()}")
        val code = e.code()
        val msg = try { e.response()?.errorBody()?.string() } catch (_: Exception) { e.message() }
        RetrofitResult.Fail(statusCode = code, message = msg ?: "HttpException")
    } catch (e: IOException) {
        Log.e("apiHandler", "IOException 발생: ${e.message}", e)
        RetrofitResult.Error(e) // 네트워크/타임아웃
    } catch (e: Exception) {
        Log.e("apiHandler", "Exception 발생: ${e.message}", e)
        RetrofitResult.Error(e)
    }
}

