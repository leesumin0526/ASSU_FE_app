package com.ssu.assu.domain.usecase.location

import com.ssu.assu.data.dto.location.LocationUserSearchResultItem
import com.ssu.assu.data.repository.location.LocationRepository
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class UserSearchStoreByKeywordUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(keyword: String)
    : RetrofitResult<List<LocationUserSearchResultItem>> {
        return locationRepository.searchStores(keyword)
    }

}