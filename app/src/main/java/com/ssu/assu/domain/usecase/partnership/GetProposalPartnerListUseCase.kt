package com.ssu.assu.domain.usecase.partnership

import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.domain.model.admin.GetProposalPartnerListModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class GetProposalPartnerListUseCase @Inject constructor(
    private val repo: PartnershipRepository
) {
    suspend operator fun invoke(isAll: Boolean): RetrofitResult<List<GetProposalPartnerListModel>> {
        return repo.getProposalPartnerList(isAll)
    }
}