package com.example.assu_fe_app.presentation.partner.dashboard

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerDashboardBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.dashboard.PartnerDashboardViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import androidx.core.graphics.toColorInt
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.domain.model.dashboard.PartnerDashboardModel
import com.example.assu_fe_app.domain.model.dashboard.PopularStoreModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PartnerDashboardFragment :
    BaseFragment<FragmentPartnerDashboardBinding>(R.layout.fragment_partner_dashboard) {

    private val viewModel: PartnerDashboardViewModel by viewModels()
    lateinit var tokenManager: TokenManager

    override fun initObserver() {
        tokenManager = TokenManager(requireContext())
        val userName = tokenManager.getUserName() ?: "사용자"

        binding.tvPartnerName.text = if (userName.isNotEmpty()) {
            "${userName}"
        } else {
            "안녕하세요, 사용자님!"
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dashboardState.collect { state ->
                when (state) {
                    is PartnerDashboardViewModel.DashboardUiState.Idle -> {
                        // 초기 상태
                    }
                    is PartnerDashboardViewModel.DashboardUiState.Loading -> {
                        showLoading()
                    }
                    is PartnerDashboardViewModel.DashboardUiState.Success -> {
                        hideLoading()
                        setupUI(state.data)
                    }
                    is PartnerDashboardViewModel.DashboardUiState.Fail -> {
                        hideLoading()
                        showError("서버 오류: ${state.message}")
                    }
                    is PartnerDashboardViewModel.DashboardUiState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedWeekIndex.collect { weekIndex ->
                updateAnalysisText()
            }
        }
    }

    override fun initView() {
        binding.btnViewContract.setOnClickListener {
            findNavController().navigate(R.id.action_partner_dashboard_to_partner_review)
        }
    }

    private fun setupUI(data: PartnerDashboardModel) {

        binding.tvGraphUpdateDateAndTime.text = getCurrentDateString()
        binding.tvTodayUpdateDateAndTime.text = getCurrentDateString()

        // 차트 설정
        setupPartnershipLineChart(data.getRankingTrend())
        setupClientBarChart(data.getUsageTrend())
        setupRankingGrid(data.todayBest)

        // 분석 텍스트 초기 설정
        updateAnalysisText()
    }

    private fun setupPartnershipLineChart(rankings: List<Long>) {
        val lineChart = binding.lineChartPartnership
        val entries = ArrayList<Entry>()

        rankings.forEachIndexed { index, ranking ->
            entries.add(Entry(index.toFloat(), ranking.toFloat()))
        }

        val dataSet = LineDataSet(entries, "우리가게 순위")

        // 라인 스타일 설정
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 5f
        dataSet.setDrawFilled(false)
        dataSet.setDrawValues(true)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.valueTextSize = 10f
        dataSet.setDrawCircleHole(true)
        dataSet.circleHoleColor = Color.WHITE
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.assu_main))

        // 값 표시를 정수로 설정
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getPointLabel(entry: Entry?): String {
                return "${entry?.y?.toInt()}위"
            }
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // 차트 기본 설정
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(false)
        lineChart.setDragEnabled(false)
        lineChart.setScaleEnabled(false)
        lineChart.setPinchZoom(false)
        lineChart.setDrawGridBackground(false)
        lineChart.legend.isEnabled = false
        lineChart.setViewPortOffsets(30f, 30f, 30f, 30f)

        // X축 설정
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)

        // Y축 설정 (순위는 역순)
        val leftAxis = lineChart.axisLeft
        leftAxis.isInverted = true
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.LTGRAY
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawLabels(false)
        leftAxis.granularity = 1f

        lineChart.axisRight.isEnabled = false
        lineChart.animateX(1000)
        lineChart.invalidate()
    }

    private fun setupClientBarChart(usageData: List<Long>) {
        val barChart = binding.barChartClients
        val entries = ArrayList<BarEntry>()

        usageData.forEachIndexed { index, usage ->
            entries.add(BarEntry(index.toFloat(), usage.toFloat()))
        }

        val dataSet = BarDataSet(entries, "제휴 사용 수")

        // 바 색상 설정 (선택된 주차만 메인 색상)
        updateBarColors(dataSet, usageData.size - 1) // 초기에는 최근 주차 선택

        dataSet.setDrawValues(true)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.valueTextSize = 10f
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                return barEntry?.y?.toInt().toString()
            }
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f
        barChart.data = barData

        // 바 클릭 이벤트
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is BarEntry) {
                    viewModel.selectWeek(e.x.toInt())
                    updateBarColors(dataSet, e.x.toInt())
                    barChart.invalidate()
                }
            }
            override fun onNothingSelected() {}
        })

        // 차트 기본 설정
        barChart.description.isEnabled = false
        barChart.setTouchEnabled(true)
        barChart.setDrawGridBackground(false)
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.setViewPortOffsets(30f, 30f, 30f, 30f)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)

        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false

        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun updateBarColors(dataSet: BarDataSet, selectedIndex: Int) {
        val colors = arrayListOf<Int>()
        for (i in 0 until dataSet.entryCount) {
            if (i == selectedIndex) {
                colors.add(ContextCompat.getColor(requireContext(), R.color.assu_main))
            } else {
                colors.add("#E8E8E8".toColorInt())
            }
        }
        dataSet.colors = colors
    }

    private fun setupRankingGrid(popularStores: List<PopularStoreModel>) {
        val gridLayout = binding.gridRanking
        gridLayout.removeAllViews()

        popularStores.take(8).forEach { store ->
            val itemView = createRankingItem(store)
            gridLayout.addView(itemView)
        }
    }

    private fun createRankingItem(store: PopularStoreModel): LinearLayout {
        val context = requireContext()
        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            layoutParams = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = androidx.gridlayout.widget.GridLayout.spec(androidx.gridlayout.widget.GridLayout.UNDEFINED, 1f)
                setMargins(0, 0, 0, 12.dpToPx())
            }
        }

        val rankTextView = TextView(context).apply {
            text = store.rank.toString()
            textSize = 14f
            setTextColor(
                if (store.isHighlight)
                    ContextCompat.getColor(context, R.color.assu_main)
                else
                    ContextCompat.getColor(context, R.color.assu_font_main)
            )
        }

        val storeTextView = TextView(context).apply {
            text = store.storeName
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 8.dpToPx()
            }
        }

        linearLayout.addView(rankTextView)
        linearLayout.addView(storeTextView)

        return linearLayout
    }

    private fun updateAnalysisText() {
        val analysisText = viewModel.getAnalysisText()
        val usageCount = viewModel.getSelectedWeekUsage()
        val highlightText = "${usageCount}명"

        setClientAnalysisText(analysisText, highlightText)
    }

    private fun setClientAnalysisText(fullText: String, highlightText: String) {
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf(highlightText)
        if (start >= 0) {
            val end = start + highlightText.length
            val color = ContextCompat.getColor(requireContext(), R.color.assu_main)
            spannable.setSpan(
                ForegroundColorSpan(color),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvDashboardClientAnalysis.text = spannable
    }

    private fun getCurrentDateString(): String {
        val formatter = java.text.SimpleDateFormat("yyyy년 MM월 dd일 HH:mm 기준", java.util.Locale.KOREAN)
        return formatter.format(java.util.Date())
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

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}