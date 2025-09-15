package com.example.assu_fe_app.data.repository.map

import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.data.dto.location.LocationUserSearchResultItem
import com.example.assu_fe_app.data.dto.map.AdminMapResponseDto
import com.example.assu_fe_app.data.dto.map.PartnerMapResponseDto
import com.example.assu_fe_app.data.dto.map.StoreMapResponseDto
import com.example.assu_fe_app.util.RetrofitResult

interface MapRepository {
    suspend fun searchStores(keyword: String): RetrofitResult<List<LocationUserSearchResultItem>>
    suspend fun searchPartners(keyword: String): RetrofitResult<List<LocationAdminPartnerSearchResultItem>>
    suspend fun searchAdmins(keyword: String): RetrofitResult<List<LocationAdminPartnerSearchResultItem>>

}