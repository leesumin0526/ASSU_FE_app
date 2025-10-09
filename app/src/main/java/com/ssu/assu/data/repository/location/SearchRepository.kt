package com.ssu.assu.data.repository.location

import com.ssu.assu.presentation.common.search.LocationInfo
import com.ssu.assu.util.RetrofitResult

interface SearchRepository {
    suspend fun searchLocationByKakao(
        keyword: String,
        limit: Int
    ) : RetrofitResult<List<LocationInfo>>
}