package com.ssu.assu.domain.usecase.user

import com.ssu.assu.data.repository.user.UserHomeRepository
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class GetStampUseCase @Inject constructor(
    private val repo: UserHomeRepository
){
    suspend operator fun invoke(): RetrofitResult<Int> {
        return repo.getStampCount()
    }
}