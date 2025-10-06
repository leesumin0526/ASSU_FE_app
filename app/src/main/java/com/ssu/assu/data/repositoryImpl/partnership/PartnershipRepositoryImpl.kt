package com.ssu.assu.data.repositoryImpl.partnership

import com.ssu.assu.data.dto.partnership.request.CreateDraftRequestDto
import com.ssu.assu.data.dto.partnership.request.UpdatePartnershipStatusRequestDto
import com.ssu.assu.data.dto.partnership.request.WritePartnershipRequestDto
import com.ssu.assu.data.dto.partnership.request.ContractImageParam
import com.ssu.assu.data.dto.partnership.request.ManualPartnershipRequestDto
import com.ssu.assu.data.repository.partnership.PartnershipRepository
import com.ssu.assu.data.service.partnership.PartnershipService
import com.ssu.assu.domain.model.admin.GetProposalAdminListModel
import com.ssu.assu.domain.model.admin.GetProposalPartnerListModel
import com.ssu.assu.domain.model.partnership.ManualPartnershipModel
import com.ssu.assu.domain.model.partnership.ProposalPartnerDetailsModel
import com.ssu.assu.domain.model.partnership.CreateDraftResponseModel
import com.ssu.assu.domain.model.partnership.PartnershipStatusModel
import com.ssu.assu.domain.model.partnership.UpdatePartnershipStatusResponseModel
import com.ssu.assu.domain.model.partnership.WritePartnershipResponseModel
import com.ssu.assu.domain.model.partnership.SuspendedPaperModel
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
import com.ssu.assu.util.apiHandlerForUnit
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