package com.example.assu_fe_app.presentation.user.dashboard

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.servicerecord.ServiceRecord
import com.example.assu_fe_app.presentation.user.dashboard.adapter.ServiceRecordAdapter
import com.example.assu_fe_app.databinding.FragmentDashboardBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import java.time.LocalDate
import java.time.LocalDateTime
@RequiresApi(Build.VERSION_CODES.O)
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {
    lateinit var serviceRecordAdapter: ServiceRecordAdapter

    private var today = LocalDate.now()

    private var selectedMonth = today.monthValue
    private var selectedYear = today.year
    private var currentYear = today.year
    private var currentMonth = today.monthValue


    override fun initObserver() {

    }


    override fun initView(){
        Log.d("fragment initView", "initView")
        binding.btnSuggestService.setOnClickListener {
            val intent = Intent(requireContext(), UserServiceSuggestActivity::class.java)
            startActivity(intent)
        }

        // 현재 월로 초기화
        binding.tvDashMonth.text = currentMonth.toString()

        // 리사이클러뷰 어댑터 조기화
        initAdapter()


        // 이전달로 넘어가는 화살표
        binding.ivDashBackArrow.setOnClickListener {
            if (selectedMonth == 1) {
                selectedMonth = 12
                selectedYear -= 1
            } else {
                selectedMonth -= 1
            }
            updateMonthUI()
        }

        // 다음달로 넘어가는 화살표
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
            }
        }


        // 제휴 내역 전체 보기
        binding.tvDashSeeAll.setOnClickListener {
            val intent = Intent(requireContext(), UserServiceRecordActivity::class.java)
            startActivity(intent)
        }
        updateUI()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAdapter(){
        serviceRecordAdapter = ServiceRecordAdapter()
        binding.rvServiceRecord.apply {
            adapter = serviceRecordAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        serviceRecordAdapter.setData(createDummyData())

        binding.tvServiceCount.text = serviceRecordAdapter.itemCount.toString()
    }


//
    private fun updateUI(){
        if(serviceRecordAdapter.itemCount >=3 ){
            binding.tvDashSeeAll.visibility = View.VISIBLE
        }
    }

    private fun updateMonthUI() {
        // 화면 표시
        binding.tvDashMonth.text = selectedMonth.toString()

        // 다음 버튼 활성화 조건
        val isMaxMonth = selectedYear == currentYear && selectedMonth == currentMonth
        binding.ivDashNextArrow.isEnabled = !isMaxMonth

        if (binding.ivDashNextArrow.isEnabled) {
            binding.ivDashNextArrow.setImageResource(R.drawable.ic_small_arrow_right_able)
        } else {
            binding.ivDashNextArrow.setImageResource(R.drawable.ic_dash_small_next_disable)
        }

        // API 호출

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDummyData() : List<ServiceRecord>{
        return listOf(
            ServiceRecord(
                "숑숑 돈가스",
                "음료 한병을 제공받았어요",
                LocalDateTime.now(),
                false
            ),
            ServiceRecord(
                "밀플랜비",
                "소스를 제공받았어요",
                LocalDateTime.now(),
                false
            )
            ,
            ServiceRecord(
                "밀플랜비",
                "음료를 제공받았어요",
                LocalDateTime.now(),
                false
            )
        )
    }

}