package com.ssu.assu.domain.usecase.inquiry

import com.ssu.assu.data.repository.inquiry.InquiryRepository
import jakarta.inject.Inject


class GetInquiryDetailUseCase @Inject constructor(
    private val repo: InquiryRepository
) {
    suspend operator fun invoke(id: Long) = repo.get(id)
}