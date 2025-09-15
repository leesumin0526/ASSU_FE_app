package com.example.assu_fe_app.domain.usecase.partnership

import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.domain.model.partnership.PartnershipStatusModel
import com.example.assu_fe_app.util.RetrofitResult
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