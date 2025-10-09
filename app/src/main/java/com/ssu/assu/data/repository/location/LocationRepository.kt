package com.ssu.assu.data.repository.location

import com.ssu.assu.data.dto.location.LocationAdminPartnerSearchResultItem
import com.ssu.assu.data.dto.location.LocationUserSearchResultItem
import com.ssu.assu.data.dto.location.ViewportQuery
import com.ssu.assu.domain.model.location.AdminOnMap
import com.ssu.assu.domain.model.location.PartnerOnMap
import com.ssu.assu.domain.model.location.StoreOnMap
import com.ssu.assu.util.RetrofitResult

interface LocationRepository {
    suspend fun getNearbyStores(v: ViewportQuery): RetrofitResult<List<StoreOnMap>>
    suspend fun getNearbyPartners(v: ViewportQuery): RetrofitResult<List<PartnerOnMap>>
    suspend fun getNearbyAdmins(v: ViewportQuery): RetrofitResult<List<AdminOnMap>>

    suspend fun searchStores(keyword: String): RetrofitResult<List<LocationUserSearchResultItem>>
    suspend fun searchPartners(keyword: String): RetrofitResult<List<LocationAdminPartnerSearchResultItem>>
    suspend fun searchAdmins(keyword: String): RetrofitResult<List<LocationAdminPartnerSearchResultItem>>

}