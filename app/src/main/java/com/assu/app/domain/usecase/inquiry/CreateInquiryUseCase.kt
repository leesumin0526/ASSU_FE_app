package com.assu.app.domain.usecase.inquiry

import com.assu.app.data.dto.inquiry.request.InquiryCreateRequestDto
import com.assu.app.data.repository.inquiry.InquiryRepository
import jakarta.inject.Inject

class CreateInquiryUseCase @Inject constructor(
    private val repo: InquiryRepository
) {
    suspend operator fun invoke(title: String, content: String, email: String)
            = repo.create(InquiryCreateRequestDto(title, content, email))
}