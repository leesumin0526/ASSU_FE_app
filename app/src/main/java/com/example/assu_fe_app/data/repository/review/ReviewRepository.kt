package com.example.assu_fe_app.data.repository.review

import com.example.assu_fe_app.data.dto.review.PageReviewList
import com.example.assu_fe_app.data.dto.review.request.ReviewWriteRequestDto
import com.example.assu_fe_app.data.dto.review.response.DeleteReviewResponseDto
import com.example.assu_fe_app.data.dto.review.response.ReviewWriteResponseDto
import com.example.assu_fe_app.util.RetrofitResult
import okhttp3.MultipartBody

interface ReviewRepository {
    suspend fun writeReview(
        request: ReviewWriteRequestDto,
        images: List<MultipartBody.Part>
    ): RetrofitResult<ReviewWriteResponseDto>

    suspend fun getReview(
        page: Int,
        size: Int,
        sort: String
    ) : RetrofitResult<PageReviewList>

    suspend fun getPartnerReview(
        page: Int,
        size: Int,
        sort: String
    ): RetrofitResult<PageReviewList>


    suspend fun deleteReview(
        reviewId: Long
    ): RetrofitResult<DeleteReviewResponseDto>
}