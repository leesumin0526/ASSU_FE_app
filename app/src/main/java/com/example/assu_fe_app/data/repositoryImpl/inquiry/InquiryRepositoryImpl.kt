package com.example.assu_fe_app.data.repositoryImpl.inquiry

import com.example.assu_fe_app.data.dto.inquiry.request.InquiryCreateRequestDto
import com.example.assu_fe_app.data.repository.inquiry.InquiryRepository
import com.example.assu_fe_app.data.service.inquiry.InquiryService
import com.example.assu_fe_app.util.apiHandler
import jakarta.inject.Inject

class InquiryRepositoryImpl @Inject constructor(
    private val api: InquiryService
) : InquiryRepository {

    override suspend fun create(req: InquiryCreateRequestDto) =
        apiHandler({ api.create(req) }) { it } // Long 그대로

    override suspend fun list(status: String, page: Int, size: Int) =
        apiHandler({ api.list(status, page, size) }) { it.toModel() }

    override suspend fun get(id: Long) =
        apiHandler({ api.get(id) }) { it.toModel() }
}