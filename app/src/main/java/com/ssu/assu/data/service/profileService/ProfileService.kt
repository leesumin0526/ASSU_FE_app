package com.ssu.assu.data.service.profileService

import com.ssu.assu.data.dto.BaseResponse
import com.ssu.assu.data.dto.profileImage.ProfileImageGetResponseDto
import com.ssu.assu.data.dto.profileImage.ProfileImageResponseDto
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileService {

    @Multipart
    @PUT("/members/me/profile-image")
    suspend fun uploadOrReplaceProfileImage(
        @Part image: MultipartBody.Part
    ): BaseResponse<ProfileImageResponseDto>

    @GET("/members/me/profile-image")
    suspend fun getProfileImage(): BaseResponse<ProfileImageGetResponseDto>
}