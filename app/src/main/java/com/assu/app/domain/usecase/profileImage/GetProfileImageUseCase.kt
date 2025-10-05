package com.assu.app.domain.usecase.profileImage

import com.assu.app.data.repository.profileImage.ProfileRepository
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject

class GetProfileImageUseCase @Inject constructor(
    private val repo: ProfileRepository
) {
    suspend operator fun invoke(): RetrofitResult<String> =
        repo.getProfileImageUrl()
}