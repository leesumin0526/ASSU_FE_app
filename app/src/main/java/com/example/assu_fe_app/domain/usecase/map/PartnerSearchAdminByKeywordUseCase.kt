package com.example.assu_fe_app.domain.usecase.map

import com.example.assu_fe_app.data.repository.map.MapRepository
import javax.inject.Inject

class PartnerSearchAdminByKeywordUseCase @Inject
constructor(
    private val mapRepository: MapRepository
) {
    suspend operator fun invoke(keyword: String)
    = mapRepository.searchAdmins(keyword)
}