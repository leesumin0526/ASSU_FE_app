package com.ssu.assu.domain.usecase.location

import com.ssu.assu.data.dto.location.ViewportQuery
import com.ssu.assu.data.repository.location.LocationRepository
import com.ssu.assu.domain.model.location.PartnerOnMap
import com.ssu.assu.util.RetrofitResult
import jakarta.inject.Inject


class GetNearbyPartnersUseCase @Inject constructor(
    private val repo: LocationRepository
) {
    suspend operator fun invoke(v: ViewportQuery): RetrofitResult<List<PartnerOnMap>> =
        repo.getNearbyPartners(v)
}