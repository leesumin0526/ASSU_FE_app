package com.example.assu_fe_app.domain.usecase.location

import com.example.assu_fe_app.data.dto.location.LocationUserSearchResultItem
import com.example.assu_fe_app.data.repository.location.LocationRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class UserSearchStoreByKeywordUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(keyword: String)
    : RetrofitResult<List<LocationUserSearchResultItem>> {
        return locationRepository.searchStores(keyword)
    }

}