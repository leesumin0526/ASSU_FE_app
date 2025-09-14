package com.example.assu_fe_app.data.repositoryImpl.partnership

import com.example.assu_fe_app.data.dto.partnership.request.WritePartnershipRequestDto
import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.data.service.partnership.PartnershipService
import com.example.assu_fe_app.domain.model.partnership.WritePartnershipResponseModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import javax.inject.Inject

class PartnershipRepositoryImpl @Inject constructor(
    private val api: PartnershipService,
) : PartnershipRepository {
//    override suspend fun writePartnership(
//        request: WritePartnershipRequestDto
//    ): RetrofitResult<WritePartnershipResponseModel> {
//        return git apiHandler(
//            { api.writePartnership(request) },
//            { dto -> dto.toModel() }
//        )
//    }
}