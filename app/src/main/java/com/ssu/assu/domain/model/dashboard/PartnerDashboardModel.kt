package com.ssu.assu.domain.model.dashboard


data class PartnerDashboardModel(
    val storeInfo: StoreInfoModel,
    val weeklyRanks: List<WeeklyRankModel>,
    val todayBest: List<PopularStoreModel>,
    val adminStats: AdminDashboardModel? = null
) {
    fun getCurrentWeekRank(): Long = weeklyRanks.lastOrNull()?.rank ?: 0L
    fun getCurrentWeekUsage(): Long = weeklyRanks.lastOrNull()?.usageCount ?: 0L
    fun getRankingTrend(): List<Long> = weeklyRanks.map { it.rank }
    fun getUsageTrend(): List<Long> = weeklyRanks.map { it.usageCount }

    fun isRankImproving(): Boolean {
        if (weeklyRanks.size < 2) return false
        val currentRank = weeklyRanks.last().rank
        val previousRank = weeklyRanks[weeklyRanks.size - 2].rank
        return currentRank < previousRank
    }

    fun getAnalysisText(weekIndex: Int): String {
        val usage = weeklyRanks.getOrNull(weekIndex)?.usageCount ?: 0L
        val weekLabel = getWeekLabel(weekIndex, weeklyRanks.size)
        return "${weekLabel}에 숭실대학교 학생 ${usage}명이 매장에서 제휴 서비스를 이용했어요"
    }

    fun getTodayAnalysisText(): String {
        val todayUsage = adminStats?.todayUsagePersonCount ?: 0L
        return "오늘 ${todayUsage}명의 학생이 제휴 서비스를 이용했어요"
    }

    fun getPartnershipGrowthText(): String {
        val total = adminStats?.totalStudentCount ?: 0L
        val newCount = adminStats?.newStudentCount ?: 0L
        return "현재 ${total}명의 학생과 제휴 중이며, 이번 달 ${newCount}명이 새로 가입했어요"
    }

    private fun getWeekLabel(weekIndex: Int, totalWeeks: Int): String {
        val weeksAgo = (totalWeeks - 1) - weekIndex
        return when (weeksAgo) {
            0 -> "이번 주"
            1 -> "지난 주"
            else -> "${weeksAgo}주 전"
        }
    }
}

data class StoreInfoModel(
    val storeId: Long,
    val storeName: String
)

data class WeeklyRankModel(
    val rank: Long,
    val usageCount: Long
)

data class PopularStoreModel(
    val rank: Int,
    val storeName: String,
    val isHighlight: Boolean = false
)
