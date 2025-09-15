package com.example.assu_fe_app.domain.usecase.map

import com.example.assu_fe_app.data.repository.map.SearchRepository
import com.example.assu_fe_app.presentation.common.search.LocationInfo
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class SearchPlaceUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(keyword: String, limit: Int)
    : RetrofitResult<List<LocationInfo>> {
        return repository.searchLocationByKakao(keyword, limit)

    }
}