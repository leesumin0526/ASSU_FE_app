package com.example.assu_fe_app.presentation.admin.dashboard

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentAdminDashboardBinding
import com.example.assu_fe_app.domain.model.dashboard.AdminDashboardModel
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.dashboard.AdminDashboardViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminDashboardFragment :
    BaseFragment<FragmentAdminDashboardBinding>(R.layout.fragment_admin_dashboard) {

    private val viewModel: AdminDashboardViewModel by viewModels()

    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore



    override fun initObserver() {
        val userName = authTokenLocalStore.getUserName() ?: "사용자"

        binding.tvDashboardTitle.text = if (userName.isNotEmpty()) {
            "${userName}"
        } else {
            "안녕하세요, 사용자님!"
        }
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
        // 하드코딩된 예측값으로 설정
        setPredictionText(1250) // 하드코딩된 이용 건수

        binding.btnViewSuggestions.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_admin_dashboard_to_suggestions)
        }

        // Admin 대시보드 데이터 로드
        // viewModel.loadAdminDashboard()

        // 하드코딩된 데이터로 UI 설정
        setupUIWithHardcodedData()
    }

    private fun setupUI(data: AdminDashboardModel) {
        // 원래 코드 주석 처리
        // val predictedCount = calculatePrediction(data)
        // setPredictionText(predictedCount)
        // setupAdminSpecificUI(data)

        // 하드코딩된 데이터 사용
        setupUIWithHardcodedData()
    }

    // 하드코딩된 데이터로 UI 설정
    private fun setupUIWithHardcodedData() {
        // 하드코딩된 예측값 설정
        setPredictionText(122)

        // Admin 차트들 설정 (하드코딩된 데이터)
        setupAdminChartsWithHardcodedData()

        // 하드코딩된 Admin 관련 UI 설정
        setupAdminSpecificUIWithHardcodedData()
    }

    // Admin 대시보드 차트들을 하드코딩된 데이터로 설정
    private fun setupAdminChartsWithHardcodedData() {
        // 하드코딩된 제휴 이용현황 바 차트
        setupPartnersBarChart(listOf(320L, 280L, 450L, 380L, 520L, 410L, 600L))
    }

    // 제휴 이용현황 바 차트 설정
    private fun setupPartnersBarChart(usageData: List<Long>) {
        val barChart = binding.barChartPartners
        val entries = ArrayList<BarEntry>()

        usageData.forEachIndexed { index, usage ->
            entries.add(BarEntry(index.toFloat(), usage.toFloat()))
        }

        val dataSet = BarDataSet(entries, "제휴 이용 현황")

        // 바 색상 설정 (모든 바를 메인 색상으로)
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.setDrawValues(true)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.valueTextSize = 10f
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                return barEntry?.y?.toInt().toString()
            }
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.7f
        barChart.data = barData

        // 차트 기본 설정
        barChart.description.isEnabled = false
        barChart.setTouchEnabled(false)
        barChart.setDrawGridBackground(false)
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.setViewPortOffsets(30f, 30f, 30f, 30f)

        // X축 설정
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)

        // Y축 설정
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false

        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun calculatePrediction(adminStats: AdminDashboardModel): Int {
        // 원래 예측 로직 (주석 처리됨)
        // val totalUsage = adminStats.storeUsageStats.sumOf { it.usageCount }
        // val todayUsage = adminStats.todayUsagePersonCount
        // return (todayUsage * 30).toInt()

        // 하드코딩된 예측값 반환
        return 1250
    }

    private fun setupAdminSpecificUI(adminStats: AdminDashboardModel) {
        // 원래 Admin 전용 UI 요소들 설정 (주석 처리됨)
        // binding.tvTotalStudents.text = "총 ${adminStats.totalStudentCount}명"
        // binding.tvNewStudents.text = "신규 ${adminStats.newStudentCount}명"
        // binding.tvTodayUsers.text = "오늘 ${adminStats.todayUsagePersonCount}명 이용"
        // setupStoreUsageStats(adminStats.storeUsageStats)

        // 하드코딩된 데이터로 대체
        setupAdminSpecificUIWithHardcodedData()
    }

    // 하드코딩된 Admin 전용 UI 설정
    private fun setupAdminSpecificUIWithHardcodedData() {
        // Admin 전용 UI 요소들을 하드코딩된 데이터로 설정
        // 예시: TextView가 있다면 아래와 같이 하드코딩된 값들로 설정

        // binding.tvTotalStudents?.text = "총 3,250명"
        // binding.tvNewStudents?.text = "신규 45명"
        // binding.tvTodayUsers?.text = "오늘 127명 이용"

        // 하드코딩된 매장별 사용 통계
        // setupHardcodedStoreUsageStats()
    }

    // 하드코딩된 매장별 사용 통계 설정
    private fun setupHardcodedStoreUsageStats() {
        // 매장별 통계를 하드코딩된 데이터로 표시
        // 예시:
        val hardcodedStoreStats = listOf(
            "스타벅스 숭실대점: 85명",
            "투썸플레이스 상도점: 67명",
            "할리스커피 숭실대역점: 42명",
            "이디야커피 상도역점: 38명",
            "빽다방 숭실대점: 35명"
        )

        // 실제 UI 요소가 있다면 여기서 설정
        // hardcodedStoreStats.forEach { stat ->
        //     // 매장별 통계를 UI에 표시하는 로직
        // }
    }

    private fun setPredictionText(count: Int) {
        val fullText = "이번달에는 ${count}건 이용했어요."
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