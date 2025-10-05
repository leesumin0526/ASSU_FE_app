package com.assu.app.data.service.profileService

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.profileImage.ProfileImageGetResponseDto
import com.assu.app.data.dto.profileImage.ProfileImageResponseDto
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileService {

    @Multipart
    @PUT("/member/profile/image")
    suspend fun uploadOrReplaceProfileImage(
        @Part image: MultipartBody.Part
    ): BaseResponse<ProfileImageResponseDto>

    @GET("/member/profile/image")
    suspend fun getProfileImage(): BaseResponse<ProfileImageGetResponseDto>
}