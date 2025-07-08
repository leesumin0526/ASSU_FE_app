package com.example.assu_fe_app.presentation.user.location

import android.content.Intent
import android.view.View
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentLoactionBinding
import com.example.assu_fe_app.databinding.FragmentUserLoactionBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.user.review.store.UserReviewStoreActivity

class UserLocationFragment :
BaseFragment<FragmentUserLoactionBinding>(R.layout.fragment_user_loaction) {

    override fun initView() {
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
            binding.includeSpeechBubble.visibility = View.VISIBLE
            binding.fvUserLocationItem.visibility = View.VISIBLE
        }

        binding.fvUserLocationItem.setOnClickListener {
            val intent = Intent(requireContext(), UserReviewStoreActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initObserver() {
    }

    private fun navigateToSearch() {
        val intent = Intent(requireContext(), UserLocationSearchActivity::class.java)
        startActivity(intent)
    }
}