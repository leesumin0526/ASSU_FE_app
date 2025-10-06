package com.ssu.assu.presentation.user.location

import android.view.View
import androidx.fragment.app.FragmentContainerView
import com.ssu.assu.R
import com.ssu.assu.databinding.ItemLocationSpeechBubbleBinding
import com.ssu.assu.presentation.base.BaseFragment

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