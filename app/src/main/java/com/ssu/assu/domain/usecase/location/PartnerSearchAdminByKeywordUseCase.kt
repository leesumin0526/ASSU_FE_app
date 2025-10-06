package com.ssu.assu.domain.usecase.location

import com.ssu.assu.data.repository.location.LocationRepository
import javax.inject.Inject

class PartnerSearchAdminByKeywordUseCase @Inject
constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(keyword: String)
    = locationRepository.searchAdmins(keyword)
}