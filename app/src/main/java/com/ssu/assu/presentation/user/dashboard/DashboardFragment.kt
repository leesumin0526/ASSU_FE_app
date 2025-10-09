package com.ssu.assu.presentation.user.dashboard

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssu.assu.R
import com.ssu.assu.presentation.user.dashboard.adapter.ServiceRecordAdapter
import com.ssu.assu.databinding.FragmentDashboardBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.ui.usage.MonthUsageViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {

    private lateinit var serviceRecordAdapter: ServiceRecordAdapter
    private val usageViewModel: MonthUsageViewModel by activityViewModels()

    private val today = LocalDate.now()
    private val currentYear = today.year
    private val currentMonth = today.monthValue

    override fun initView() {
        binding.btnSuggestService.setOnClickListener {
            val intent = Intent(requireContext(), UserServiceSuggestActivity::class.java)
            startActivity(intent)
        }

        // 초기 UI 설정 로직
        initAdapter()

        // 초기 날짜 설정 (처음 실행시에만)
        if (usageViewModel.getCurrentYear() == 2025 && usageViewModel.getCurrentMonth() == 9) {
            usageViewModel.updateSelectedDate(currentYear, currentMonth)
        }

        // 초기 데이터 로딩
        usageViewModel.getMonthUsage()

        binding.ivDashBackArrow.setOnClickListener {
            val currentSelectedYear = usageViewModel.getCurrentYear()
            val currentSelectedMonth = usageViewModel.getCurrentMonth()

            val newYear: Int
            val newMonth: Int

            if (currentSelectedMonth == 1) {
                newMonth = 12
                newYear = currentSelectedYear - 1
            } else {
                newMonth = currentSelectedMonth - 1
                newYear = currentSelectedYear
            }

            usageViewModel.updateSelectedDate(newYear, newMonth)
            usageViewModel.getMonthUsage()
        }

        binding.ivDashNextArrow.setOnClickListener {
            val currentSelectedYear = usageViewModel.getCurrentYear()
            val currentSelectedMonth = usageViewModel.getCurrentMonth()

            val isMaxMonth = currentSelectedYear == currentYear && currentSelectedMonth == currentMonth
            if (!isMaxMonth) {
                val newYear: Int
                val newMonth: Int

                if (currentSelectedMonth == 12) {
                    newMonth = 1
                    newYear = currentSelectedYear + 1
                } else {
                    newMonth = currentSelectedMonth + 1
                    newYear = currentSelectedYear
                }

                usageViewModel.updateSelectedDate(newYear, newMonth)
                usageViewModel.getMonthUsage()
            }
        }

        binding.tvDashSeeAll.setOnClickListener { view ->
            findNavController().navigate(R.id.action_dashboardFragment_to_userServiceRecordFragment)
        }
    }

    override fun initObserver() {
        // 월/연도 변경 관찰
        usageViewModel.selectedYear.observe(viewLifecycleOwner) { year ->
            updateMonthUI()
        }

        usageViewModel.selectedMonth.observe(viewLifecycleOwner) { month ->
            updateMonthUI()
        }

        usageViewModel.recordList.observe(viewLifecycleOwner) { records ->
            // records 리스트가 비어있는지 확인
            if (records.isNullOrEmpty()) {
                // 1. 데이터가 없을 때 UI 처리
                binding.flBenefit.visibility = View.GONE         // RecyclerView 숨기기
                binding.flNoneBenefit.visibility = View.VISIBLE   // '받은 혜택 없음' 레이아웃 보이기
                binding.tvServiceCount.text = "0"                 // 서비스 카운트 0으로 설정
                updateUI(0)                                     // '전체 보기' 버튼 숨기기
            } else {
                // 2. 데이터가 있을 때 UI 처리
                binding.flBenefit.visibility = View.VISIBLE      // RecyclerView 보이기
                binding.flNoneBenefit.visibility = View.GONE     // '받은 혜택 없음' 레이아웃 숨기기

                // 기존 로직 수행
                val limitedRecords = if (records.size > 3) records.take(3) else records
                serviceRecordAdapter.setData(limitedRecords)

                binding.tvServiceCount.text = records.size.toString()
                updateUI(records.size) // '전체 보기' 버튼 상태 업데이트
            }
        }
    }

    private fun initAdapter() {
        serviceRecordAdapter = ServiceRecordAdapter()
        binding.rvServiceRecord.apply {
            adapter = serviceRecordAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateUI(recordCount: Int) {
        // 총 레코드 수가 3개 초과일 때만 '전체 보기' 버튼을 표시
        binding.tvDashSeeAll.visibility = if (recordCount > 3) View.VISIBLE else View.GONE
    }

    private fun updateMonthUI() {
        val selectedYear = usageViewModel.getCurrentYear()
        val selectedMonth = usageViewModel.getCurrentMonth()

        binding.tvDashMonth.text = selectedMonth.toString()
        binding.tvServiceBenefitMonth.text = selectedMonth.toString()

        // 현재 달인지 확인
        val isCurrentMonth = selectedYear == currentYear && selectedMonth == currentMonth

        // 다음 화살표 상태 업데이트
        binding.ivDashNextArrow.isEnabled = !isCurrentMonth

        // 화살표 색상 변경 (활성화: 검정색, 비활성화: 회색)
        if (isCurrentMonth) {
            binding.ivDashNextArrow.alpha = 0.3f // 회색처리
        } else {
            binding.ivDashNextArrow.alpha = 1.0f // 검정색 (활성화)
        }

        // 이전 화살표는 항상 활성화 (과거로는 언제든 이동 가능)
        binding.ivDashBackArrow.isEnabled = true
        binding.ivDashBackArrow.alpha = 1.0f
    }
}