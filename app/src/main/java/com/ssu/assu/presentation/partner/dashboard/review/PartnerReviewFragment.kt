package com.ssu.assu.presentation.partner.dashboard.review

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentPartnerReviewBinding
import com.ssu.assu.domain.model.report.ReportTargetType
import com.ssu.assu.domain.model.report.ReportType
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.common.report.OnItemClickListener
import com.ssu.assu.presentation.common.report.OnReportTargetSelectedListener
import com.ssu.assu.presentation.common.report.OnReviewReportCompleteListener
import com.ssu.assu.presentation.common.report.OnReviewReportConfirmedListener
import com.ssu.assu.presentation.partner.PartnerMainActivity
import com.ssu.assu.presentation.user.review.adapter.UserReviewAdapter
import com.ssu.assu.ui.report.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PartnerReviewFragment : BaseFragment<FragmentPartnerReviewBinding>(R.layout.fragment_partner_review),
    OnItemClickListener, OnReportTargetSelectedListener, OnReviewReportConfirmedListener, OnReviewReportCompleteListener {

    private val getReviewViewModel: GetPartnerReviewViewModel by viewModels()
    private val reportViewModel: ReportViewModel by viewModels()
    private lateinit var userReviewAdapter : UserReviewAdapter
    private var selectedItemPosition: Int = -1
    private var isCurrentStudentReport: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        binding.btnCustomerReviewBack.setOnClickListener {
            findNavController().popBackStack()
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
                val count = reviews.size.toString()
                binding.tvReviewStoreReviewCount.text = "${count}개의 평가"
            }
        }

        getReviewViewModel.average.observe(viewLifecycleOwner) { average ->
            val formatted = String.format("%.1f", average)
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

        // ReportViewModel Observer
        reportViewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                val completeDialog = ReviewReportCompleteDialogFragment.newInstance(
                    selectedItemPosition,
                    isCurrentStudentReport
                )
                completeDialog.show(parentFragmentManager, "ReviewReportCompleteDialog")
            }
        }

        reportViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                reportViewModel.clearError()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAdapter() {
        userReviewAdapter = UserReviewAdapter(
            showDeleteButton = false,
            listener = null,
            showReportButton = true,
            reportListener = this
        )

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
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItemPosition == totalItemCount - 1 && !getReviewViewModel.isFetchingReviews) {
                    getReviewViewModel.getReviews()
                }
            }
        })
    }

    override fun onClick(position: Int) {
        selectedItemPosition = position
        val dialog = ReportTargetDialogFragment.newInstance(position)
        dialog.show(parentFragmentManager, "ReportTargetDialog")
    }

    override fun onReportTargetSelected(selectedTarget: String, isStudentReport: Boolean) {
        isCurrentStudentReport = isStudentReport
        val reportDialog = ReviewReportDialogFragment.newInstance(
            selectedItemPosition,
            isStudentReport
        )
        reportDialog.show(parentFragmentManager, "ReviewReportDialog")
    }

    override fun onReviewReportConfirmed(position: Int, reportReason: String) {
        val currentList = userReviewAdapter.currentList
        if (position < currentList.size) {
            val reviewToReport = currentList[position]

            reportViewModel.submitReport(
                targetType = ReportTargetType.REVIEW,
                targetId = reviewToReport.id ?: 0L,
                reportType = ReportType.fromApiValue(reportReason) ?: ReportType.REVIEW_INAPPROPRIATE_CONTENT,
                isStudentReport = isCurrentStudentReport
            )
        }
    }

    override fun onReviewReportComplete(position: Int) {
        val currentList = userReviewAdapter.currentList.toMutableList()

        if (position >= 0 && position < currentList.size) {
            currentList.removeAt(position)
            userReviewAdapter.submitList(currentList)

            binding.tvReviewStoreReviewCount.text = "${currentList.size}개의 평가"

            if (currentList.isEmpty()) {
                findNavController().navigate(R.id.action_partnerReviewFragment_to_partnerReviewNoneFragment)
            }
        }

        selectedItemPosition = -1
    }
}