package com.example.assu_fe_app.presentation.user.location

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.location.LocationSearchItem
import com.example.assu_fe_app.databinding.FragmentUserLocationSearchRankBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.user.location.adapter.UserLocationSearchRankAdapter

class UserLocationSearchRankFragment :
    BaseFragment<FragmentUserLocationSearchRankBinding>(R.layout.fragment_user_location_search_rank) {
    private lateinit var adapter: UserLocationSearchRankAdapter

    override fun initObserver() {}

    override fun initView() {
        val dummyList = listOf(
            LocationSearchItem("역전할머니맥주 강남점", 1),
            LocationSearchItem("스타벅스 합정점", 2),
            LocationSearchItem("교촌치킨 신촌점", 3),
            LocationSearchItem("세븐일레븐 사당점", 4),
            LocationSearchItem("GS25 홍대입구점", 5)
        )

        adapter = UserLocationSearchRankAdapter(dummyList)
        binding.rvLocationSearchRank.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLocationSearchRank.adapter = adapter

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