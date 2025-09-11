package com.example.assu_fe_app.presentation.partner.dashboard

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerDashboardBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.data.LineDataSet.Mode.LINEAR
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import androidx.core.graphics.toColorInt
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class PartnerDashboardFragment :
    BaseFragment<FragmentPartnerDashboardBinding>(R.layout.fragment_partner_dashboard) {

    override fun initObserver() {}

    override fun initView() {
        setupPartnershipLineChart()
        setupClientBarChart()

        setClientAnalysisText(
            fullText = "이번 달에 숭실대학교 학생 \n 110명이 매장에서 제휴 서비스를 이용했어요",
            highlightText = "110명"
        )

        binding.btnViewContract.setOnClickListener {
            findNavController().navigate(R.id.action_partner_dashboard_to_partner_review)
        }
    }

    private fun setupPartnershipLineChart() {
        val lineChart = binding.lineChartPartnership

        // 순위 데이터 (낮은 숫자가 더 좋은 순위)
        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 5f))   // 1주차 - 5위
        entries.add(Entry(1f, 8f))   // 2주차 - 8위
        entries.add(Entry(2f, 3f))   // 3주차 - 3위
        entries.add(Entry(3f, 12f))  // 4주차 - 12위
        entries.add(Entry(4f, 2f))   // 5주차 - 2위
        entries.add(Entry(5f, 15f))  // 6주차 - 15위 (하락 - 빨간색으로 표시)

        val dataSet = LineDataSet(entries, "우리가게 순위")

        // 라인 스타일 설정
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 5f
        dataSet.setDrawFilled(false)
        dataSet.fillColor = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.fillAlpha = 30
        dataSet.setDrawValues(true)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_main)
        dataSet.valueTextSize = 10f
        dataSet.setDrawCircleHole(true)
        dataSet.circleHoleColor = Color.WHITE

        // 값 표시를 정수로 설정
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getPointLabel(entry: Entry?): String {
                return "${entry?.y?.toInt()}위"
            }
        }

        // 모든 원의 색상을 메인 색상으로 설정하되, 마지막 점만 빨간색으로 설정
        val circleColors = ArrayList<Int>()
        for (i in 0 until chartEntries.size) {
            if (i == chartEntries.size - 1) {
                circleColors.add(Color.RED) // 마지막 점은 빨간색 (순위 하락)
            } else {
                circleColors.add(ContextCompat.getColor(requireContext(), R.color.assu_main))
            }
        }
        dataSet.circleColors = circleColors

        // 마지막 세그먼트(edge)를 빨간색으로 표시하기 위해 별도 데이터셋 생성
        val lastSegmentEntries = ArrayList<Entry>()
        lastSegmentEntries.add(Entry(4f, 2f))  // 5주차 - 2위
        lastSegmentEntries.add(Entry(5f, 15f)) // 6주차 - 15위

        val lastSegmentDataSet = LineDataSet(lastSegmentEntries, "")
        lastSegmentDataSet.color = Color.RED
        lastSegmentDataSet.lineWidth = 3f
        lastSegmentDataSet.setDrawCircles(false)
        lastSegmentDataSet.setDrawValues(false)

        dataSet.mode = LINEAR
        dataSet.cubicIntensity = 0.2f

        val lineData = LineData(dataSet, lastSegmentDataSet)
        lineChart.data = lineData

        // 커스텀 렌더러 적용 (빨간 텍스트를 위해)
        val customRenderer = CustomLineChartRenderer(lineChart, lineChart.animator, lineChart.viewPortHandler)
        lineChart.renderer = customRenderer

        // 차트 기본 설정
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(false)
        lineChart.setDragEnabled(false)
        lineChart.setScaleEnabled(false)
        lineChart.setPinchZoom(false)
        lineChart.setDrawGridBackground(false)
        lineChart.legend.isEnabled = false
        lineChart.setViewPortOffsets(30f, 30f, 30f, 30f)  // 패딩을 줄여서 차트가 box에 맞게

        // X축 설정 (라벨 없이)
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)  // X축 라벨 완전히 숨기기

        // Y축 설정 (라벨 숨김으로 잘림 방지)
        val leftAxis = lineChart.axisLeft
        leftAxis.isInverted = true  // Y축 역순 (1위가 위쪽)
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.LTGRAY
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawLabels(false)  // Y축 라벨 숨김
        leftAxis.granularity = 1f  // 1위 단위로 격자선 표시
        leftAxis.setLabelCount(20, false)  // 더 많은 격자선 표시

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
        entries.add(BarEntry(1f, 78f))   // 2월
        entries.add(BarEntry(2f, 92f))   // 3월
        entries.add(BarEntry(3f, 65f))   // 4월
        entries.add(BarEntry(4f, 87f))   // 5월
        entries.add(BarEntry(5f, 110f))  // 6월 (가장 최근)

        val dataSet = BarDataSet(entries, "제휴 사용 수")

        // 바 색상 설정 (6월만 메인 색상, 나머지는 회색) - 초기 선택: 가장 최근
        val colors = arrayListOf<Int>()
        colors.add("#E8E8E8".toColorInt()) // 1월 - 회색
        colors.add("#E8E8E8".toColorInt()) // 2월 - 회색
        colors.add("#E8E8E8".toColorInt()) // 3월 - 회색
        colors.add("#E8E8E8".toColorInt()) // 4월 - 회색
        colors.add("#E8E8E8".toColorInt()) // 5월 - 회색
        colors.add(ContextCompat.getColor(requireContext(), R.color.assu_main)) // 6월 - 메인 색상 (초기 선택)

        dataSet.colors = colors
        dataSet.setDrawValues(true)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_main) // 초기 선택된 바의 텍스트는 파란색
        dataSet.valueTextSize = 10f
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                return barEntry?.y?.toInt().toString()
            }
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f
        barChart.data = barData

        // 커스텀 렌더러로 둥근 모서리 적용 (radius만)
        val renderer = RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler)
        renderer.setRadius(12f)  // radius만 적용
        barChart.renderer = renderer

        // 차트 기본 설정
        barChart.description.isEnabled = false
        barChart.setTouchEnabled(true)
        barChart.setDrawGridBackground(false)
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)
        barChart.setViewPortOffsets(30f, 30f, 30f, 30f)  // 패딩 조정

        // X축 설정 (라벨 숨김)
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)  // X축 라벨 완전히 숨기기

        // Y축 숨기기
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false

        // 바 클릭 이벤트 - 클릭 시 선택된 바만 업데이트
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is BarEntry) {
                    updateBarSelection(e.x.toInt())
                }
            }

            override fun onNothingSelected() {}
        })

        // 초기 분석 텍스트는 가장 최근 월(6월)로 설정
        updateAnalysisText(5) // 6월 인덱스

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
                colors.add("#E8E8E8".toColorInt()) // 나머지는 회색
            }
        }

        dataSet.colors = colors

        // 선택된 바의 텍스트 색상도 파란색으로 변경
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.assu_main)

        barChart.invalidate()

        // 선택된 월에 따라 분석 텍스트 업데이트
        updateAnalysisText(selectedIndex)
    }

    // 분석 텍스트 업데이트
    private fun updateAnalysisText(selectedMonth: Int) {
        val studentCounts = arrayOf(45, 78, 92, 65, 87, 110)
        val monthNames = arrayOf("1월", "2월", "3월", "4월", "5월", "6월")

        if (selectedMonth < studentCounts.size) {
            val fullText = "${monthNames[selectedMonth]}에 ASSU와 함께 ${studentCounts[selectedMonth]}명이\n매장에서 제휴 서비스를 이용했어요"
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

// 빨간 텍스트 표시를 위한 커스텀 LineChart 렌더러
class CustomLineChartRenderer(
    chart: LineDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : LineChartRenderer(chart, animator, viewPortHandler) {

    override fun drawValues(c: Canvas) {
        if (isDrawingValuesAllowed(mChart)) {
            val dataSets = mChart.lineData.dataSets

            for (i in dataSets.indices) {
                val dataSet = dataSets[i]
                if (!shouldDrawValues(dataSet) || dataSet.entryCount < 1) continue

                // 첫 번째 데이터셋만 값을 그림 (두 번째는 빨간 라인만)
                if (i != 0) continue

                applyValueTextStyle(dataSet)

                val trans = mChart.getTransformer(dataSet.axisDependency)
                val entries = dataSet.values
                val positions = trans.generateTransformedValuesLine(
                    dataSet, mAnimator.phaseX, mAnimator.phaseY, mXBounds.min, mXBounds.max
                )

                val formatter = dataSet.valueFormatter

                for (j in 0 until positions.size step 2) {
                    val x = positions[j]
                    val y = positions[j + 1]

                    if (!mViewPortHandler.isInBoundsRight(x)) break
                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y)) continue

                    val entry = entries[j / 2]
                    val entryIndex = j / 2

                    // 마지막 항목만 빨간색으로 설정
                    if (entryIndex == entries.size - 1) {
                        mValuePaint.color = Color.RED
                    } else {
                        mValuePaint.color = dataSet.valueTextColor
                    }

                    val label = formatter.getPointLabel(entry)
                    c.drawText(label, x, y - 10f, mValuePaint)
                }
            }
        }
    }
}

// 막대그래프 radius 주기
class RoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private var radius = 0f

    fun setRadius(radius: Float) {
        this.radius = radius
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = dataSet.barBorderWidth
        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1

        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }

        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

            if (!isSingleColor) {
                mRenderPaint.color = dataSet.getColor(j / 4)
            }

            // 둥근 모서리 적용
            val rect = RectF(
                buffer.buffer[j], buffer.buffer[j + 1],
                buffer.buffer[j + 2], buffer.buffer[j + 3]
            )

            c.drawRoundRect(rect, radius, radius, mRenderPaint)

            if (drawBorder) {
                c.drawRoundRect(rect, radius, radius, mBarBorderPaint)
            }

            j += 4
        }
    }
}