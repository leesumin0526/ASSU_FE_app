package com.example.assu_fe_app.data.repositoryImpl.review

import com.example.assu_fe_app.data.dto.review.request.ReviewWriteRequestDto
import com.example.assu_fe_app.data.dto.review.response.ReviewWriteResponseDto
import com.example.assu_fe_app.data.repository.review.ReviewRepository
import com.example.assu_fe_app.data.service.review.ReviewService
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val api: ReviewService
) : ReviewRepository {

    override suspend fun writeReview(
        request: ReviewWriteRequestDto,
        images: List<MultipartBody.Part>
    ): RetrofitResult<ReviewWriteResponseDto> {
        return try {
            // DTO → JSON → RequestBody 변환
            val json = Gson().toJson(request)
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
//            val requestPart = MultipartBody.Part.createFormData(
//                "request",
//                null, // 파일이 아니므로 파일명은 null
//                json.toRequestBody("application/json".toMediaTypeOrNull())
//            )
            apiHandler(
                { api.writeReview(requestBody, images) },
                { dto -> dto }
            )
        } catch (e: Exception) {
            RetrofitResult.Error(e)
        }
    }

}