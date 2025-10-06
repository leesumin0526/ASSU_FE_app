package com.ssu.assu.domain.usecase.inquiry

import com.ssu.assu.data.dto.inquiry.request.InquiryCreateRequestDto
import com.ssu.assu.data.repository.inquiry.InquiryRepository
import jakarta.inject.Inject

class CreateInquiryUseCase @Inject constructor(
    private val repo: InquiryRepository
) {
    suspend operator fun invoke(title: String, content: String, email: String)
            = repo.create(InquiryCreateRequestDto(title, content, email))
}