package com.example.assu_fe_app.domain.usecase.inquiry

import com.example.assu_fe_app.data.dto.inquiry.request.InquiryCreateRequestDto
import com.example.assu_fe_app.data.repository.inquiry.InquiryRepository
import jakarta.inject.Inject

class CreateInquiryUseCase @Inject constructor(
    private val repo: InquiryRepository
) {
    suspend operator fun invoke(title: String, content: String, email: String)
            = repo.create(InquiryCreateRequestDto(title, content, email))
}