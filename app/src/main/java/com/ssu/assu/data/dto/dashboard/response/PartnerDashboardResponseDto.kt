package com.ssu.assu.data.dto.dashboard.response

import com.ssu.assu.domain.model.dashboard.PartnerDashboardModel
import com.ssu.assu.domain.model.dashboard.PopularStoreModel
import com.ssu.assu.domain.model.dashboard.StoreInfoModel
import com.ssu.assu.domain.model.dashboard.WeeklyRankModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnerDashboardResponseDto(
    val storeInfo: StoreInfoDto,
    val weeklyRanks: List<WeeklyRankResponseDto>,
    val todayBest: TodayBestResponseDto
) {
    fun toModel() = PartnerDashboardModel(
        storeInfo = storeInfo.toModel(),
        weeklyRanks = weeklyRanks.map { it.toModel() },
        todayBest = todayBest.toPopularStoreModels()
    )
}

@JsonClass(generateAdapter = true)
data class StoreInfoDto(
    val storeId: Long,
    val storeName: String
) {
    fun toModel() = StoreInfoModel(
        storeId = storeId,
        storeName = storeName
    )
}

@JsonClass(generateAdapter = true)
data class WeeklyRankResponseDto(
    val rank: Long,
    val usageCount: Long
) {
    fun toModel() = WeeklyRankModel(
        rank = rank,
        usageCount = usageCount
    )
}

@JsonClass(generateAdapter = true)
data class TodayBestResponseDto(
    val bestStores: List<String>
) {
    fun toPopularStoreModels(): List<PopularStoreModel> {
        return bestStores.mapIndexed { index, storeName ->
            PopularStoreModel(
                rank = index + 1,
                storeName = storeName,
                isHighlight = index < 3
            )
        }
    }
}