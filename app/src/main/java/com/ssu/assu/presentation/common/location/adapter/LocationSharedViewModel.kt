package com.ssu.assu.presentation.common.location.adapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssu.assu.data.dto.location.LocationAdminPartnerSearchResultItem

class LocationSharedViewModel : ViewModel() {
    val locationList = MutableLiveData<List<LocationAdminPartnerSearchResultItem>>()
}