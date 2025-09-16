package com.example.assu_fe_app.presentation.admin.dashboard

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentAdminDashboardBinding
import com.example.assu_fe_app.domain.model.dashboard.AdminDashboardModel
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.dashboard.AdminDashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminDashboardFragment :
    BaseFragment<FragmentAdminDashboardBinding>(R.layout.fragment_admin_dashboard) {

    private val viewModel: AdminDashboardViewModel by viewModels()

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dashboardState.collect { state ->
                when (state) {
                    is AdminDashboardViewModel.DashboardUiState.Idle -> {
                        // 초기 상태
                    }
                    is AdminDashboardViewModel.DashboardUiState.Loading -> {
                        showLoading()
                    }
                    is AdminDashboardViewModel.DashboardUiState.Success -> {
                        hideLoading()
                        setupUI(state.data)
                    }
                    is AdminDashboardViewModel.DashboardUiState.Fail -> {
                        hideLoading()
                        showError("서버 오류: ${state.message}")
                    }
                    is AdminDashboardViewModel.DashboardUiState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }
    }

    override fun initView() {
        // 초기 예측값 설정 (하드코딩이지만 임시로)
        setPredictionText(192)

        binding.btnViewSuggestions.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_admin_dashboard_to_suggestions)
        }

        // Admin 대시보드 데이터 로드
        viewModel.loadAdminDashboard()
    }

    private fun setupUI(data: AdminDashboardModel) {
        // 예측값을 실제 데이터 기반으로 계산
        val predictedCount = calculatePrediction(data)
        setPredictionText(predictedCount)

        // Admin 관련 UI 설정
        setupAdminSpecificUI(data)
    }

    private fun calculatePrediction(adminStats: AdminDashboardModel): Int {
        // 간단한 예측 로직 (실제로는 더 복잡한 알고리즘 사용)
        val totalUsage = adminStats.storeUsageStats.sumOf { it.usageCount }
        val todayUsage = adminStats.todayUsagePersonCount

        // 예시: 오늘 사용량 * 30 (한 달 예측)
        return (todayUsage * 30).toInt()
    }

    private fun setupAdminSpecificUI(adminStats: AdminDashboardModel) {
        // Admin 전용 UI 요소들 설정
        // 예: 총 제휴 학생 수, 신규 가입자 수 등을 화면에 표시

        // 예시: TextView가 있다면
        // binding.tvTotalStudents.text = "총 ${adminStats.totalStudentCount}명"
        // binding.tvNewStudents.text = "신규 ${adminStats.newStudentCount}명"
        // binding.tvTodayUsers.text = "오늘 ${adminStats.todayUsagePersonCount}명 이용"

        // 매장별 사용 통계 표시
        // setupStoreUsageStats(adminStats.storeUsageStats)
    }

    private fun setPredictionText(count: Int) {
        val fullText = "이번달에는 ${count}건 이상일 것으로 예상돼요"
        val spannable = SpannableString(fullText)

        val start = fullText.indexOf("$count")
        val end = start + "$count".length

        val color = ContextCompat.getColor(requireContext(), R.color.assu_main)
        spannable.setSpan(
            ForegroundColorSpan(color),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvDashboardPrediction.text = spannable
    }

    private fun showLoading() {
        // 로딩 UI 표시
    }

    private fun hideLoading() {
        // 로딩 UI 숨김
    }

    private fun showError(message: String) {
        // 에러 메시지 표시
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }
}