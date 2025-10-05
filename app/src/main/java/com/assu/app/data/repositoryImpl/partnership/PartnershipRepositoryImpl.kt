package com.assu.app.data.repositoryImpl.partnership

import com.assu.app.data.dto.partnership.request.CreateDraftRequestDto
import com.assu.app.data.dto.partnership.request.UpdatePartnershipStatusRequestDto
import com.assu.app.data.dto.partnership.request.WritePartnershipRequestDto
import com.assu.app.data.dto.partnership.request.ContractImageParam
import com.assu.app.data.dto.partnership.request.ManualPartnershipRequestDto
import com.assu.app.data.repository.partnership.PartnershipRepository
import com.assu.app.data.service.partnership.PartnershipService
import com.assu.app.domain.model.admin.GetProposalAdminListModel
import com.assu.app.domain.model.admin.GetProposalPartnerListModel
import com.assu.app.domain.model.partnership.ManualPartnershipModel
import com.assu.app.domain.model.partnership.ProposalPartnerDetailsModel
import com.assu.app.domain.model.partnership.CreateDraftResponseModel
import com.assu.app.domain.model.partnership.PartnershipStatusModel
import com.assu.app.domain.model.partnership.UpdatePartnershipStatusResponseModel
import com.assu.app.domain.model.partnership.WritePartnershipResponseModel
import com.assu.app.domain.model.partnership.SuspendedPaperModel
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
import com.assu.app.util.apiHandlerForUnit
import com.squareup.moshi.Moshi
import jakarta.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class PartnershipRepositoryImpl @Inject constructor(
    private val api: PartnershipService,
    private val moshi: Moshi
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
            mapper = { dto -> dto.toDetailModel() }
        )

    override suspend fun updatePartnershipStatus(
        partnershipId: Long,
        status: String
    ): RetrofitResult<UpdatePartnershipStatusResponseModel> {
        val requestDto = UpdatePartnershipStatusRequestDto(status = status)
        return apiHandler(
            { api.updatePartnershipStatus(partnershipId, requestDto) },
            { dto -> dto.toModel() }
        )
    }


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