package com.assu.app.data.repositoryImpl.location

import com.assu.app.data.dto.location.response.SearchPlaceByKakaoDto
import com.assu.app.data.repository.location.SearchRepository
import com.assu.app.data.service.location.SearchLocationService
import com.assu.app.presentation.common.search.LocationInfo
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api : SearchLocationService
)
    : SearchRepository {
    override suspend fun searchLocationByKakao(
        keyword: String,
        limit: Int
    ): RetrofitResult<List<LocationInfo>> {
        return try{
            apiHandler(
                { api.searchPlaceByKaKao(keyword, limit) },
                { dto -> toLocationInfo(dto) }
            )
        } catch(e: Exception) {
            RetrofitResult.Error(e)
        }
    }

    private fun toLocationInfo(
        dtos: List<SearchPlaceByKakaoDto>
    ) : List<LocationInfo>{
        return dtos.map{ dto ->
            LocationInfo(
                name = dto.name,
                address = dto.address,
                id = dto.placeId,
                latitude = dto.latitude,
                longitude = dto.longitude,
                roadAddress = dto.roadAddress

            )
        }
    }
}