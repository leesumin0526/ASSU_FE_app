package com.assu.app.data.repository.inquiry

import com.assu.app.data.dto.inquiry.request.InquiryCreateRequestDto
import com.assu.app.domain.model.inquiry.InquiriesPageModel
import com.assu.app.domain.model.inquiry.InquiryModel
import com.assu.app.util.RetrofitResult

interface InquiryRepository {
    suspend fun create(req: InquiryCreateRequestDto): RetrofitResult<Long>
    suspend fun list(status: String, page: Int, size: Int): RetrofitResult<InquiriesPageModel>
    suspend fun get(id: Long): RetrofitResult<InquiryModel>
}