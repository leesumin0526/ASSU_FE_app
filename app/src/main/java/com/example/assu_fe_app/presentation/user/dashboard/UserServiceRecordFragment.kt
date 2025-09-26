package com.example.assu_fe_app.presentation.user.dashboard

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.presentation.user.dashboard.adapter.ServiceRecordAdapter
import com.example.assu_fe_app.databinding.ActivityUserServiceRecordBinding
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.usage.MonthUsageViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class UserServiceRecordFragment : BaseFragment<ActivityUserServiceRecordBinding>(R.layout.activity_user_service_record) {

    lateinit var serviceRecordAdapter: ServiceRecordAdapter
    private val usageViewModel: MonthUsageViewModel by activityViewModels()

    private val today = LocalDate.now()
    private val currentYear = today.year
    private val currentMonth = today.monthValue

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        binding.btnServiceRecordBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.ivServiceRecordBackArrow.setOnClickListener {
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

        binding.ivServiceRecordNextArrow.setOnClickListener {
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

        initAdapter()
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
            serviceRecordAdapter.setData(records)
            binding.tvServiceCount.text = records.size.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAdapter() {
        serviceRecordAdapter = ServiceRecordAdapter()
        binding.rvServiceRecord.apply {
            adapter = serviceRecordAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateMonthUI() {
        val selectedYear = usageViewModel.getCurrentYear()
        val selectedMonth = usageViewModel.getCurrentMonth()

        binding.tvServiceRecordMonth.text = selectedMonth.toString()
        binding.tvRecordBenefitMonth.text= selectedMonth.toString()

        // 현재 달인지 확인
        val isCurrentMonth = selectedYear == currentYear && selectedMonth == currentMonth

        // 다음 화살표 상태 업데이트
        binding.ivServiceRecordNextArrow.isEnabled = !isCurrentMonth

        // 화살표 색상 변경 (활성화: 검정색, 비활성화: 회색)
        if (isCurrentMonth) {
            binding.ivServiceRecordNextArrow.alpha = 0.3f // 회색처리
        } else {
            binding.ivServiceRecordNextArrow.alpha = 1.0f // 검정색 (활성화)
        }

        // 이전 화살표는 항상 활성화 (과거로는 언제든 이동 가능)
        binding.ivServiceRecordBackArrow.isEnabled = true
        binding.ivServiceRecordBackArrow.alpha = 1.0f
    }
}