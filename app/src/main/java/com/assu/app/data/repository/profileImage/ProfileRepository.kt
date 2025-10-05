package com.assu.app.data.repository.profileImage

import com.assu.app.domain.model.profileImage.ProfileImageModel
import com.assu.app.util.RetrofitResult
import okhttp3.MultipartBody

interface ProfileRepository {
    suspend fun uploadOrReplaceProfileImage(imagePart: MultipartBody.Part): RetrofitResult<ProfileImageModel>
    suspend fun getProfileImageUrl(): RetrofitResult<String>
}