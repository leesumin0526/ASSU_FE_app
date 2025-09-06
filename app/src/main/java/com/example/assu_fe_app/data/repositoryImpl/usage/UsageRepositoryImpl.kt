package com.example.assu_fe_app.data.repositoryImpl.usage

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.assu_fe_app.data.dto.usage.ServiceRecord
import com.example.assu_fe_app.data.dto.usage.response.UserMonthUsageResponseDto
import com.example.assu_fe_app.data.repository.usage.UsageRepository
import com.example.assu_fe_app.data.service.usage.UsageService
import com.example.assu_fe_app.domain.model.usage.MonthUsageModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class UsageRepositoryImpl @Inject constructor(
    private val api: UsageService
) : UsageRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getMonthUsage(
        year : Int,
        month : Int
    ): RetrofitResult<MonthUsageModel>{
        return apiHandler(
            {
                api.getMonthUsage(year,month)
            },
            { dto ->
                toMonthUsageModel(dto) // 변환 함수 호출
            }

        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toMonthUsageModel(
        response: UserMonthUsageResponseDto
    ): MonthUsageModel {
        val serviceRecords: List<ServiceRecord> = response.details.map { detail ->


            ServiceRecord(
                id = detail.partnershipUsageId,
                adminName = detail.adminName,
                marketName = detail.storeName,
                serviceContent = detail.benefitDescription,
                dateTime = detail.usedAt,
                isReviewd = detail.reviewed,
                storeId = detail.storeId,
                partnerId = detail.partnerId
            )
        }

        // MonthUsageModel 생성 및 반환
        return MonthUsageModel(
            serviceCount = response.serviceCount, // serviceCount를 reviewCount로 매핑
            records = serviceRecords
        )
    }
}