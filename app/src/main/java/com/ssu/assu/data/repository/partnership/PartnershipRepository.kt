package com.ssu.assu.data.repository.partnership

import com.ssu.assu.data.dto.partnership.request.CreateDraftRequestDto
import com.ssu.assu.data.dto.partnership.request.WritePartnershipRequestDto
import com.ssu.assu.domain.model.partnership.CreateDraftResponseModel
import com.ssu.assu.domain.model.partnership.PartnershipStatusModel
import com.ssu.assu.domain.model.partnership.WritePartnershipResponseModel
import com.ssu.assu.data.dto.partnership.request.ContractImageParam
import com.ssu.assu.data.dto.partnership.request.ManualPartnershipRequestDto
import com.ssu.assu.domain.model.admin.GetProposalAdminListModel
import com.ssu.assu.domain.model.admin.GetProposalPartnerListModel
import com.ssu.assu.domain.model.partnership.ManualPartnershipModel
import com.ssu.assu.domain.model.partnership.ProposalPartnerDetailsModel
import com.ssu.assu.domain.model.partnership.UpdatePartnershipStatusResponseModel
import com.ssu.assu.domain.model.partnership.SuspendedPaperModel
import com.ssu.assu.util.RetrofitResult

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