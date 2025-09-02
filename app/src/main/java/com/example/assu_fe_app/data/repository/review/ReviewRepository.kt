package com.example.assu_fe_app.data.repository.review

import com.example.assu_fe_app.data.dto.review.request.ReviewWriteRequestDto
import com.example.assu_fe_app.data.dto.review.response.ReviewWriteResponseDto
import com.example.assu_fe_app.util.RetrofitResult
import okhttp3.MultipartBody

interface ReviewRepository {
    suspend fun writeReview(
        request: ReviewWriteRequestDto,
        images: List<MultipartBody.Part>
    ): RetrofitResult<ReviewWriteResponseDto>
}