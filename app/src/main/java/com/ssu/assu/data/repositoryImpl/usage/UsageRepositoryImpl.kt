package com.ssu.assu.data.repositoryImpl.usage

import android.os.Build
import androidx.annotation.RequiresApi
import com.ssu.assu.data.dto.certification.response.NoneDataResponseDto
import com.ssu.assu.data.dto.usage.SaveUsageRequestDto
import com.ssu.assu.data.dto.usage.response.GetUnreviewedUsageDto
import com.ssu.assu.data.dto.usage.ServiceRecord
import com.ssu.assu.data.dto.usage.response.UserMonthUsageResponseDto
import com.ssu.assu.data.repository.usage.UsageRepository
import com.ssu.assu.data.service.usage.UsageService
import com.ssu.assu.domain.model.usage.MonthUsageModel
import com.ssu.assu.domain.model.usage.UnreviewedModel
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
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
    override suspend fun getUnreviewedUsage(
        page: Int,
        size: Int,
        sort: String
    ): RetrofitResult<UnreviewedModel>{
        return apiHandler({
            api.getUnreviewedUsage(page, size, sort)
        }, {
            dto -> toServiceRecord(dto)
        })
    }

    override suspend fun postUsage(
        request: SaveUsageRequestDto)
    : RetrofitResult<NoneDataResponseDto> {
        return apiHandler(
            {api.postUsage(request)},
            {dto -> dto}
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toServiceRecord(
        dto : GetUnreviewedUsageDto
    ) : UnreviewedModel {
        val serviceRecords: List<ServiceRecord> = dto.content.map{
            content ->
            ServiceRecord(
                id = content.partnershipUsageId,
                adminName = content.adminName,
                marketName = content.storeName,
                serviceContent = content.benefitDescription,
                dateTime = content.usedAt,
                isReviewd = content.reviewed,
                storeId = content.storeId,
                partnerId = content.partnerId

            )
        }
        return UnreviewedModel(
            records = serviceRecords,
            isLastPage = dto.last
        )
    }
}