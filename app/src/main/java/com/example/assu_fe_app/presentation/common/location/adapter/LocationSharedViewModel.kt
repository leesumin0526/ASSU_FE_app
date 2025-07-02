package com.example.assu_fe_app.presentation.common.location.adapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem

class LocationSharedViewModel : ViewModel() {
    val locationList = MutableLiveData<List<LocationAdminPartnerSearchResultItem>>()
}