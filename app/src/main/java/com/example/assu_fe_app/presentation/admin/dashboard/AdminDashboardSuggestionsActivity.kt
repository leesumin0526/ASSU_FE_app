package com.example.assu_fe_app.presentation.admin.dashboard

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityAdminDashboardSuggestionsBinding
import com.example.assu_fe_app.presentation.admin.dashboard.adapter.AdminSuggestionListAdapter
import com.example.assu_fe_app.presentation.admin.dashboard.adapter.AdminSuggestionReportCompleteDialogFragment
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.user.review.mypage.OnItemClickListener
import com.example.assu_fe_app.presentation.partner.dashboard.review.OnReviewReportConfirmedListener
import com.example.assu_fe_app.ui.admin.AdminSuggestionsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminDashboardSuggestionsActivity : BaseActivity<ActivityAdminDashboardSuggestionsBinding>(R.layout.activity_admin_dashboard_suggestions),
    OnItemClickListener, OnReviewReportConfirmedListener {

    private val viewModel: AdminSuggestionsViewModel by viewModels()
    private lateinit var adapter: AdminSuggestionListAdapter

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3 // 8dp 추가
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        binding.ivSuggestionListBack.setOnClickListener {
            finish() // 현재 Activity 종료 → 이전 화면으로 돌아감
        }

        initAdapter()
    }

    override fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.suggestionsState.collect { state ->
                    when (state) {
                        is AdminSuggestionsViewModel.SuggestionsUiState.Loading -> {}
                        is AdminSuggestionsViewModel.SuggestionsUiState.Success -> {
                            adapter.submitList(state.data)
                            binding.tvSuggestionCount.text = state.data.size.toString()
                        }
                        is AdminSuggestionsViewModel.SuggestionsUiState.Error -> {
                            Toast.makeText(this@AdminDashboardSuggestionsActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = AdminSuggestionListAdapter(reportListener = this)

        binding.rvSuggestionList.apply {
            adapter = this@AdminDashboardSuggestionsActivity.adapter
            layoutManager = LinearLayoutManager(this@AdminDashboardSuggestionsActivity)

            // 아이템 간 여백 설정 (20dp)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position != 0) {
                        outRect.top = (20 * resources.displayMetrics.density).toInt()
                    }
                }
            })
        }
    }

    override fun onClick(position: Int) {
        val dialog = AdminSuggestionReportDialogFragment.newInstance(position)
        dialog.show(supportFragmentManager, "AdminSuggestionReportDialog")
    }

    override fun onReviewReportConfirmed(position: Int, reportReason: String) {
        val currentList = adapter.currentList
        if (position < currentList.size) {
            val suggestionToReport = currentList[position]

            // TODO: ViewModel을 통해 실제 서버에 신고하는 API 호출
            handleSuggestionReport(suggestionToReport.suggestionId, reportReason)

            // 신고 완료 후 다이얼로그 닫기
            val reportDialog = supportFragmentManager.findFragmentByTag("AdminSuggestionReportCompleteDialog") as? AdminSuggestionReportCompleteDialogFragment
            reportDialog?.dismiss()

            // 신고 완료 토스트 메시지
            Toast.makeText(this, "신고가 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSuggestionReport(suggestionId: Long, reportReason: String) {
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

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}