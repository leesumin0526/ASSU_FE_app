package com.assu.app.domain.usecase.inquiry

import com.assu.app.data.repository.inquiry.InquiryRepository
import jakarta.inject.Inject


class GetInquiryDetailUseCase @Inject constructor(
    private val repo: InquiryRepository
) {
    suspend operator fun invoke(id: Long) = repo.get(id)
}