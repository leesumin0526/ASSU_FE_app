package com.ssu.assu.data.repository.inquiry

import com.ssu.assu.data.dto.inquiry.request.InquiryCreateRequestDto
import com.ssu.assu.domain.model.inquiry.InquiriesPageModel
import com.ssu.assu.domain.model.inquiry.InquiryModel
import com.ssu.assu.util.RetrofitResult

interface InquiryRepository {
    suspend fun create(req: InquiryCreateRequestDto): RetrofitResult<Long>
    suspend fun list(status: String, page: Int, size: Int): RetrofitResult<InquiriesPageModel>
    suspend fun get(id: Long): RetrofitResult<InquiryModel>
}