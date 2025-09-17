package com.example.assu_fe_app.domain.usecase.location

import com.example.assu_fe_app.data.repository.location.LocationRepository
import com.example.assu_fe_app.data.service.location.LocationService
import javax.inject.Inject

class AdminSearchPartnerByKeywordUseCase @Inject constructor(
    private val locationRepository : LocationRepository
) {
    suspend operator fun invoke(keyword: String)
    = locationRepository.searchPartners(keyword)

}