package com.assu.app.data.repository.partnership

import com.assu.app.data.dto.partnership.request.CreateDraftRequestDto
import com.assu.app.data.dto.partnership.request.WritePartnershipRequestDto
import com.assu.app.domain.model.partnership.CreateDraftResponseModel
import com.assu.app.domain.model.partnership.PartnershipStatusModel
import com.assu.app.domain.model.partnership.WritePartnershipResponseModel
import com.assu.app.data.dto.partnership.request.ContractImageParam
import com.assu.app.data.dto.partnership.request.ManualPartnershipRequestDto
import com.assu.app.domain.model.admin.GetProposalAdminListModel
import com.assu.app.domain.model.admin.GetProposalPartnerListModel
import com.assu.app.domain.model.partnership.ManualPartnershipModel
import com.assu.app.domain.model.partnership.ProposalPartnerDetailsModel
import com.assu.app.domain.model.partnership.UpdatePartnershipStatusResponseModel
import com.assu.app.domain.model.partnership.SuspendedPaperModel
import com.assu.app.util.RetrofitResult

interface PartnershipRepository {
    suspend fun createDraftPartnership(request: CreateDraftRequestDto): RetrofitResult<CreateDraftResponseModel>
    suspend fun updatePartnership(request: WritePartnershipRequestDto): RetrofitResult<WritePartnershipResponseModel>
    suspend fun checkPartnershipAsAdmin(partnerId: Long): RetrofitResult<PartnershipStatusModel>
    suspend fun checkPartnershipAsPartner(adminId: Long): RetrofitResult<PartnershipStatusModel>
    suspend fun getProposalPartnerList(isAll: Boolean): RetrofitResult<List<GetProposalPartnerListModel>>
    suspend fun getProposalAdminList(isAll: Boolean): RetrofitResult<List<GetProposalAdminListModel>>
    suspend fun getPartnership(partnershipId: Long): RetrofitResult<ProposalPartnerDetailsModel>
    suspend fun updatePartnershipStatus(partnershipId: Long, status: String): RetrofitResult<UpdatePartnershipStatusResponseModel>
    suspend fun createManualPartnership(req: ManualPartnershipRequestDto, image: ContractImageParam?): RetrofitResult<ManualPartnershipModel>
    suspend fun getSuspendedPapers(): RetrofitResult<List<SuspendedPaperModel>>
    suspend fun deletePartnership(paperId: Long): RetrofitResult<Unit>
}