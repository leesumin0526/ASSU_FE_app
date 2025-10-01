package com.example.assu_fe_app.presentation.common.search


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentLocationInfoSearchBinding
import com.example.assu_fe_app.ui.search.LocationInfoSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LocationInfoSearchFragment : BaseFragment<FragmentLocationInfoSearchBinding>(R.layout.fragment_location_info_search) {

    private lateinit var authTokenLocalStore : AuthTokenLocalStore
    private val searchViewModel: LocationInfoSearchViewModel by activityViewModels()
    private lateinit var adapter: LocationInfoSearchListAdapter

    override fun initObserver() {
        // 검색 결과 관찰
        searchViewModel.locationInfoList.observe(viewLifecycleOwner) { locationInfoList ->
            adapter.submitList(locationInfoList)
        }

        // 로딩 상태 관찰
        searchViewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                "loading" -> showLoading()
                "success" -> hideLoading()
                "error" -> hideLoading()
                else -> hideLoading()
            }
        }
    }

    override fun initView() {

        initAdapter()

        binding.etLocationInfoSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.ivLocationInfoSearchCancle.visibility =
                    if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etLocationInfoSearch.setOnEditorActionListener { v, actionId, event ->
            when {
                actionId == EditorInfo.IME_ACTION_DONE -> {
                    val keyword = v.text.toString().trim()
                    if (keyword.isNotEmpty()) {
                        searchViewModel.searchLocationByKakao(keyword)
                    }
                    true
                }
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER -> {
                    val keyword = v.text.toString().trim()
                    if (keyword.isNotEmpty()) {
                        searchViewModel.searchLocationByKakao(keyword)
                    }
                    true
                }
                else -> false
            }
        }

        binding.fragmentLocationInfoSearchLeftArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 취소 버튼 클릭 시 입력 내용 지우기
        binding.ivLocationInfoSearchCancle.setOnClickListener {
            binding.etLocationInfoSearch.text.clear()
        }
    }

    private fun showLoading(){
        binding.rvLocationInfoSearchResult.visibility = View.INVISIBLE
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.tvLoadingText.text = "검색 중..."
    }

    private fun hideLoading(){
        binding.rvLocationInfoSearchResult.visibility = View.VISIBLE
        binding.loadingOverlay.visibility = View.GONE
    }

    private fun initAdapter(){
        adapter = LocationInfoSearchListAdapter()

        binding.rvLocationInfoSearchResult.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LocationInfoSearchFragment.adapter
        }

        adapter.onItemClick = { clickedLocationInfo ->
            if(arguments?.getString("type") == "passive"){
                val resultBundle = Bundle().apply {
                    putString("selectedPlace", clickedLocationInfo.name)
                    putString("selectedPlace_placeId",     clickedLocationInfo.id)
                    putString("selectedPlace_address",     clickedLocationInfo.address)
                    putString("selectedPlace_roadAddress", clickedLocationInfo.roadAddress)
                    putDouble("selectedPlace_latitude",    clickedLocationInfo.latitude)
                    putDouble("selectedPlace_longitude",   clickedLocationInfo.longitude)
                }
                parentFragmentManager.setFragmentResult("result", resultBundle)
                parentFragmentManager.popBackStack()

            } else {
                val resultBundle = Bundle().apply {
                    putString("selectedAddress", clickedLocationInfo.address)
                    // selectedPlace 객체 생성을 위한 모든 데이터 전달
                    putString("selectedPlaceName", clickedLocationInfo.name)
                    putString("selectedPlaceId", clickedLocationInfo.id)
                    putString("selectedPlaceRoadAddress", clickedLocationInfo.roadAddress)
                    putDouble("selectedPlaceLatitude", clickedLocationInfo.latitude)
                    putDouble("selectedPlaceLongitude", clickedLocationInfo.longitude)
                }
                parentFragmentManager.setFragmentResult("result", resultBundle)
                parentFragmentManager.popBackStack()
            }
        }
    }
}