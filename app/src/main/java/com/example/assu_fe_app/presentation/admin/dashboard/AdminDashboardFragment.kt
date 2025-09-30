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
import kotlinx.coroutines.launch
import android.app.AlertDialog
import android.widget.Toast
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
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
            binding.tvDashboardAnalysis?.text = "&quot;${topStore.storeName}&quot; 의\n제휴 누적이용률이 가장 높아요!"
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

        // 상위 6개만 선택 (이미 사용량 내림차순으로 정렬되어 있음)
        val topStores = storeUsageStats.take(6)

        // 빈 데이터 체크
        if (topStores.isEmpty()) {
            barChart.clear()
            barChart.setNoDataText("표시할 데이터가 없습니다")
            barChart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
            barChart.invalidate()
            return
        }

        val entries = ArrayList<BarEntry>()

        topStores.forEachIndexed { index, store ->
            // usageCount가 null이거나 음수가 아닌지 확인
            val count = store.usageCount?.takeIf { it >= 0 } ?: 0
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
        }

        // entries가 비어있지 않은지 확인
        if (entries.isEmpty()) {
            barChart.clear()
            barChart.setNoDataText("차트 데이터를 생성할 수 없습니다")
            barChart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
            barChart.invalidate()
            return
        }

        val dataSet = BarDataSet(entries, "제휴 이용 현황")

        // 바 색상 설정
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.setDrawValues(true)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.valueTextSize = 10f

        // null 체크가 포함된 ValueFormatter
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                return barEntry?.y?.toInt()?.toString() ?: "0"
            }

            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.7f

        // 차트에 데이터 설정하기 전에 차트 초기화
        barChart.clear()
        barChart.data = barData

        // 기본 렌더러 사용 (둥근 모서리는 나중에 추가)
        // val customRenderer = RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler)
        // barChart.renderer = customRenderer

        // 차트 기본 설정
        barChart.description.isEnabled = false
        barChart.setTouchEnabled(true) // 터치 활성화 - 바 클릭 가능
        barChart.setDrawGridBackground(false)
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.setViewPortOffsets(30f, 30f, 30f, 30f)

        // 차트 새로고침 설정
        barChart.setMaxVisibleValueCount(6) // 최대 6개만 표시
        barChart.setPinchZoom(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)

        // X축 설정
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        // Y축 설정
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false

        // 바 클릭 리스너 추가
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let { entry ->
                    val selectedIndex = entry.x.toInt()
                    // topStores 리스트의 인덱스를 사용
                    if (selectedIndex >= 0 && selectedIndex < topStores.size) {
                        val selectedStore = topStores[selectedIndex]
                        //showStoreUsageDialog(selectedStore)
                        // 분석 텍스트 업데이트
                        updateAnalysisText(selectedStore)
                    }
                }
            }

            override fun onNothingSelected() {
                // 아무것도 선택되지 않았을 때 - 원래 최고 이용률 매장으로 되돌리기
                if (currentStoreUsageStats.isNotEmpty()) {
                    val topStore = currentStoreUsageStats.first()
                    binding.tvDashboardAnalysis?.text = "${topStore.storeName}의\n제휴 누적이용률이 가장 높아요!"
                }
            }
        })

        // 차트 새로고침 및 애니메이션
        try {
            barChart.animateY(1000)
            barChart.invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
            // 애니메이션 실패 시 기본 차트만 표시
            barChart.invalidate()
        }
    }

    // 선택된 매장에 따라 분석 텍스트 업데이트
    private fun updateAnalysisText(selectedStore: AdminDashboardModel.StoreUsageStat) {
        val storeName = selectedStore.storeName ?: "알 수 없는 매장"
        val usageCount = selectedStore.usageCount ?: 0
        binding.tvDashboardAnalysis?.text = "${storeName}의\n제휴 이용건수는 ${usageCount}건이에요!"
    }

    // 매장 이용현황 다이얼로그 표시
//    private fun showStoreUsageDialog(store: AdminDashboardModel.StoreUsageStat) {
//        val storeName = store.storeName ?: "알 수 없는 매장"
//        val usageCount = store.usageCount ?: 0
//
//        try {
//            AlertDialog.Builder(requireContext())
//                .setTitle("제휴 이용현황")
//                .setMessage("${storeName}\n이용건수: ${usageCount}건")
//                .setPositiveButton("확인") { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .show()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            showError("다이얼로그 표시 중 오류가 발생했습니다")
//        }
//    }

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