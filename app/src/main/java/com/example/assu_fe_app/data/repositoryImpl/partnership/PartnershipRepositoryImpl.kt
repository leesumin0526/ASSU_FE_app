package com.example.assu_fe_app.data.repositoryImpl.partnership

import com.example.assu_fe_app.data.dto.partnership.request.ContractImageParam
import com.example.assu_fe_app.data.dto.partnership.request.ManualPartnershipRequestDto
import com.example.assu_fe_app.data.repository.notification.NotificationRepository
import com.example.assu_fe_app.data.repository.partnership.PartnershipRepository
import com.example.assu_fe_app.data.service.notification.NotificationService
import com.example.assu_fe_app.data.service.partnership.PartnershipService
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
import com.example.assu_fe_app.domain.model.partnership.ManualPartnershipModel
import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel
import com.example.assu_fe_app.domain.model.partnership.SuspendedPaperModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import com.example.assu_fe_app.util.apiHandlerForUnit
import com.squareup.moshi.Moshi
import jakarta.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class PartnershipRepositoryImpl @Inject constructor(
    private val api: PartnershipService,
    private val moshi: Moshi
) : PartnershipRepository {
    override suspend fun getProposalPartnerList(isAll: Boolean) : RetrofitResult<List<GetProposalPartnerListModel>> =
        apiHandler(
            {api.getProposalPartnerList(isAll)},
            {list -> list.map{it.toModel()}}
        )

    override suspend fun getProposalAdminList(isAll: Boolean): RetrofitResult<List<GetProposalAdminListModel>> =
        apiHandler(
            {api.getProposalAdminList(isAll)},
            {list -> list.map{it.toModel()}}
        )

    override suspend fun getPartnership(partnershipId: Long)
            : RetrofitResult<ProposalPartnerDetailsModel> =
        apiHandler(
            execute = { api.getPartnership(partnershipId) },
            mapper = { dto -> dto.toModel() }
        )

    private val adapter = moshi.adapter(ManualPartnershipRequestDto::class.java)

    override suspend fun createManualPartnership(
        req: ManualPartnershipRequestDto,
        image: ContractImageParam?
    ): RetrofitResult<ManualPartnershipModel> = apiHandler(
        execute = {
            val json = adapter.toJson(req)
            val requestPart = json.toRequestBody("application/json; charset=utf-8".toMediaType())

            val filePart = image?.let {
                val body = it.bytes.toRequestBody(it.mimeType.toMediaType())
                MultipartBody.Part.createFormData(
                    /* name = */ "contractImage",
                    /* filename = */ it.fileName,
                    /* body = */ body
                )
            }

            api.createManualPartnership(requestPart, filePart)
        },
        mapper = { dto -> dto.toModel() }
    )

    override suspend fun getSuspendedPapers(): RetrofitResult<List<SuspendedPaperModel>> =
        apiHandler(
            execute = { api.getSuspendedPapers() },
            mapper = { dtoList -> dtoList.map { it.toModel() } }
        )

    override suspend fun deletePartnership(paperId: Long): RetrofitResult<Unit> =
        apiHandlerForUnit(
            execute = { api.deletePartnership(paperId) },
            mapper  = { Unit }
        )
}