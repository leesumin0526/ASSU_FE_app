package com.assu.app.domain.usecase.location

import com.assu.app.data.repository.location.SearchRepository
import com.assu.app.presentation.common.search.LocationInfo
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class SearchPlaceUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(keyword: String, limit: Int)
    : RetrofitResult<List<LocationInfo>> {
        return repository.searchLocationByKakao(keyword, limit)

    }
}