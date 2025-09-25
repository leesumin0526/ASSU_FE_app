package com.example.assu_fe_app.data.repository.partnership

import com.example.assu_fe_app.data.dto.partnership.request.ContractImageParam
import com.example.assu_fe_app.data.dto.partnership.request.ManualPartnershipRequestDto
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.domain.model.partnership.ManualPartnershipModel
import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel
import com.example.assu_fe_app.domain.model.partnership.SuspendedPaperModel
import com.example.assu_fe_app.util.RetrofitResult

interface PartnershipRepository {
    suspend fun getProposalPartnerList(isAll: Boolean): RetrofitResult<List<GetProposalPartnerListModel>>
    suspend fun getProposalAdminList(isAll: Boolean): RetrofitResult<List<GetProposalAdminListModel>>
    suspend fun getPartnership(partnershipId: Long): RetrofitResult<ProposalPartnerDetailsModel>
    suspend fun createManualPartnership(req: ManualPartnershipRequestDto, image: ContractImageParam?): RetrofitResult<ManualPartnershipModel>
    suspend fun getSuspendedPapers(): RetrofitResult<List<SuspendedPaperModel>>
    suspend fun deletePartnership(paperId: Long): RetrofitResult<Unit>
}