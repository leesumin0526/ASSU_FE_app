package com.ssu.assu.presentation.admin.dashboard

import android.content.Context
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssu.assu.R
import com.ssu.assu.databinding.ActivityAdminDashboardSuggestionsBinding
import com.ssu.assu.domain.model.report.ReportTargetType
import com.ssu.assu.domain.model.report.ReportType
import com.ssu.assu.presentation.admin.dashboard.adapter.AdminSuggestionListAdapter
import com.ssu.assu.presentation.base.BaseActivity
import com.ssu.assu.presentation.common.report.OnItemClickListener
import com.ssu.assu.presentation.common.report.OnReportTargetSelectedListener
import com.ssu.assu.presentation.common.report.OnReviewReportConfirmedListener
import com.ssu.assu.ui.admin.AdminSuggestionsViewModel
import com.ssu.assu.ui.report.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminDashboardSuggestionsActivity : BaseActivity<ActivityAdminDashboardSuggestionsBinding>(R.layout.activity_admin_dashboard_suggestions),
    OnItemClickListener, OnReportTargetSelectedListener, OnReviewReportConfirmedListener {

    private val viewModel: AdminSuggestionsViewModel by viewModels()
    private val reportViewModel: ReportViewModel by viewModels()
    private lateinit var adapter: AdminSuggestionListAdapter
    private var selectedItemPosition: Int = -1
    private var isCurrentStudentReport: Boolean = false

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

        // ReportViewModel Observer 추가
        reportViewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                val completeDialog = AdminSuggestionReportCompleteDialogFragment.newInstance(
                    selectedItemPosition,
                    isCurrentStudentReport
                )
                completeDialog.show(supportFragmentManager, "AdminSuggestionReportCompleteDialog")
            }
        }

        reportViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                reportViewModel.clearError()
            }
        }

        reportViewModel.isLoading.observe(this) { isLoading ->
            // 필요시 로딩 UI 표시
        }
    }

    private fun initAdapter() {
        adapter = AdminSuggestionListAdapter(reportListener = this)

        binding.rvSuggestionList.apply {
            adapter = this@AdminDashboardSuggestionsActivity.adapter
            layoutManager = LinearLayoutManager(this@AdminDashboardSuggestionsActivity)
        }
    }

    // 1단계: 어댑터에서 제휴 건의글 아이템 클릭시 신고 타겟 선택 다이얼로그 띄우기
    override fun onClick(position: Int) {
        selectedItemPosition = position
        val dialog = AdminReportTargetDialogFragment.newInstance(position)
        dialog.show(supportFragmentManager, "AdminReportTargetDialog")
    }

    // 2단계: 제휴 건의 대상 선택 이후 신고하기 버튼 누르면 신고 이유 다이얼로그 띄우기
    override fun onReportTargetSelected(selectedTarget: String, isStudentReport: Boolean) {
        isCurrentStudentReport = isStudentReport
        val reportDialog = AdminSuggestionReportDialogFragment.newInstance(
            selectedItemPosition,
            isStudentReport
        )
        reportDialog.show(supportFragmentManager, "AdminSuggestionReportDialog")
    }
    // 3단계: 제휴 건의 이유 버튼 누르면 api 호출 이후 신고 완료 dialog 띄우기
    override fun onReviewReportConfirmed(position: Int, reportReason: String) {
        val currentList = adapter.currentList
        if (position < currentList.size) {
            val suggestionToReport = currentList[position]

            // ViewModel을 통해 실제 API 호출
            reportViewModel.submitReport(
                targetType = ReportTargetType.SUGGESTION,
                targetId = suggestionToReport.suggestionId,
                reportType = ReportType.fromApiValue(reportReason) ?: ReportType.SUGGESTION_INAPPROPRIATE_CONTENT,
                isStudentReport = isCurrentStudentReport
            )
        }
    }

    // 4단계: 신고 완료 다이얼로그에서 확인 버튼 누르면 해당 아이템 삭제
    fun onReportCompleteConfirmed(position: Int) {
        // 현재 리스트에서 해당 아이템 제거
        val currentList = adapter.currentList.toMutableList()
        if (position < currentList.size) {
            currentList.removeAt(position)
            adapter.submitList(currentList)

            // 카운트 업데이트
            binding.tvSuggestionCount.text = currentList.size.toString()

            // 성공 토스트 메시지
            Toast.makeText(this, "신고가 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 위치 초기화
        selectedItemPosition = -1
    }


    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}