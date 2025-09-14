package com.example.assu_fe_app.domain.usecase.map

import com.example.assu_fe_app.data.dto.location.LocationUserSearchResultItem
import com.example.assu_fe_app.data.repository.map.MapRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class UserSearchStoreByKeywordUseCase @Inject constructor(
    private val mapRepository: MapRepository
) {
    suspend operator fun invoke(keyword: String)
    : RetrofitResult<List<LocationUserSearchResultItem>> {
        return mapRepository.searchStores(keyword)
    }

}