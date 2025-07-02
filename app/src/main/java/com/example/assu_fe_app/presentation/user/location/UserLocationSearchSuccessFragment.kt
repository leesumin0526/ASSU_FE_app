package com.example.assu_fe_app.presentation.user.location

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.data.dto.location.LocationUserSearchResultItem
import com.example.assu_fe_app.databinding.FragmentLocationSearchSuccessBinding
import com.example.assu_fe_app.databinding.FragmentUserLocationSearchSuccessBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.location.adapter.AdminPartnerLocationAdapter
import com.example.assu_fe_app.presentation.user.location.adapter.UserLocationSearchSuccessAdapter

class UserLocationSearchSuccessFragment :
    BaseFragment<FragmentUserLocationSearchSuccessBinding>(R.layout.fragment_user_location_search_success) {

    private lateinit var adapter: UserLocationSearchSuccessAdapter

    override fun initObserver() {}

    override fun initView() {
        val dummyList = listOf(
            LocationUserSearchResultItem("역전할머니맥주 숭실대점", "IT대학", "4인이상 식사시, 음료제공"),
            LocationUserSearchResultItem("역전할머니맥주 숭실대점", "IT대학", "4인이상 식사시, 음료제공"),
            LocationUserSearchResultItem("역전할머니맥주 숭실대점", "IT대학", "4인이상 식사시, 음료제공"),
            LocationUserSearchResultItem("역전할머니맥주 숭실대점", "IT대학", "4인이상 식사시, 음료제공"),
            LocationUserSearchResultItem("역전할머니맥주 숭실대점", "IT대학", "4인이상 식사시, 음료제공"),
            LocationUserSearchResultItem("역전할머니맥주 숭실대점", "IT대학", "4인이상 식사시, 음료제공"),
            LocationUserSearchResultItem("역전할머니맥주 숭실대점", "IT대학", "4인이상 식사시, 음료제공"),
            LocationUserSearchResultItem("역전할머니맥주 숭실대점", "IT대학", "4인이상 식사시, 음료제공"),
            LocationUserSearchResultItem("역전할머니맥주 숭실대점", "IT대학", "4인이상 식사시, 음료제공")
        )
        adapter = UserLocationSearchSuccessAdapter(dummyList)
        binding.rvLocationSearchSuccess.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLocationSearchSuccess.adapter = adapter
    }

}