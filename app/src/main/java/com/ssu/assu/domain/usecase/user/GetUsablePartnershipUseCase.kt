package com.ssu.assu.domain.usecase.user

import com.ssu.assu.data.repository.user.UserHomeRepository
import com.ssu.assu.domain.model.user.GetUsablePartnershipModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class GetUsablePartnershipUseCase @Inject constructor(
    private val repo: UserHomeRepository
) {
    suspend operator fun invoke(
        all: Boolean
    ): RetrofitResult<List<GetUsablePartnershipModel>> {
        return repo.getUsablePartnership(all)

    }
}