package com.ssu.assu.domain.usecase.inquiry

import com.ssu.assu.data.repository.inquiry.InquiryRepository
import jakarta.inject.Inject

class GetInquiriesUseCase @Inject constructor(
    private val repo: InquiryRepository
) {
    suspend operator fun invoke(status: String, page: Int, size: Int)
            = repo.list(status, page, size)
}