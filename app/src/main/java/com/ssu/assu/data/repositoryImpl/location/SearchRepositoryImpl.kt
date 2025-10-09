package com.ssu.assu.data.repositoryImpl.location

import com.ssu.assu.data.dto.location.response.SearchPlaceByKakaoDto
import com.ssu.assu.data.repository.location.SearchRepository
import com.ssu.assu.data.service.location.SearchLocationService
import com.ssu.assu.presentation.common.search.LocationInfo
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
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