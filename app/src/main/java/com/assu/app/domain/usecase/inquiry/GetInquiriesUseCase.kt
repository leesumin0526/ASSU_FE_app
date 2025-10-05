package com.assu.app.domain.usecase.inquiry

import com.assu.app.data.repository.inquiry.InquiryRepository
import jakarta.inject.Inject

class GetInquiriesUseCase @Inject constructor(
    private val repo: InquiryRepository
) {
    suspend operator fun invoke(status: String, page: Int, size: Int)
            = repo.list(status, page, size)
}