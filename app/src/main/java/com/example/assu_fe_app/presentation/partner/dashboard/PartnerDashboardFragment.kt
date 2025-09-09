package com.example.assu_fe_app.presentation.partner.dashboard

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerDashboardBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class PartnerDashboardFragment :
    BaseFragment<FragmentPartnerDashboardBinding>(R.layout.fragment_partner_dashboard) {

    override fun initObserver() {}

    override fun initView() {
        // 차트 초기화
        setupPartnershipLineChart()
        setupClientBarChart()

        // 예시 적용
        setClientAnalysisText(
            fullText = "이번 달에 숭실대학교 학생 \n 120명이 매장에서 제휴 서비스를 이용했어요",
            highlightText = "120명"
        )

        binding.btnViewContract.setOnClickListener {
            findNavController().navigate(R.id.action_partner_dashboard_to_partner_review)
        }
    }

    private fun setupPartnershipLineChart() {
        val lineChart = binding.lineChartPartnership

        // 주간 제휴 이용현황 데이터
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 78f))
        entries.add(Entry(1f, 85f))
        entries.add(Entry(2f, 90f))
        entries.add(Entry(3f, 95f))
        entries.add(Entry(4f, 105f))
        entries.add(Entry(5f, 118f))
        entries.add(Entry(6f, 111f))

        val dataSet = LineDataSet(entries, "제휴 이용현황")

        // 라인 스타일 설정
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.assu_main))
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 5f

        dataSet.setDrawValues(true)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.valueTextSize = 10f
        dataSet.setDrawCircleHole(false)

        // 꺾은선 스타일 (직선으로 연결)
        dataSet.mode = LineDataSet.Mode.LINEAR

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
        lineChart.setViewPortOffsets(20f, 20f, 20f, 40f)

        // X축 설정 (요일)
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.assu_font_sub)
        xAxis.textSize = 10f
        xAxis.labelCount = 7
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return when (value.toInt()) {
                    0 -> "월"
                    1 -> "화"
                    2 -> "수"
                    3 -> "목"
                    4 -> "금"
                    5 -> "토"
                    6 -> "일"
                    else -> ""
                }
            }
        }

        // Y축 설정 (순위는 뒤집어서 표시 - 1위가 위쪽)
        lineChart.axisLeft.isEnabled = true
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisLeft.setDrawAxisLine(false)
        lineChart.axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.assu_font_sub)
        lineChart.axisLeft.textSize = 10f
        lineChart.axisLeft.setInverted(true) // Y축 뒤집기
        lineChart.axisLeft.axisMinimum = 1f
        lineChart.axisLeft.axisMaximum = 8f
        lineChart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return "${value.toInt()}위"
            }
        }
        lineChart.axisRight.isEnabled = false

        // 애니메이션
        lineChart.animateX(1000)
        lineChart.invalidate()
    }

    private fun setupClientBarChart() {
        val barChart = binding.barChartClients

        // 월별 제휴 사용 수 데이터
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 45f))   // 1월
        entries.add(BarEntry(1f, 120f))  // 2월 (선택된 바)
        entries.add(BarEntry(2f, 78f))   // 3월
        entries.add(BarEntry(3f, 92f))   // 4월
        entries.add(BarEntry(4f, 65f))   // 5월
        entries.add(BarEntry(5f, 110f))  // 6월

        val dataSet = BarDataSet(entries, "제휴 사용 수")

        // 바 색상 설정 (2월만 메인 색상, 나머지는 회색)
        val colors = arrayListOf<Int>()
        colors.add(Color.parseColor("#E8E8E8")) // 1월 - 회색
        colors.add(ContextCompat.getColor(requireContext(), R.color.assu_main)) // 2월 - 메인 색상
        colors.add(Color.parseColor("#E8E8E8")) // 3월 - 회색
        colors.add(Color.parseColor("#E8E8E8")) // 4월 - 회색
        colors.add(Color.parseColor("#E8E8E8")) // 5월 - 회색
        colors.add(Color.parseColor("#E8E8E8")) // 6월 - 회색

        dataSet.colors = colors
        dataSet.setDrawValues(true)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_font_sub)
        dataSet.valueTextSize = 10f
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                return barEntry?.y?.toInt().toString()
            }
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f
        barChart.data = barData

        // 차트 기본 설정
        barChart.description.isEnabled = false
        barChart.setTouchEnabled(true)
        barChart.setDrawGridBackground(false)
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.setViewPortOffsets(40f, 20f, 40f, 40f)

        // X축 설정 (월)
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.assu_font_sub)
        xAxis.textSize = 10f
        xAxis.labelCount = 6
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return when (value.toInt()) {
                    0 -> "1월"
                    1 -> "2월"
                    2 -> "3월"
                    3 -> "4월"
                    4 -> "5월"
                    5 -> "6월"
                    else -> ""
                }
            }
        }

        // Y축 숨기기
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false

        // 바 클릭 이벤트
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is BarEntry) {
                    updateBarSelection(e.x.toInt())
                }
            }

            override fun onNothingSelected() {}
        })

        // 애니메이션
        barChart.animateY(1000)
        barChart.invalidate()
    }

    // 바 차트에서 선택된 바의 색상 업데이트
    private fun updateBarSelection(selectedIndex: Int) {
        val barChart = binding.barChartClients
        val dataSet = barChart.data.getDataSetByIndex(0) as BarDataSet

        val colors = arrayListOf<Int>()
        for (i in 0 until dataSet.entryCount) {
            if (i == selectedIndex) {
                colors.add(ContextCompat.getColor(requireContext(), R.color.assu_main)) // 선택된 바는 메인 색상
            } else {
                colors.add(ContextCompat.getColor(requireContext(), R.color.not_selected_nav_btn)) // 나머지는 not_selected_nav_btn 색상
            }
        }

        dataSet.colors = colors
        barChart.invalidate()

        // 선택된 월에 따라 분석 텍스트 업데이트
        updateAnalysisText(selectedIndex)
    }

    // 분석 텍스트 업데이트
    private fun updateAnalysisText(selectedMonth: Int) {
        val monthNames = arrayOf("1월", "2월", "3월", "4월", "5월", "6월")
        val studentCounts = arrayOf(45, 120, 78, 92, 65, 110)

        if (selectedMonth < monthNames.size && selectedMonth < studentCounts.size) {
            val fullText = "${monthNames[selectedMonth]}에 숭실대학교 학생 \n ${studentCounts[selectedMonth]}명이 매장에서 제휴 서비스를 이용했어요"
            val highlightText = "${studentCounts[selectedMonth]}명"

            setClientAnalysisText(fullText, highlightText)
        }
    }

    // 분석 문구 중 특정 텍스트에 색상을 입히는 함수
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
}