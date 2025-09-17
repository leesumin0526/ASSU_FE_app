package com.example.assu_fe_app.domain.usecase.inquiry

import com.example.assu_fe_app.data.repository.inquiry.InquiryRepository
import jakarta.inject.Inject

class GetInquiriesUseCase @Inject constructor(
    private val repo: InquiryRepository
) {
    suspend operator fun invoke(status: String, page: Int, size: Int)
            = repo.list(status, page, size)
}