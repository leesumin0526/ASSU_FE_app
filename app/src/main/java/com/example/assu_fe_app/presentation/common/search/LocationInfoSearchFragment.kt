package com.example.assu_fe_app.presentation.common.search


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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


@AndroidEntryPoint
class LocationInfoSearchFragment : BaseFragment<FragmentLocationInfoSearchBinding>(R.layout.fragment_location_info_search) {

    private lateinit var authTokenLocalStore : AuthTokenLocalStore
    private val searchViewModel: LocationInfoSearchViewModel by activityViewModels()
    private lateinit var adapter: LocationInfoSearchListAdapter

    override fun initObserver() {
        searchViewModel.locationInfoList.observe(viewLifecycleOwner) { locationInfoList ->
            adapter.submitList(locationInfoList)
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

        binding.etLocationInfoSearch.setOnEditorActionListener { keyword: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val keyword = keyword.text.toString().trim()

                searchViewModel.searchLocationByKakao(keyword)
                true
            } else {
                false
            }
        }

        binding.fragmentLocationInfoSearchLeftArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private fun initAdapter(){
        adapter= LocationInfoSearchListAdapter()
        // authTokenLocalStore는 @Inject로 주입됨

        binding.rvLocationInfoSearchResult.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LocationInfoSearchFragment.adapter
        }
        adapter.onItemClick = { clickedLocationInfo ->
            if(arguments?.getString("type") == "passive"){

                val resultBundle = Bundle().apply {
                    putString("selectedPlace", clickedLocationInfo.name)
                }
                parentFragmentManager.setFragmentResult("result", resultBundle)
                parentFragmentManager.popBackStack()

            } else{
                val resultBundle = Bundle().apply {
                    putString("selectedAddress", clickedLocationInfo.address)
                }
                parentFragmentManager.setFragmentResult("result", resultBundle)
                parentFragmentManager.popBackStack()

            }

        }
    }


}