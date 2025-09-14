package com.example.assu_fe_app.presentation.common.location

import android.util.Log
import android.view.View
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.databinding.ItemLocationBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class LocationItemFragment :
    BaseFragment<ItemLocationBinding>(R.layout.item_location) {
    override fun initObserver() {}
    override fun initView() {}

    fun showCapsuleInfo(item: LocationAdminPartnerSearchResultItem) {
        binding.tvAdminPartnerLocationAddressDate.text = item.name

        if (item.isPartnered) {
            binding.ivAdminPartnerLocationCapsule.visibility = View.VISIBLE
            binding.tvAdminPartnerLocationCapsuleText.visibility = View.VISIBLE
            binding.tvAdminPartnerLocationCapsuleText.text = item.term
        } else {
            binding.ivAdminPartnerLocationCapsule.visibility = View.GONE
            binding.tvAdminPartnerLocationCapsuleText.visibility = View.GONE
            binding.tvAdminPartnerLocationAddressDate.text = item.address
        }
    }
}