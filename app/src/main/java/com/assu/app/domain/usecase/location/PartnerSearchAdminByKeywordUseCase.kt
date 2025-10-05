package com.assu.app.domain.usecase.location

import com.assu.app.data.repository.location.LocationRepository
import javax.inject.Inject

class PartnerSearchAdminByKeywordUseCase @Inject
constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(keyword: String)
    = locationRepository.searchAdmins(keyword)
}