package com.example.assu_fe_app.data.service.partnership

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.partnership.request.WritePartnershipRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface PartnershipService {

    // 제휴 제안서 작성 API
    @POST("partnership/proposal")
    suspend fun writePartnership(
        @Body request: WritePartnershipRequestDto
    ): BaseResponse<WritePartnershipResponseDto>

}