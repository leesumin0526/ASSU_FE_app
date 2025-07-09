package com.example.assu_fe_app.presentation.user.location

import android.view.View
import androidx.fragment.app.FragmentContainerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ItemLocationSpeechBubbleBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class UserLocationSpeechBubbleItemFragment :
    BaseFragment<ItemLocationSpeechBubbleBinding>(R.layout.item_location_speech_bubble) {
    override fun initObserver() {}

    override fun initView() {
        binding.speechBubbleCancle.setOnClickListener{
            requireActivity()
                .findViewById<FragmentContainerView>(R.id.include_speech_bubble)
                .visibility = View.GONE
        }
    }
}