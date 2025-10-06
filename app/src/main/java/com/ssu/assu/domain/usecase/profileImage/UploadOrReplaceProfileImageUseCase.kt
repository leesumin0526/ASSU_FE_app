package com.ssu.assu.domain.usecase.profileImage

import com.ssu.assu.data.repository.profileImage.ProfileRepository
import com.ssu.assu.domain.model.profileImage.ProfileImageModel
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject
import okhttp3.MultipartBody

class UploadOrReplaceProfileImageUseCase @Inject constructor(
    private val repo: ProfileRepository
) {
    suspend operator fun invoke(imagePart: MultipartBody.Part): RetrofitResult<ProfileImageModel> {
        return repo.uploadOrReplaceProfileImage(imagePart)
    }
}