package com.assu.app.domain.usecase.partnership

import com.assu.app.data.repository.partnership.PartnershipRepository
import com.assu.app.domain.model.admin.GetProposalPartnerListModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetProposalPartnerListUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(isAll: Boolean): RetrofitResult<List<GetProposalPartnerListModel>> {
        return repo.getProposalPartnerList(isAll)
    }
}