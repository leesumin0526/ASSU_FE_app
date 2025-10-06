package com.ssu.assu.domain.usecase.profileImage

import com.ssu.assu.data.repository.profileImage.ProfileRepository
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class GetProfileImageUseCase @Inject constructor(
    private val repo: ProfileRepository
) {
    suspend operator fun invoke(): RetrofitResult<String> =
        repo.getProfileImageUrl()
}