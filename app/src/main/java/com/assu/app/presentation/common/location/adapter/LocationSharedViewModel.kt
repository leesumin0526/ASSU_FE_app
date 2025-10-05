package com.assu.app.presentation.common.location.adapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.assu.app.data.dto.location.LocationAdminPartnerSearchResultItem

class LocationSharedViewModel : ViewModel() {
    val locationList = MutableLiveData<List<LocationAdminPartnerSearchResultItem>>()
}