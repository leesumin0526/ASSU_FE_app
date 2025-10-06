package com.ssu.assu.data.repositoryImpl.review

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ssu.assu.data.dto.review.response.PageReviewList
import com.ssu.assu.data.dto.review.Review
import com.ssu.assu.data.dto.review.request.ReviewWriteRequestDto
import com.ssu.assu.data.dto.review.response.DeleteReviewResponseDto
import com.ssu.assu.data.dto.review.response.ReviewAverageResponseDto
import com.ssu.assu.data.dto.review.response.ReviewResponseContent
import com.ssu.assu.data.dto.review.response.ReviewWriteResponseDto
import com.ssu.assu.data.repository.review.ReviewRepository
import com.ssu.assu.data.service.review.ReviewService
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime
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

            apiHandler(
                { api.writeReview(requestBody, images) },
                { dto -> dto }
            )
        } catch (e: Exception) {
            RetrofitResult.Error(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getReview(
        page: Int,
        size: Int,
        sort: String
    ): RetrofitResult<PageReviewList> {
        Log.d("Repository✨", "repository is runed")
        return apiHandler(
            {
                Log.d("Repository✨", "api 호출 까지...")
                Log.d(page.toString(), "page")
                api.getReview(page, size, sort) },
            { serverResponse ->
                val mappedContent = serverResponse.content.map { it.toMyReview() }
                PageReviewList(
                    reviews = mappedContent,
                    isLastPage = serverResponse.last // 🚨 서버 응답의 last 필드 사용
                )
            }
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getPartnerReview(
        page: Int,
        size: Int,
        sort: String
    ): RetrofitResult<PageReviewList> {
        Log.d("Repository✨", "repository is runed")
        return apiHandler(
            {
                Log.d("Repository✨", "api 호출 까지...")
                Log.d(page.toString(), "page")
                api.getPartnerReview(page, size, sort) },
            { serverResponse ->
                val mappedContent = serverResponse.content.map { it.toStoreReview() }
                PageReviewList(
                    reviews = mappedContent,
                    isLastPage = serverResponse.last // 🚨 서버 응답의 last 필드 사용
                )
            }
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getStoreReview(
        storeId: Long,
        page: Int,
        size: Int,
        sort: String
    ): RetrofitResult<PageReviewList> {
        return apiHandler(
            {
                Log.d("Repository✨", "api 호출 까지...")
                Log.d(page.toString(), "page")
                api.getStoreReview(storeId, page, size, sort) },
            { serverResponse ->
                val mappedContent = serverResponse.content.map { it.toStoreReview() }
                PageReviewList(
                    reviews = mappedContent,
                    isLastPage = serverResponse.last // 🚨 서버 응답의 last 필드 사용
                )
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun ReviewResponseContent.toMyReview(): Review {
        return Review(
            id = this.reviewId,
            marketName = this.storeName,
            rate = this.rate,
            content = this.content,
            reviewImage = this.reviewImageUrls,
            date = LocalDateTime.parse(this.createdAt)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun ReviewResponseContent.toStoreReview(): Review{
        return Review(
            id = this.reviewId,
            marketName = this.affiliation,
            rate = this.rate,
            content = this.content,
            reviewImage = this.reviewImageUrls,
            date = LocalDateTime.parse(this.createdAt)
        )
    }

    override suspend fun deleteReview(
        reviewId: Long
    ): RetrofitResult<DeleteReviewResponseDto> {
        return try {
            apiHandler(
                {
                    Log.d("Repository✨", "delete api 호출 시작...")
                    val response = api.deleteReview(reviewId)
                    response
                },
                { serverResponse ->
                    Log.d("Repository✨", "delete api transform 실행")
                    serverResponse
                }
            )
        } catch (e: Exception) {
            Log.e("Repository✨", "deleteReview 예외 발생: ${e.message}", e)
            RetrofitResult.Error(e)
        }
    }

    override suspend fun getMyStoreAverage(): RetrofitResult<ReviewAverageResponseDto> {
        return try{
            apiHandler(
                {api.getMyStoreAverageScore()},
                {serverResponse -> serverResponse}
            )
        }
        catch (e: Exception){
            RetrofitResult.Error(e)
        }

    }

    override suspend fun getUserStoreAverage(storeId: Long): RetrofitResult<ReviewAverageResponseDto> {
        return try{
            apiHandler(
                {api.getUserStoreAverageScore(storeId)},
                {serverResponse -> serverResponse}
            )
        }
        catch (e: Exception){
            RetrofitResult.Error(e)
        }

    }

}