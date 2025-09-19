package com.example.assu_fe_app.domain.usecase.profileImage

import com.example.assu_fe_app.data.repository.profileImage.ProfileRepository
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class GetProfileImageUseCase @Inject constructor(
    private val repo: ProfileRepository
) {
    suspend operator fun invoke(): RetrofitResult<String> =
        repo.getProfileImageUrl()
}