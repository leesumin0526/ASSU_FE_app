package com.example.assu_fe_app.data.repository.location

import com.example.assu_fe_app.presentation.common.search.LocationInfo
import com.example.assu_fe_app.util.RetrofitResult

interface SearchRepository {
    suspend fun searchLocationByKakao(
        keyword: String,
        limit: Int
    ) : RetrofitResult<List<LocationInfo>>
}