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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import android.app.AlertDialog

@AndroidEntryPoint
class AdminDashboardFragment :
    BaseFragment<FragmentAdminDashboardBinding>(R.layout.fragment_admin_dashboard) {

    //private val viewModel: AdminDashboardViewModel by viewModels()

    override fun initObserver() {
        // 기본 제목 설정
        binding.tvDashboardTitle.text = "숭실대학교 총학생회"

        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.dashboardState.collect { state ->
//                when (state) {
//                    is AdminDashboardViewModel.DashboardUiState.Idle -> {
//                        // 초기 상태
//                    }
//                    is AdminDashboardViewModel.DashboardUiState.Loading -> {
//                        showLoading()
//                    }
//                    is AdminDashboardViewModel.DashboardUiState.Success -> {
//                        hideLoading()
//                        setupUI(state.data)
//                    }
//                    is AdminDashboardViewModel.DashboardUiState.Fail -> {
//                        hideLoading()
//                        showError("서버 오류: ${state.message}")
//                    }
//                    is AdminDashboardViewModel.DashboardUiState.Error -> {
//                        hideLoading()
//                        showError(state.message)
//                    }
//                }
//            }
        }
    }

    override fun initView() {
        binding.btnViewSuggestions.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_admin_dashboard_to_suggestions)
        }

//        // 통합 API로 대시보드 데이터 로드
//        viewModel.loadAdminDashboard()
    }

    private fun setupUI(data: AdminDashboardModel) {
        // API에서 받은 이번달 이용현황 데이터 사용
        setMonthlyUsageText(data.monthlyUsageCount)
        setupAdminSpecificUI(data)
    }

    private fun setupAdminSpecificUI(adminStats: AdminDashboardModel) {
        // 통합 API에서 받은 모든 데이터 설정
        binding.tvTotalStudents?.text = "+ ${adminStats.totalStudentCount}"
        binding.tvNewStudents?.text = "+ ${adminStats.newStudentCount}"
        binding.tvTodayUsers?.text = "+ ${adminStats.todayUsagePersonCount}"

        // 바차트 설정
        setupPartnersBarChart(adminStats.storeUsageStats)

        // 분석 텍스트 설정
        if (adminStats.storeUsageStats.isNotEmpty()) {
            val topStore = adminStats.storeUsageStats.first() // 이미 사용량 내림차순으로 정렬됨
            binding.tvDashboardAnalysis?.text = "${topStore.storeName}의\n제휴 누적이용률이 가장 높아요!"
        } else {
            // 제휴 이용내역이 없는 경우
            binding.tvDashboardAnalysis?.text = "아직 제휴 이용내역이 없어요.\n첫 번째 이용자를 기다리고 있어요!"
        }
    }

    // 제휴 이용현황 바 차트 설정
    private fun setupPartnersBarChart(storeUsageStats: List<AdminDashboardModel.StoreUsageStat>) {
        val barChart = binding.barChartPartners

        // 데이터가 없는 경우 처리
        if (storeUsageStats.isEmpty()) {
            barChart.clear()
            barChart.setNoDataText("아직 제휴 이용내역이 없습니다")
            barChart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
            barChart.invalidate()
            return
        }

        val entries = ArrayList<BarEntry>()

        storeUsageStats.forEachIndexed { index, store ->
            entries.add(BarEntry(index.toFloat(), store.usageCount.toFloat()))
        }

        val dataSet = BarDataSet(entries, "제휴 이용 현황")

        // 바 색상 설정
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
        barChart.setTouchEnabled(true) // 터치 활성화 - 바 클릭 가능
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

        // 바 클릭 리스너 추가
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let { entry ->
                    val selectedIndex = entry.x.toInt()
                    if (selectedIndex < storeUsageStats.size) {
                        val selectedStore = storeUsageStats[selectedIndex]
                        showStoreUsageDialog(selectedStore)
                    }
                }
            }

            override fun onNothingSelected() {
                // 아무것도 선택되지 않았을 때
            }
        })

        barChart.animateY(1000)
        barChart.invalidate()
    }

    // 제휴 업체 이용현황 다이얼로그 표시
    private fun showStoreUsageDialog(store: AdminDashboardModel.StoreUsageStat) {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        val message = """
            업체명: ${store.storeName}
            총 이용 횟수: ${store.usageCount}건
            오늘 이용 횟수: ${store.todayUsageCount}건
            이번 달 이용 횟수: ${store.monthlyUsageCount}건
            등록일: ${store.registrationDate}
        """.trimIndent()

        dialogBuilder.setTitle("제휴 업체 이용현황")
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
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
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }
}