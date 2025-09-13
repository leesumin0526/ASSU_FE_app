package com.example.assu_fe_app.data.repository.partnership

import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.util.RetrofitResult

interface PartnershipRepository {
    suspend fun getProposalPartnerList(isAll: Boolean): RetrofitResult<List<GetProposalPartnerListModel>>
}