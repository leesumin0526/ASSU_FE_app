package com.example.assu_fe_app.data.repositoryImpl.profileImage

import com.example.assu_fe_app.data.repository.profileImage.ProfileRepository
import com.example.assu_fe_app.data.service.profileService.ProfileService
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
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