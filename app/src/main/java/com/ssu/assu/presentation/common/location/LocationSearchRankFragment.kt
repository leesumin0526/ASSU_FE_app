package com.ssu.assu.presentation.common.location

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssu.assu.R
import com.ssu.assu.data.dto.location.LocationSearchItem
import com.ssu.assu.databinding.FragmentLocationSearchRankBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.common.location.adapter.LocationSearchRankAdapter
import com.ssu.assu.ui.map.AdminPartnerKeyWordSearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationSearchRankFragment :
    BaseFragment<FragmentLocationSearchRankBinding>(R.layout.fragment_location_search_rank) {

    private lateinit var adapter: LocationSearchRankAdapter
    private val viewModel: AdminPartnerKeyWordSearchViewModel by activityViewModels()

    override fun initObserver() {
        viewModel.bestStores.observe(viewLifecycleOwner) { bestStores ->
            val locationSearchItems = bestStores.mapIndexed { index, storeName ->
                LocationSearchItem(storeName, index + 1)
            }
            adapter = LocationSearchRankAdapter(locationSearchItems)
            binding.rvLocationSearchRank.adapter = adapter
        }
    }

    override fun initView() {
        setupRecyclerView()
        setupTitleText()
        viewModel.getPopularSearch()
    }

    private fun setupRecyclerView() {
        binding.rvLocationSearchRank.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupTitleText() {
        val fullText = "🔥 지금 많이 찾는 제휴 매장"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("제휴")
        val end = start + "제휴".length
        val blueColor = ContextCompat.getColor(requireContext(), R.color.assu_main)

        spannable.setSpan(
            ForegroundColorSpan(blueColor),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvLocationSearchRankTitle.text = spannable
    }
}