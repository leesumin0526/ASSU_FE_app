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
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.domain.model.dashboard.PartnerDashboardModel
import com.example.assu_fe_app.domain.model.dashboard.PopularStoreModel
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PartnerDashboardFragment :
    BaseFragment<FragmentPartnerDashboardBinding>(R.layout.fragment_partner_dashboard) {

    private val viewModel: PartnerDashboardViewModel by viewModels()

    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore

    override fun initObserver() {
        val userName = authTokenLocalStore.getUserName() ?: "사용자"

        binding.tvPartnerName.text = if (userName.isNotEmpty()) {
            userName
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

        viewModel.loadPartnerDashboard()
    }

    private fun setupUI(data: PartnerDashboardModel) {
        binding.tvGraphUpdateDateAndTime.text = getCurrentDateString()
        binding.tvTodayUpdateDateAndTime.text = getCurrentDateString()

        setupPartnershipLineChart(data.getRankingTrend())
        setupClientBarChart(data.getUsageTrend())
        setupRankingGrid(data.todayBest)

        updateAnalysisText()
    }

    private fun setupPartnershipLineChart(rankings: List<Long>) {
        val lineChart = binding.lineChartPartnership

        if (rankings.isEmpty()) {
            lineChart.clear()
            lineChart.setNoDataText("아직 순위 데이터가 없어요")
            lineChart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
            lineChart.invalidate()
            return
        }

        val entries = ArrayList<Entry>()

        rankings.forEachIndexed { index, ranking ->
            entries.add(Entry(index.toFloat(), ranking.toFloat()))
        }

        val dataSets = arrayListOf<LineDataSet>()

        for (i in 0 until entries.size) {
            val singleEntry = arrayListOf<Entry>()
            singleEntry.add(entries[i])

            val nodeDataSet = LineDataSet(singleEntry, "")
            nodeDataSet.setDrawCircles(true)
            nodeDataSet.circleRadius = 5f
            nodeDataSet.setDrawCircleHole(true)
            nodeDataSet.circleHoleColor = Color.WHITE
            nodeDataSet.setDrawValues(true)
            nodeDataSet.valueTextSize = 10f
            nodeDataSet.color = Color.TRANSPARENT

            if (i == entries.size - 1) {
                nodeDataSet.setCircleColor(Color.RED)
                nodeDataSet.valueTextColor = Color.RED
            } else {
                nodeDataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.graph_unselected))
                nodeDataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.graph_unselected)
            }

            nodeDataSet.valueFormatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String {
                    return "${entry?.y?.toInt()}위"
                }
            }

            dataSets.add(nodeDataSet)
        }

        for (i in 0 until entries.size - 1) {
            val segmentEntries = arrayListOf<Entry>()
            segmentEntries.add(entries[i])
            segmentEntries.add(entries[i + 1])

            val segmentDataSet = LineDataSet(segmentEntries, "")
            segmentDataSet.setDrawCircles(false)
            segmentDataSet.setDrawValues(false)
            segmentDataSet.lineWidth = 3f

            if (i == entries.size - 2) {
                segmentDataSet.color = Color.RED
            } else {
                segmentDataSet.color = ContextCompat.getColor(requireContext(), R.color.graph_unselected)
            }

            dataSets.add(segmentDataSet)
        }

        val lineData = LineData(dataSets.map { it as ILineDataSet })
        lineChart.data = lineData

        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(false)
        lineChart.setDragEnabled(false)
        lineChart.setScaleEnabled(false)
        lineChart.setPinchZoom(false)
        lineChart.setDrawGridBackground(false)
        lineChart.legend.isEnabled = false
        lineChart.setViewPortOffsets(30f, 30f, 30f, 30f)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)

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

        if (usageData.isEmpty()) {
            barChart.clear()
            barChart.setNoDataText("아직 이용내역이 없어요")
            barChart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
            barChart.invalidate()
            return
        }

        updateBarChart(barChart, usageData, usageData.size - 1)

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is BarEntry) {
                    viewModel.selectWeek(e.x.toInt())
                    updateBarChart(barChart, usageData, e.x.toInt())
                }
            }
            override fun onNothingSelected() {}
        })

        barChart.description.isEnabled = false
        barChart.setTouchEnabled(true)
        barChart.setDrawGridBackground(false)
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.setViewPortOffsets(30f, 30f, 30f, 60f)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.assu_font_sub)
        xAxis.textSize = 10f

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
                val index = value.toInt()
                val weeksFromNow = usageData.size - 1 - index

                return when (weeksFromNow) {
                    0 -> "이번주"
                    1 -> "1주전"
                    2 -> "2주전"
                    3 -> "3주전"
                    4 -> "4주전"
                    5 -> "5주전"
                    else -> "${weeksFromNow}주전"
                }
            }
        }

        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.animateY(1000)
    }

    private fun updateBarChart(
        barChart: com.github.mikephil.charting.charts.BarChart,
        usageData: List<Long>,
        selectedIndex: Int
    ) {
        val dataSets = ArrayList<BarDataSet>()

        usageData.forEachIndexed { index, usage ->
            val entry = BarEntry(index.toFloat(), usage.toFloat())
            val dataSet = BarDataSet(arrayListOf(entry), "")

            dataSet.setDrawValues(true)
            dataSet.valueTextSize = 10f

            if (index == selectedIndex) {
                dataSet.color = ContextCompat.getColor(requireContext(), R.color.assu_main)
                dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_main)
            } else {
                dataSet.color = "#E8E8E8".toColorInt()
                dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_font_sub)
            }

            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String {
                    return barEntry?.y?.toInt().toString()
                }
            }

            dataSets.add(dataSet)
        }

        val barData = BarData(dataSets as List<com.github.mikephil.charting.interfaces.datasets.IBarDataSet>)
        barData.barWidth = 0.6f
        barChart.data = barData
        barChart.invalidate()
    }

    private fun setupRankingGrid(popularStores: List<PopularStoreModel>) {
        val gridLayout = binding.gridRanking
        gridLayout.removeAllViews()

        if (popularStores.isEmpty()) {
            val context = requireContext()
            val noDataTextView = TextView(context).apply {
                text = "아직 인기매장 데이터가 없어요"
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.assu_font_sub))
                gravity = android.view.Gravity.CENTER
                layoutParams = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = (80 * resources.displayMetrics.density).toInt()
                    columnSpec = androidx.gridlayout.widget.GridLayout.spec(0, 2)
                    rowSpec = androidx.gridlayout.widget.GridLayout.spec(0, 4)
                }
            }
            gridLayout.addView(noDataTextView)
            return
        }

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
                setMargins(0, 0, 0, (12 * resources.displayMetrics.density).toInt())
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
                marginStart = (8 * resources.displayMetrics.density).toInt()
            }
        }

        linearLayout.addView(rankTextView)
        linearLayout.addView(storeTextView)

        return linearLayout
    }

    private fun updateAnalysisText() {
        val currentDashboardData = viewModel.dashboardState.value
        if (currentDashboardData !is PartnerDashboardViewModel.DashboardUiState.Success) {
            binding.tvDashboardClientAnalysis.text = "아직 이용내역이 없어요.\n첫 번째 이용자를 기다리고 있어요!"
            return
        }

        val usageData = currentDashboardData.data.getUsageTrend()

        if (usageData.isEmpty()) {
            binding.tvDashboardClientAnalysis.text = "아직 이용내역이 없어요.\n첫 번째 이용자를 기다리고 있어요!"
            return
        }

        val selectedWeekIndex = viewModel.selectedWeekIndex.value ?: (usageData.size - 1)
        val usageCount = usageData.getOrNull(selectedWeekIndex) ?: usageData.lastOrNull() ?: 0L

        if (usageCount == 0L) {
            val weeksFromNow = usageData.size - 1 - selectedWeekIndex
            val timeText = when (weeksFromNow) {
                0 -> "이번 주"
                1 -> "지난주"
                else -> "${weeksFromNow}주 전"
            }
            binding.tvDashboardClientAnalysis.text = "${timeText}에는 아직 이용내역이 없어요.\n다음을 기대해보세요!"
            return
        }

        val weeksFromNow = usageData.size - 1 - selectedWeekIndex
        val timeText = when (weeksFromNow) {
            0 -> "이번 주에"
            1 -> "지난주에"
            2 -> "2주 전에"
            3 -> "3주 전에"
            4 -> "4주 전에"
            5 -> "5주 전에"
            else -> "${weeksFromNow}주 전에"
        }

        val analysisText = "${timeText} 숭실대학교 학생\n${usageCount}명이 매장에서 제휴 서비스를 이용했어요"
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
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }
}