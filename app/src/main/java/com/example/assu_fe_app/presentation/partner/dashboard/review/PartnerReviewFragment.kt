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
import com.example.assu_fe_app.presentation.common.report.OnItemClickListener
import com.example.assu_fe_app.presentation.common.report.OnReportTargetSelectedListener
import com.example.assu_fe_app.presentation.common.report.OnReviewReportCompleteListener
import com.example.assu_fe_app.presentation.common.report.OnReviewReportConfirmedListener
import com.example.assu_fe_app.presentation.partner.PartnerMainActivity
import com.example.assu_fe_app.presentation.user.review.adapter.UserReviewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PartnerReviewFragment : BaseFragment<FragmentPartnerReviewBinding>(R.layout.fragment_partner_review),
    OnItemClickListener, OnReportTargetSelectedListener, OnReviewReportConfirmedListener, OnReviewReportCompleteListener {

    private val getReviewViewModel: GetPartnerReviewViewModel by viewModels()
    private lateinit var userReviewAdapter : UserReviewAdapter
    private var selectedItemPosition: Int = -1

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
                val count = reviews.size.toString()
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
        userReviewAdapter = UserReviewAdapter(showDeleteButton = false, listener = null,
            showReportButton = true, reportListener = this)

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

    // 1단계: 고객 리뷰 아이템 클릭 시 신고 대상 선택 다이얼로그 표시
    override fun onClick(position: Int) {
        selectedItemPosition = position
        val dialog = ReportTargetDialogFragment.newInstance(position)
        dialog.show(parentFragmentManager, "ReportTargetDialog")
    }

    // 2단계: 신고 대상 선택 후 신고 이유 선택 다이얼로그 표시
    override fun onReportTargetSelected(selectedTarget: String) {
        // 신고 대상이 선택되면 신고 이유 선택 다이얼로그를 띄움
        val reportDialog = ReviewReportDialogFragment.newInstance(selectedItemPosition)
        reportDialog.show(parentFragmentManager, "ReviewReportDialog")
    }

    // 3단계: 신고 이유 선택 후 API 호출하고 완료 다이얼로그 표시
    override fun onReviewReportConfirmed(position: Int, reportReason: String) {
        val currentList = userReviewAdapter.currentList
        if (position < currentList.size) {
            val reviewToReport = currentList[position]

            // TODO: ViewModel을 통해 실제 서버에 신고하는 API 호출
            handleReviewReport(reviewToReport.id ?: 0L, reportReason)

            // 신고 완료 다이얼로그 표시
            val completeDialog = ReviewReportCompleteDialogFragment.newInstance(position)
            completeDialog.show(parentFragmentManager, "ReviewReportCompleteDialog")
        }
    }

    // 4단계: 신고 완료 확인 후 아이템 삭제 및 UI 업데이트
    override fun onReviewReportComplete(position: Int) {
        val currentList = userReviewAdapter.currentList.toMutableList()

        if (position >= 0 && position < currentList.size) {
            // 아이템 삭제
            currentList.removeAt(position)
            userReviewAdapter.submitList(currentList)

            // 리뷰 개수 업데이트
            binding.tvReviewStoreReviewCount.text = "${currentList.size}개의 평가"

            // 리뷰가 모두 삭제된 경우 빈 화면으로 이동
            if (currentList.isEmpty()) {
                findNavController().navigate(R.id.action_partnerReviewFragment_to_partnerReviewNoneFragment)
            }
        }

        // 위치 초기화
        selectedItemPosition = -1
    }

    private fun handleReviewReport(reviewId: Long, reportReason: String) {
        // TODO: 실제 신고 API 호출 로직 추가
        when (reportReason) {
            "INAPPROPRIATE_CONTENT" -> {
                // 부적절한 내용 신고 처리
            }
            "FALSE_INFORMATION" -> {
                // 허위정보 신고 처리
            }
            "SPAM_PROMOTION" -> {
                // 스팸/광고 신고 처리
            }
            else -> {
                // 기타 신고 처리
            }
        }
    }
}