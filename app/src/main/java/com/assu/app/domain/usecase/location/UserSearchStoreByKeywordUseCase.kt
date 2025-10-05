package com.assu.app.domain.usecase.location

import com.assu.app.data.dto.location.LocationUserSearchResultItem
import com.assu.app.data.repository.location.LocationRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class UserSearchStoreByKeywordUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(keyword: String)
    : RetrofitResult<List<LocationUserSearchResultItem>> {
        return locationRepository.searchStores(keyword)
    }

}