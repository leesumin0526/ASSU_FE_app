package com.ssu.assu.data.repository.profileImage

import com.ssu.assu.domain.model.profileImage.ProfileImageModel
import com.ssu.assu.util.RetrofitResult
import okhttp3.MultipartBody

interface ProfileRepository {
    suspend fun uploadOrReplaceProfileImage(imagePart: MultipartBody.Part): RetrofitResult<ProfileImageModel>
    suspend fun getProfileImageUrl(): RetrofitResult<String>
}