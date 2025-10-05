package com.assu.app.data.repository.location

import com.assu.app.presentation.common.search.LocationInfo
import com.assu.app.util.RetrofitResult

interface SearchRepository {
    suspend fun searchLocationByKakao(
        keyword: String,
        limit: Int
    ) : RetrofitResult<List<LocationInfo>>
}