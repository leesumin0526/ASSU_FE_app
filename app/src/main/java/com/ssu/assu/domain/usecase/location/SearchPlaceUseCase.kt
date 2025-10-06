package com.ssu.assu.domain.usecase.location

import com.ssu.assu.data.repository.location.SearchRepository
import com.ssu.assu.presentation.common.search.LocationInfo
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class SearchPlaceUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(keyword: String, limit: Int)
    : RetrofitResult<List<LocationInfo>> {
        return repository.searchLocationByKakao(keyword, limit)

    }
}