package com.example.assu_fe_app.data.repository.partnership

import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel
import com.example.assu_fe_app.util.RetrofitResult

interface PartnershipRepository {
    suspend fun getProposalPartnerList(isAll: Boolean): RetrofitResult<List<GetProposalPartnerListModel>>
    suspend fun getProposalAdminList(isAll: Boolean): RetrofitResult<List<GetProposalAdminListModel>>
    suspend fun getPartnership(partnershipId: Long): RetrofitResult<ProposalPartnerDetailsModel>
}