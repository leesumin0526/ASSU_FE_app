package com.ssu.assu.data.repositoryImpl.profileImage

import com.ssu.assu.data.repository.profileImage.ProfileRepository
import com.ssu.assu.data.service.profileService.ProfileService
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
import jakarta.inject.Inject
import okhttp3.MultipartBody

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileService
) : ProfileRepository {

    override suspend fun uploadOrReplaceProfileImage(imagePart: MultipartBody.Part)
            = apiHandler(
        execute = { api.uploadOrReplaceProfileImage(imagePart) },
        mapper  = { it.toModel() }
    )

    override suspend fun getProfileImageUrl(): RetrofitResult<String> =
        apiHandler(
            execute = { api.getProfileImage() },
            mapper  = { it.url }
        )
}