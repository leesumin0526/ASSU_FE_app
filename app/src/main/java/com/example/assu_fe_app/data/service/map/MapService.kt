package com.example.assu_fe_app.data.service.map
import com.example.assu_fe_app.data.dto.map.StoreMapResponseDto
import com.example.assu_fe_app.data.dto.map.AdminMapResponseDto
import com.example.assu_fe_app.data.dto.map.PartnerMapResponseDto
import com.example.assu_fe_app.data.dto.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {

    @GET("/map/search")
    suspend fun searchStores(
        @Query("searchKeyword") keyword: String
    ): BaseResponse<List<StoreMapResponseDto>>

    @GET("/map/search") // admin 입장에서 검색한 결과
    suspend fun searchPartners(
        @Query("searchKeyword") keyword: String
    ): BaseResponse<List<PartnerMapResponseDto>>

    // 파트너용 검색 API
    @GET("/map/search") // partner 입장에서 검색한 결과
    suspend fun searchAdmins(
        @Query("searchKeyword") keyword: String
    ): BaseResponse<List<AdminMapResponseDto>>
}