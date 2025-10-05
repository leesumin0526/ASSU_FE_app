package com.assu.app.data.repositoryImpl.profileImage

import com.assu.app.data.repository.profileImage.ProfileRepository
import com.assu.app.data.service.profileService.ProfileService
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
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