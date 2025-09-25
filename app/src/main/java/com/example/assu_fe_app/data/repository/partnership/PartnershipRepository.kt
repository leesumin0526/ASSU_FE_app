package com.example.assu_fe_app.data.repository.partnership

import com.example.assu_fe_app.data.dto.partnership.request.CreateDraftRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.WritePartnershipRequestDto
import com.example.assu_fe_app.domain.model.partnership.CreateDraftResponseModel
import com.example.assu_fe_app.domain.model.partnership.PartnershipStatusModel
import com.example.assu_fe_app.domain.model.partnership.WritePartnershipResponseModel
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel
import com.example.assu_fe_app.domain.model.partnership.UpdatePartnershipStatusResponseModel
import com.example.assu_fe_app.util.RetrofitResult

interface PartnershipRepository {
    suspend fun createDraftPartnership(request: CreateDraftRequestDto): RetrofitResult<CreateDraftResponseModel>
    suspend fun updatePartnership(request: WritePartnershipRequestDto): RetrofitResult<WritePartnershipResponseModel>
    suspend fun checkPartnershipAsAdmin(partnerId: Long): RetrofitResult<PartnershipStatusModel>
    suspend fun checkPartnershipAsPartner(adminId: Long): RetrofitResult<PartnershipStatusModel>
    suspend fun getProposalPartnerList(isAll: Boolean): RetrofitResult<List<GetProposalPartnerListModel>>
    suspend fun getProposalAdminList(isAll: Boolean): RetrofitResult<List<GetProposalAdminListModel>>
    suspend fun getPartnership(partnershipId: Long): RetrofitResult<ProposalPartnerDetailsModel>
    suspend fun updatePartnershipStatus(partnershipId: Long, status: String): RetrofitResult<UpdatePartnershipStatusResponseModel>
}