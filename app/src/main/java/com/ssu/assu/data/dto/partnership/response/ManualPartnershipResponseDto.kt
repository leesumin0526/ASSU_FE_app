package com.ssu.assu.data.dto.partnership.response

import com.ssu.assu.domain.model.partnership.ManualPartnershipModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ManualPartnershipResponseDto(
    val storeId: Long,
    val storeCreated: Boolean,
    val storeActivated: Boolean,
    val status: String?,                 // "SUSPEND" 등
    val contractImageUrl: String?,       // S3 presigned url
    val partnership: WritePartnershipResponseDto?     // 상세 결과(서버 DTO에 맞춰 필요 필드만)
) {
    fun toModel() = ManualPartnershipModel(
        storeId = this.storeId,
        storeCreated = this.storeCreated,
        storeActivated = this.storeActivated,
        status = this.status,
        contractImageUrl = this.contractImageUrl
    )
}