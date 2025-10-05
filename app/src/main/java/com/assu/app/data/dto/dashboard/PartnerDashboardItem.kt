package com.assu.app.data.dto.dashboard

class PartnerDashboardItem {
    // UI 표시용 아이템들
    data class RankingChartItem(
        val weekIndex: Int,
        val rank: Long,
        val isImprovement: Boolean = false,
        val isDecline: Boolean = false
    )

    data class UsageChartItem(
        val weekIndex: Int,
        val usageCount: Long,
        val isSelected: Boolean = false
    )

    data class PopularStoreItem(
        val rank: Int,
        val storeName: String,
        val isHighlight: Boolean = false,
        val isMyStore: Boolean = false
    )
}