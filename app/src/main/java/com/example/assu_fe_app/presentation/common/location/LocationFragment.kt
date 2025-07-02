package com.example.assu_fe_app.presentation.common.location

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.databinding.FragmentLoactionBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.location.adapter.AdminPartnerLocationAdapter
import com.example.assu_fe_app.presentation.common.location.adapter.LocationSharedViewModel
import com.example.assu_fe_app.presentation.user.review.store.UserReviewStoreActivity

class LocationFragment :
BaseFragment<FragmentLoactionBinding>(R.layout.fragment_loaction) {
    private val sharedViewModel: LocationSharedViewModel by activityViewModels()
    private lateinit var adapter: AdminPartnerLocationAdapter
    private var currentItem: LocationAdminPartnerSearchResultItem? = null

    override fun initView() {
        val dummyList = listOf(
            LocationAdminPartnerSearchResultItem("역전할머니맥주 숭실대점1", "서울 동작구 사당로 36-1 서정캐슬", true, "2025.02.24 ~ 2025.06.15"),
            LocationAdminPartnerSearchResultItem("역전할머니맥주 숭실대점2", "서울 동작구 사당로 36-1 서정캐슬", false, "")
        )
        sharedViewModel.locationList.value = dummyList
        adapter = AdminPartnerLocationAdapter(dummyList)

        binding.viewLocationSearchBar.setOnClickListener {
            navigateToSearch()
        }
        binding.ivLocationSearchIc.setOnClickListener {
            navigateToSearch()
        }
        binding.tvLocationHint.setOnClickListener {
            navigateToSearch()
        }

        binding.viewLocationMap.setOnClickListener{
            binding.fvLocationItem.visibility = View.VISIBLE
        }

        binding.fvLocationItem.setOnClickListener {
            val intent = Intent(requireContext(), UserReviewStoreActivity::class.java)
            startActivity(intent)
        }

        sharedViewModel.locationList.observe(viewLifecycleOwner) { list ->
            Log.d("ViewModel", "list size = ${list?.size}")
            list.forEachIndexed { index, item ->
                Log.d("ViewModel", "$index: ${item.shopName}")
            }

            currentItem = list.getOrNull(1)
        }

        sharedViewModel.locationList.observe(viewLifecycleOwner) { list ->
            val item = list.getOrNull(1) ?: return@observe

            val fragment = childFragmentManager.findFragmentById(R.id.fv_location_item) as? LocationItemFragment
            fragment?.showCapsuleInfo(item)
        }
    }

    override fun initObserver() {

    }

    private fun navigateToSearch() {
        val intent = Intent(requireContext(), LocationSearchActivity::class.java)
        startActivity(intent)
    }
}