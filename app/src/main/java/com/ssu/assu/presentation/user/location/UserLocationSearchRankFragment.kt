package com.ssu.assu.presentation.user.location

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssu.assu.R
import com.ssu.assu.data.dto.location.LocationSearchItem
import com.ssu.assu.databinding.FragmentUserLocationSearchRankBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.user.location.adapter.UserLocationSearchRankAdapter
import com.ssu.assu.ui.map.UserLocationSearchViewModel
import kotlin.getValue

class UserLocationSearchRankFragment :
    BaseFragment<FragmentUserLocationSearchRankBinding>(R.layout.fragment_user_location_search_rank) {
    private lateinit var adapter: UserLocationSearchRankAdapter
    private val searchViewModel: UserLocationSearchViewModel by activityViewModels()

    override fun initObserver() {
        searchViewModel.bestStores.observe(this) { bestStores ->
            val locationSearchItems = bestStores.mapIndexed { index, storeName ->
                LocationSearchItem(storeName, index + 1)
            }
            adapter = UserLocationSearchRankAdapter(locationSearchItems)
            binding.rvLocationSearchRank.adapter = adapter
        }
    }

    override fun initView() {
        searchViewModel.getPopularSearch()
        binding.rvLocationSearchRank.layoutManager = LinearLayoutManager(requireContext())

        // 텍스트 스타일 설정
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