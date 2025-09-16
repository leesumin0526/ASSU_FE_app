package com.example.assu_fe_app.domain.usecase.location

import com.example.assu_fe_app.data.dto.location.ViewportQuery
import com.example.assu_fe_app.data.repository.location.LocationRepository
import com.example.assu_fe_app.domain.model.location.StoreOnMap
import com.example.assu_fe_app.util.RetrofitResult
import jakarta.inject.Inject

class GetNearbyStoresUseCase @Inject constructor(
    private val repo: LocationRepository
) {
    suspend operator fun invoke(v: ViewportQuery): RetrofitResult<List<StoreOnMap>> =
        repo.getNearbyStores(v)
}