package com.assu.app.domain.usecase.user

import com.assu.app.data.repository.user.UserHomeRepository
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject

class GetStampUseCase @Inject constructor(
    private val repo: UserHomeRepository
){
    suspend operator fun invoke(): RetrofitResult<Int> {
        return repo.getStampCount()
    }
}