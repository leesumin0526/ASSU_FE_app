package com.assu.app.domain.usecase.partnership

import com.assu.app.data.repository.partnership.PartnershipRepository
import com.assu.app.domain.model.admin.GetProposalAdminListModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetProposalAdminListUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(isAll: Boolean): RetrofitResult<List<GetProposalAdminListModel>> {
        return repo.getProposalAdminList(isAll)
    }
}