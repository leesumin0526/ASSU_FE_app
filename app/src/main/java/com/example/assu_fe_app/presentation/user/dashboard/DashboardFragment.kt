package com.example.assu_fe_app.presentation.user.dashboard

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.usage.ServiceRecord
import com.example.assu_fe_app.presentation.user.dashboard.adapter.ServiceRecordAdapter
import com.example.assu_fe_app.databinding.FragmentDashboardBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.usage.MonthUsageViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {

    private lateinit var serviceRecordAdapter: ServiceRecordAdapter

    private val usageViewModel: MonthUsageViewModel by activityViewModels()
    private val today = LocalDate.now()

    private var selectedMonth = today.monthValue
    private var selectedYear = today.year
    private val currentYear = today.year
    private val currentMonth = today.monthValue

    override fun initView() {
        binding.btnSuggestService.setOnClickListener {
            val intent = Intent(requireContext(), UserServiceSuggestActivity::class.java)
            startActivity(intent)
        }

        // 초기 UI 설정 로직
        updateMonthUI()
        initAdapter()

        // 초기 데이터 로딩
        usageViewModel.getMonthUsage()


        binding.ivDashBackArrow.setOnClickListener {
            if (selectedMonth == 1) {
                selectedMonth = 12
                selectedYear -= 1
            } else {
                selectedMonth -= 1
            }
            updateMonthUI()
            // 월 변경 시에만 API 호출
            usageViewModel.year = selectedYear
            usageViewModel.month = selectedMonth
            usageViewModel.getMonthUsage()
        }

        binding.ivDashNextArrow.setOnClickListener {
            val isMaxMonth = selectedYear == currentYear && selectedMonth == currentMonth
            if (!isMaxMonth) {
                if (selectedMonth == 12) {
                    selectedMonth = 1
                    selectedYear += 1
                } else {
                    selectedMonth += 1
                }
                updateMonthUI()
                // 월 변경 시에만 API 호출
                usageViewModel.year = selectedYear
                usageViewModel.month = selectedMonth
                usageViewModel.getMonthUsage()
            }
        }

        binding.tvDashSeeAll.setOnClickListener { view ->
            findNavController().navigate(R.id.action_dashboardFragment_to_userServiceRecordFragment)
        }
    }

    override fun initObserver() {
        usageViewModel.recordList.observe(viewLifecycleOwner) { records ->
            // 최대 3개의 레코드만 가져와 어댑터에 설정
            val limitedRecords = if (records.size > 3) records.take(3) else records
            serviceRecordAdapter.setData(limitedRecords)

            // UI 업데이트: 총 서비스 이용 건수 및 '전체 보기' 버튼 가시성
            binding.tvServiceCount.text = records.size.toString()
            updateUI(records.size)
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
        // ViewModel에 있는 월/연도 데이터를 UI에 반영
        binding.tvDashMonth.text = selectedMonth.toString()
        val isMaxMonth = selectedYear == currentYear && selectedMonth == currentMonth
        binding.ivDashNextArrow.isEnabled = !isMaxMonth
    }
}

