package com.example.assu_fe_app.domain.usecase.partnership

import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class GetProposalAdminListUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(isAll: Boolean): RetrofitResult<List<GetProposalAdminListModel>> {
        return repo.getProposalAdminList(isAll)
    }
}