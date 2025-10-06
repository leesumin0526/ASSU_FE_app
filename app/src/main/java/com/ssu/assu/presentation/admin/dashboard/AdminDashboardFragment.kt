package com.ssu.assu.presentation.admin.dashboard

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentAdminDashboardBinding
import com.ssu.assu.domain.model.dashboard.AdminDashboardModel
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.ui.dashboard.AdminDashboardViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.widget.Toast
import com.ssu.assu.data.local.AuthTokenLocalStore
import javax.inject.Inject

@AndroidEntryPoint
class AdminDashboardFragment :
    BaseFragment<FragmentAdminDashboardBinding>(R.layout.fragment_admin_dashboard) {

    private val viewModel: AdminDashboardViewModel by viewModels()

    // 현재 매장 데이터를 저장할 변수
    private var currentStoreUsageStats: List<AdminDashboardModel.StoreUsageStat> = emptyList()

    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore

    override fun initObserver() {
        // 기본 제목 설정
        viewLifecycleOwner.lifecycleScope.launch {
            val username = authTokenLocalStore.getUserName()
            binding.tvDashboardTitle.text = if (!username.isNullOrEmpty()) {
                username
            } else {
                " "
            }
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
        binding.btnViewSuggestions.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_admin_dashboard_to_suggestions)
        }

        viewModel.loadAdminDashboard()
    }

    private fun setupUI(data: AdminDashboardModel) {
        // API에서 받은 이번달 이용현황 데이터 사용
        setMonthlyUsageText(data.monthlyUsageCount)
        setupAdminSpecificUI(data)
    }

    private fun setupAdminSpecificUI(adminStats: AdminDashboardModel) {
        // 통합 API에서 받은 모든 데이터 설정
        binding.tvTotalStudents?.text = "++ ${adminStats.totalStudentCount}"
        binding.tvNewStudents?.text = "++ ${adminStats.newStudentCount}"
        binding.tvTodayUsers?.text = "++ ${adminStats.todayUsagePersonCount}"

        // 현재 데이터 저장
        currentStoreUsageStats = adminStats.storeUsageStats

        // 바차트 설정
        setupPartnersBarChart(adminStats.storeUsageStats)

        // 분석 텍스트 설정
        if (adminStats.storeUsageStats.isNotEmpty()) {
            val topStore = adminStats.storeUsageStats.first() // 이미 사용량 내림차순으로 정렬됨
            binding.tvDashboardAnalysis?.text = "\"${topStore.storeName}\" 의\n제휴 누적이용률이 가장 높아요!"
        } else {
            // 제휴 이용내역이 없는 경우
            binding.tvDashboardAnalysis?.text = "아직 제휴 이용내역이 없어요.\n첫 번째 이용자를 기다리고 있어요!"
        }
    }

    // 제휴 이용현황 바 차트 설정
    private fun setupPartnersBarChart(storeUsageStats: List<AdminDashboardModel.StoreUsageStat>) {
        val barChart = binding.barChartPartners

        if (storeUsageStats.isEmpty()) {
            barChart.clear()
            barChart.setNoDataText("아직 제휴 이용내역이 없습니다")
            barChart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
            barChart.invalidate()
            return
        }

        val topStores = storeUsageStats.take(6)

        if (topStores.isEmpty()) {
            barChart.clear()
            barChart.setNoDataText("표시할 데이터가 없습니다")
            barChart.invalidate()
            return
        }

        // 초기 차트 업데이트 (1위 업체 선택)
        updatePartnersBarChart(barChart, topStores, 0)

        // 바 클릭 리스너
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is BarEntry) {
                    val selectedIndex = e.x.toInt()
                    if (selectedIndex >= 0 && selectedIndex < topStores.size) {
                        updatePartnersBarChart(barChart, topStores, selectedIndex)
                    }
                }
            }
            override fun onNothingSelected() {
                updatePartnersBarChart(barChart, topStores, 0)
            }
        })

        // 차트 기본 설정
        barChart.description.isEnabled = false
        barChart.setTouchEnabled(true)
        barChart.setDrawGridBackground(false)
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.setViewPortOffsets(20f, 100f, 20f, 40f)

        // X축 설정
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)

        // Y축 설정 - 중요: 활성화 필요
        val axisLeft = barChart.axisLeft
        axisLeft.isEnabled = true
        axisLeft.setDrawGridLines(false)
        axisLeft.setDrawAxisLine(false)
        axisLeft.setDrawLabels(false)
        axisLeft.axisMinimum = 0f

        barChart.axisRight.isEnabled = false
    }

    private fun updatePartnersBarChart(
        barChart: com.github.mikephil.charting.charts.BarChart,
        topStores: List<AdminDashboardModel.StoreUsageStat>,
        selectedIndex: Int
    ) {
        val mainColor = ContextCompat.getColor(requireContext(), R.color.assu_main)
        val defaultColor = ContextCompat.getColor(requireContext(), R.color.assu_font_sub)

        val dataSets = ArrayList<BarDataSet>()

        topStores.forEachIndexed { index, store ->
            val count = store.usageCount?.takeIf { it >= 0 } ?: 0
            val entry = BarEntry(index.toFloat(), count.toFloat())
            val dataSet = BarDataSet(arrayListOf(entry), "")

            dataSet.setDrawValues(true)
            dataSet.valueTextSize = 14f

            if (index == selectedIndex) {
                dataSet.color = mainColor
                dataSet.valueTextColor = mainColor
            } else {
                dataSet.color = defaultColor
                dataSet.valueTextColor = defaultColor
            }

            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }

            dataSets.add(dataSet)
        }

        val barData = BarData(dataSets as List<com.github.mikephil.charting.interfaces.datasets.IBarDataSet>)
        barData.barWidth = 0.6f

        // Y축 최대값 설정
        val maxValue = topStores.maxOfOrNull { it.usageCount ?: 0 } ?: 100
        barChart.axisLeft.axisMaximum = maxValue * 1.8f

        barChart.data = barData
        barChart.notifyDataSetChanged()
        barChart.invalidate()

        // 분석 텍스트 업데이트
        if (selectedIndex >= 0 && selectedIndex < topStores.size) {
            updateAnalysisText(topStores[selectedIndex])
        }
    }

    // 선택된 매장에 따라 분석 텍스트 업데이트
    private fun updateAnalysisText(selectedStore: AdminDashboardModel.StoreUsageStat) {
        val storeName = selectedStore.storeName ?: "알 수 없는 매장"
        val usageCount = selectedStore.usageCount ?: 0
        binding.tvDashboardAnalysis?.text = "\"${storeName}\"의\n제휴 이용건수는 ${usageCount}건이에요!"
    }

    // 이번달 이용현황 텍스트 설정
    private fun setMonthlyUsageText(monthlyCount: Int) {
        val fullText = "이번달에는 ${monthlyCount}건 이용했어요."
        val spannable = SpannableString(fullText)

        val start = fullText.indexOf("$monthlyCount")
        val end = start + "$monthlyCount".length

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
        // 예: binding.progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        // 로딩 UI 숨김
        // 예: binding.progressBar?.visibility = View.GONE
    }

    private fun showError(message: String) {
        // 에러 메시지 표시
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}