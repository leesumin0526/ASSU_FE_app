package com.example.assu_fe_app.data.repositoryImpl.partnership

import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.data.service.notification.NotificationService
import com.example.assu_fe_app.data.service.partnership.PartnershipService
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import jakarta.inject.Inject

class PartnershipRepositoryImpl @Inject constructor(
    private val api: PartnershipService
) : PartnershipRepository {
    override suspend fun getProposalPartnerList(isAll: Boolean) : RetrofitResult<List<GetProposalPartnerListModel>> =
        apiHandler(
            {api.getProposalPartnerList(isAll)},
            {list -> list.map{it.toModel()}}
        )
}