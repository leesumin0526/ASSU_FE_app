package com.ssu.assu.domain.usecase.partnership

import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.domain.model.partnership.PartnershipStatusModel
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject

class CheckPartnershipUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(role: String?, opponentId: Long): RetrofitResult<PartnershipStatusModel> {
        return if (role.equals("ADMIN", ignoreCase = true)) {
            repo.checkPartnershipAsAdmin(partnerId = opponentId)
        } else {
            repo.checkPartnershipAsPartner(adminId = opponentId)
        }
    }
}