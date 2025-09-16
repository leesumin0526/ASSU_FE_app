package com.example.assu_fe_app.presentation.common.mypage

// ❌ import android.app.Fragment  제거
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserCustomerServiceBinding
import com.example.assu_fe_app.domain.model.inquiry.InquiryModel
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.inquiry.InquiryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomerServiceDialogFragment :
    BaseFragment<FragmentUserCustomerServiceBinding>(R.layout.fragment_user_customer_service) {

    private val vm: InquiryViewModel by activityViewModels()
    private lateinit var historyAdapter: InquiryHistoryAdapter

    override fun initObserver() { /* 필요 시 */ }

    // ✅ BaseFragment가 이미 DataBinding inflate → 여기서 바로 binding 사용
    override fun initView() {
        setupTabs()
        setupInquirySubmit()
        setupHistoryList()
        showInquiryTab()
    }

    /** 문의하기 탭 - 제출 */
    private fun setupInquirySubmit() = with(binding.inquiryLayout) {
        btnSubmitInquiry.setOnClickListener {
            val title = etInquiryTitle.text.toString().trim()
            val content = etInquiryContent.text.toString().trim()
            val email = etInquiryEmail.text.toString().trim()

            if (title.isEmpty() || content.isEmpty() || email.isEmpty()) {
                return@setOnClickListener
            }
            vm.create(title, content, email)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.createResult.collect { newId ->
                    etInquiryTitle.setText("")
                    etInquiryContent.setText("")
                    etInquiryEmail.setText("")
                    showHistoryTab()
                    vm.refresh(status = "all")
                }
            }
        }
    }

    /** 내역 탭 */
    private fun setupHistoryList() = with(binding.historyLayout) {
        historyAdapter = InquiryHistoryAdapter(onClick = ::openDetail)
        rvInquiryHistory.layoutManager = LinearLayoutManager(requireContext())
        rvInquiryHistory.adapter = historyAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.list.collectLatest { st -> historyAdapter.submitList(st.items) }
            }
        }

        rvInquiryHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val lm = rv.layoutManager as LinearLayoutManager
                val last = lm.findLastVisibleItemPosition()
                val total = rv.adapter?.itemCount ?: 0
                if (total > 0 && last >= total - 3) vm.loadMore()
            }
        })
    }

    private fun openDetail(item: InquiryModel) {
        InquiryDetailDialogFragment.newInstance(item.id)
            .show(childFragmentManager, "inquiry_detail")
    }

    /** 탭 스위칭 */
    private fun setupTabs() = with(binding) {
        tabInquiry.setOnClickListener { showInquiryTab() }
        tabHistory.setOnClickListener {
            showHistoryTab()
            if (vm.list.value.items.isEmpty()) vm.refresh(status = "all")
        }
        btnCsBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }

    private fun showInquiryTab() = with(binding) {
        inquiryLayout.root.visibility = View.VISIBLE
        historyLayout.root.visibility = View.GONE

        // 선택 탭 = 메인색, 비선택 탭 = 서브색
        tvTabInquiry.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_main))
        tvTabHistory.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
        tabInquiryBottomLine.visibility = View.VISIBLE
        tabHistoryBottomLine.visibility = View.GONE
    }

    private fun showHistoryTab() = with(binding) {
        inquiryLayout.root.visibility = View.GONE
        historyLayout.root.visibility = View.VISIBLE

        tvTabInquiry.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
        tvTabHistory.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_main))
        tabInquiryBottomLine.visibility = View.GONE
        tabHistoryBottomLine.visibility = View.VISIBLE
    }
}