package com.example.assu_fe_app.data.service.location

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.location.response.SearchPlaceByKakaoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchLocationService {

    @GET("/map/place")
    suspend fun searchPlaceByKaKao(
        @Query("searchKeyword") keyword: String,
        @Query("limit") limit: Int,)
    : BaseResponse<List<SearchPlaceByKakaoDto>>
}