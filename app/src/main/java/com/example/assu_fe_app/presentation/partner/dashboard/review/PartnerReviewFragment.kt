package com.example.assu_fe_app.presentation.partner.dashboard.review

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerReviewBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.partner.PartnerMainActivity
import com.example.assu_fe_app.presentation.user.review.adapter.UserReviewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PartnerReviewFragment : BaseFragment<FragmentPartnerReviewBinding>(R.layout.fragment_partner_review) {

    private val getReviewViewModel: GetPartnerReviewViewModel by viewModels()

    private lateinit var userReviewAdapter : UserReviewAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {

        binding.btnCustomerReviewBack.setOnClickListener {
            (requireActivity() as PartnerMainActivity).showBottomNavigation()
            requireActivity().onBackPressed()
        }
        (requireActivity() as PartnerMainActivity).hideBottomNavigation()

        getReviewViewModel.getAverage()

        initAdapter()
        initScrollListener()
        getReviewViewModel.getReviews()
    }

    override fun initObserver() {
        getReviewViewModel.reviewList.observe(viewLifecycleOwner) { reviews ->
            if (reviews.isNullOrEmpty()) {
                userReviewAdapter.submitList(emptyList())
                findNavController().navigate(R.id.action_partnerReviewFragment_to_partnerReviewNoneFragment)
            } else {
                userReviewAdapter.submitList(reviews)
                val count =reviews.size.toString()
                binding.tvReviewStoreReviewCount.text = "${count}개의 평가"

            }
        }

        getReviewViewModel.average.observe(viewLifecycleOwner) { average ->
            val formatted = String.format("%.1f", average) // "3.1"
            binding.tvCustomerReviewScore.text = formatted

            val rating = average.toInt()
            val stars = listOf(
                binding.ivCustomerReviewStar1,
                binding.ivCustomerReviewStar2,
                binding.ivCustomerReviewStar3,
                binding.ivCustomerReviewStar4,
                binding.ivCustomerReviewStar5
            )


            fun setStars(rating: Int) {
                for (i in stars.indices) {
                    val drawableRes = if (i < rating) R.drawable.ic_activated_star
                    else R.drawable.ic_deactivated_star
                    stars[i].setImageResource(drawableRes)
                }
            }
            setStars(rating)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAdapter() {
        // 삭제 기능이 필요 없으므로 showDeleteButton을 false로 설정하고 listener를 null로 전달합니다.
        userReviewAdapter = UserReviewAdapter(showDeleteButton = false, listener = null)

        binding.rvCustomerReview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userReviewAdapter
        }
    }

    private fun initScrollListener() {
        binding.rvCustomerReview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition =
                    layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItemPosition == totalItemCount - 1 && !getReviewViewModel.isFetchingReviews) {
                    getReviewViewModel.getReviews()
                }
            }
        })
    }



}