package com.assu.app.data.service.location


import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.location.response.AdminMapResponseDto
import com.assu.app.data.dto.location.response.PartnerMapResponseDto
import com.assu.app.data.dto.location.response.StoreMapResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationService {

    /** STUDENT 토큰 → Store 리스트 */
    @GET("/map/nearby")
    suspend fun getStores(
        @Query("lng1") lng1: Double,
        @Query("lat1") lat1: Double,
        @Query("lng2") lng2: Double,
        @Query("lat2") lat2: Double,
        @Query("lng3") lng3: Double,
        @Query("lat3") lat3: Double,
        @Query("lng4") lng4: Double,
        @Query("lat4") lat4: Double
    ): BaseResponse<List<StoreMapResponseDto>>

    /** ADMIN 토큰 → Partner 리스트 */
    @GET("/map/nearby")
    suspend fun getPartners(
        @Query("lng1") lng1: Double,
        @Query("lat1") lat1: Double,
        @Query("lng2") lng2: Double,
        @Query("lat2") lat2: Double,
        @Query("lng3") lng3: Double,
        @Query("lat3") lat3: Double,
        @Query("lng4") lng4: Double,
        @Query("lat4") lat4: Double
    ): BaseResponse<List<PartnerMapResponseDto>>

    /** PARTNER 토큰 → Admin 리스트 */
    @GET("/map/nearby")
    suspend fun getAdmins(
        @Query("lng1") lng1: Double,
        @Query("lat1") lat1: Double,
        @Query("lng2") lng2: Double,
        @Query("lat2") lat2: Double,
        @Query("lng3") lng3: Double,
        @Query("lat3") lat3: Double,
        @Query("lng4") lng4: Double,
        @Query("lat4") lat4: Double
    ): BaseResponse<List<AdminMapResponseDto>>

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