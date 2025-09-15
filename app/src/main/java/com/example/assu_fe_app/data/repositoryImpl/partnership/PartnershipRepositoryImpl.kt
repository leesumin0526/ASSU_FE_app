package com.example.assu_fe_app.data.repositoryImpl.partnership

import com.example.assu_fe_app.data.dto.partnership.request.CreateDraftRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.WritePartnershipRequestDto
import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.data.service.notification.NotificationService
import com.example.assu_fe_app.data.service.partnership.PartnershipService
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.domain.model.partnership.CreateDraftResponseModel
import com.example.assu_fe_app.domain.model.partnership.PartnershipStatusModel
import com.example.assu_fe_app.domain.model.partnership.WritePartnershipResponseModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import javax.inject.Inject

class PartnershipRepositoryImpl @Inject constructor(
    private val api: PartnershipService,
) : PartnershipRepository {

    override suspend fun createDraftPartnership(
        request: CreateDraftRequestDto
    ): RetrofitResult<CreateDraftResponseModel> {
        return apiHandler (
            { api.createDraftPartnership(request) },
            { dto -> dto.toModel() }
        )
    }

    override suspend fun updatePartnership(
        request: WritePartnershipRequestDto
    ): RetrofitResult<WritePartnershipResponseModel> {
        return apiHandler (
            { api.updatePartnership(request) },
            { dto -> dto.toModel() }
        )
    }

    override suspend fun checkPartnershipAsAdmin(
        partnerId: Long
    ): RetrofitResult<PartnershipStatusModel> {
        return apiHandler (
            { api.checkPartnershipAsAdmin(partnerId) },
            { dto -> dto.toModel() }
        )
    }

    override suspend fun checkPartnershipAsPartner(
        adminId: Long
    ): RetrofitResult<PartnershipStatusModel> {
        return apiHandler (
            { api.checkPartnershipAsPartner(adminId) },
            { dto -> dto.toModel() }
        )
    }

        override suspend fun getProposalPartnerList(isAll: Boolean): RetrofitResult<List<GetProposalPartnerListModel>> {
                return apiHandler(
                        {api.getProposalPartnerList(isAll)},
                        {list -> list.map{it.toModel()}}
                )
        }

        override suspend fun getProposalAdminList(isAll: Boolean): RetrofitResult<List<GetProposalAdminListModel>> {
                return apiHandler(
                        {api.getProposalAdminList(isAll)},
                        {list -> list.map{it.toModel()}}
                )
        }
}