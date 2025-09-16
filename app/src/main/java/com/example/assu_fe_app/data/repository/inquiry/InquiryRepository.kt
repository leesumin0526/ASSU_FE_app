package com.example.assu_fe_app.data.repository.inquiry

import com.example.assu_fe_app.data.dto.inquiry.request.InquiryCreateRequestDto
import com.example.assu_fe_app.domain.model.inquiry.InquiriesPageModel
import com.example.assu_fe_app.domain.model.inquiry.InquiryModel
import com.example.assu_fe_app.util.RetrofitResult

interface InquiryRepository {
    suspend fun create(req: InquiryCreateRequestDto): RetrofitResult<Long>
    suspend fun list(status: String, page: Int, size: Int): RetrofitResult<InquiriesPageModel>
    suspend fun get(id: Long): RetrofitResult<InquiryModel>
}