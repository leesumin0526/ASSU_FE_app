package com.assu.app.data.repository.location

import com.assu.app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.assu.app.data.dto.location.LocationUserSearchResultItem
import com.assu.app.data.dto.location.ViewportQuery
import com.assu.app.domain.model.location.AdminOnMap
import com.assu.app.domain.model.location.PartnerOnMap
import com.assu.app.domain.model.location.StoreOnMap
import com.assu.app.util.RetrofitResult

interface LocationRepository {
    suspend fun getNearbyStores(v: ViewportQuery): RetrofitResult<List<StoreOnMap>>
    suspend fun getNearbyPartners(v: ViewportQuery): RetrofitResult<List<PartnerOnMap>>
    suspend fun getNearbyAdmins(v: ViewportQuery): RetrofitResult<List<AdminOnMap>>

    suspend fun searchStores(keyword: String): RetrofitResult<List<LocationUserSearchResultItem>>
    suspend fun searchPartners(keyword: String): RetrofitResult<List<LocationAdminPartnerSearchResultItem>>
    suspend fun searchAdmins(keyword: String): RetrofitResult<List<LocationAdminPartnerSearchResultItem>>

}