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
    private var selectedMonth = today.monthValue
    private var selectedYear = today.year
    private val currentYear = today.year
    private val currentMonth = today.monthValue


    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        // ... (기존 코드 유지)

        // 초기 UI 업데이트
        updateMonthUI()

        binding.btnServiceRecordBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.ivServiceRecordBackArrow.setOnClickListener {
            if (selectedMonth == 1) {
                selectedMonth = 12
                selectedYear -= 1
            } else {
                selectedMonth -= 1
            }
            updateMonthUI()
            usageViewModel.year = selectedYear
            usageViewModel.month = selectedMonth
            usageViewModel.getMonthUsage()
        }

        binding.ivServiceRecordNextArrow.setOnClickListener {
            val isMaxMonth = selectedYear == currentYear && selectedMonth == currentMonth
            if (!isMaxMonth) {
                if (selectedMonth == 12) {
                    selectedMonth = 1
                    selectedYear += 1
                } else {
                    selectedMonth += 1
                }
                updateMonthUI()
                usageViewModel.year = selectedYear
                usageViewModel.month = selectedMonth
                usageViewModel.getMonthUsage()
            }
        }
        initAdapter()
    }

    override fun initObserver() {
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
        binding.tvServiceRecordMonth.text = selectedMonth.toString()
        val isMaxMonth = selectedYear == currentYear && selectedMonth == currentMonth
        binding.ivServiceRecordNextArrow.isEnabled = !isMaxMonth
    }
}