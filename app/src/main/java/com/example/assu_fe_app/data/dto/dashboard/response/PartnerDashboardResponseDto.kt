package com.example.assu_fe_app.data.dto.dashboard.response

import com.example.assu_fe_app.domain.model.dashboard.PartnerDashboardModel
import com.example.assu_fe_app.domain.model.dashboard.PopularStoreModel
import com.example.assu_fe_app.domain.model.dashboard.StoreInfoModel
import com.example.assu_fe_app.domain.model.dashboard.WeeklyRankModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartnerDashboardResponseDto(
    val storeInfo: StoreInfoDto,
    val weeklyRanks: List<WeeklyRankResponseDto>,
    val todayBest: TodayBestResponseDto,
    val adminStats: AdminDashboardResponseDto? = null
) {
    fun toModel() = PartnerDashboardModel(
        storeInfo = storeInfo.toModel(),
        weeklyRanks = weeklyRanks.map { it.toModel() },
        todayBest = todayBest.toPopularStoreModels(),
        adminStats = adminStats?.toModel()
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