package com.example.assu_fe_app.data.service.review

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.review.response.DeleteReviewResponseDto
import com.example.assu_fe_app.data.dto.review.response.GetReviewResponseDto
import com.example.assu_fe_app.data.dto.review.response.ReviewWriteResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewService {

    @POST("/reviews")
    @Multipart
    suspend fun writeReview(
        @Part("request") request: RequestBody, // JSON을 RequestBody로 변환해서 보냄
        @Part reviewImages: List<MultipartBody.Part>
    ): BaseResponse<ReviewWriteResponseDto>

    @GET("/reviews/student")
    suspend fun getReview(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): BaseResponse<GetReviewResponseDto>

    @GET("/reviews/partner")
    suspend fun getPartnerReview(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): BaseResponse<GetReviewResponseDto>

    @DELETE("/reviews/{reviewId}")
    suspend fun deleteReview(
        @Path("reviewId") reviewId: Long
    ): BaseResponse<DeleteReviewResponseDto>

}