package com.assu.app.data.repositoryImpl.inquiry

import com.assu.app.data.dto.inquiry.request.InquiryCreateRequestDto
import com.assu.app.data.repository.inquiry.InquiryRepository
import com.assu.app.data.service.inquiry.InquiryService
import com.assu.app.util.apiHandler
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