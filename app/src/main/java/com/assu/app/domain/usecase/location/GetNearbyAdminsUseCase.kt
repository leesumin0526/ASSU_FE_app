package com.assu.app.domain.usecase.location

import com.assu.app.data.dto.location.ViewportQuery
import com.assu.app.data.repository.location.LocationRepository
import com.assu.app.domain.model.location.AdminOnMap
import com.assu.app.util.RetrofitResult
import jakarta.inject.Inject

class GetNearbyAdminsUseCase @Inject constructor(
    private val repo: LocationRepository
) {
    suspend operator fun invoke(v: ViewportQuery): RetrofitResult<List<AdminOnMap>> =
        repo.getNearbyAdmins(v)
}