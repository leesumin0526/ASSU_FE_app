package com.ssu.assu.domain.usecase.partnership

import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.domain.model.admin.GetProposalAdminListModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class GetProposalAdminListUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(isAll: Boolean): RetrofitResult<List<GetProposalAdminListModel>> {
        return repo.getProposalAdminList(isAll)
    }
}