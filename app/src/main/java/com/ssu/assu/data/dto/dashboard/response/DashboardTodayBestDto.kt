package com.ssu.assu.data.dto.dashboard.response

import com.ssu.assu.domain.model.dashboard.PopularStoreModel
import com.squareup.moshi.JsonClass

    @JsonClass(generateAdapter = true)
    data class TodayBestDto(
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