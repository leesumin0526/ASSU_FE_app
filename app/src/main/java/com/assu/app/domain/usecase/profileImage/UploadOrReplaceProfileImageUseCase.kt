package com.assu.app.domain.usecase.profileImage

import com.assu.app.data.repository.profileImage.ProfileRepository
import com.assu.app.domain.model.profileImage.ProfileImageModel
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject
import okhttp3.MultipartBody

class UploadOrReplaceProfileImageUseCase @Inject constructor(
    private val repo: ProfileRepository
) {
    suspend operator fun invoke(imagePart: MultipartBody.Part): RetrofitResult<ProfileImageModel> {
        return repo.uploadOrReplaceProfileImage(imagePart)
    }
}