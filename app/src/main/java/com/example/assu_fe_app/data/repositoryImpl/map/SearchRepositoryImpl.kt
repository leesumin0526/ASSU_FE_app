package com.example.assu_fe_app.data.repositoryImpl.map

import com.example.assu_fe_app.data.dto.map.SearchPlaceByKakaoDto
import com.example.assu_fe_app.data.repository.map.SearchRepository
import com.example.assu_fe_app.data.service.map.SearchLocationService
import com.example.assu_fe_app.presentation.common.search.LocationInfo
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
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
                {api.searchPlaceByKaKao(keyword, limit)},
                {dto ->toLocationInfo(dto) }
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
                address = dto.address
            )
        }
    }
}