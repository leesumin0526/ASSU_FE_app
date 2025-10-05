package com.assu.app.domain.usecase.partnership

import com.assu.app.data.repository.partnership.PartnershipRepository
import com.assu.app.domain.model.partnership.PartnershipStatusModel
import com.assu.app.util.RetrofitResult
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