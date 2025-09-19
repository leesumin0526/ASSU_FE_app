package com.example.assu_fe_app.data.repository.profileImage

import com.example.assu_fe_app.domain.model.profileImage.ProfileImageModel
import com.example.assu_fe_app.util.RetrofitResult
import okhttp3.MultipartBody

interface ProfileRepository {
    suspend fun uploadOrReplaceProfileImage(imagePart: MultipartBody.Part): RetrofitResult<ProfileImageModel>
    suspend fun getProfileImageUrl(): RetrofitResult<String>
}