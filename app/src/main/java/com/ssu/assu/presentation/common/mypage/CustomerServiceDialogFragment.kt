package com.ssu.assu.presentation.common.mypage

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentUserCustomerServiceBinding
import com.ssu.assu.domain.model.inquiry.InquiryModel
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.ui.inquiry.InquiryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomerServiceDialogFragment :
    BaseFragment<FragmentUserCustomerServiceBinding>(R.layout.fragment_user_customer_service) {

    private val vm: InquiryViewModel by activityViewModels()
    private lateinit var historyAdapter: InquiryHistoryAdapter

    override fun initObserver() { /* 필요 시 */ }

    override fun initView() {
        setupTabs()
        setupInquirySubmit()
        setupHistoryList()
        showInquiryTab()
        bindLoading()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val root = view.findViewById<View>(R.id.root_container)
        val inquiryRoot = binding.inquiryLayout.root
        val scroll = inquiryRoot.findViewById<ScrollView>(R.id.scroll_view)
        val button = inquiryRoot.findViewById<View>(R.id.btn_submit_inquiry)

        scroll.clipToPadding = false

        // ✅ adjustNothing으로 전체 레이아웃은 안 움직이게 (버튼 고정)
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED or
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )

        var lastImeVisible = false

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val sysHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom

            // ✅ IME가 보일 때 ScrollView에 패딩을 줘서 가려지지 않게 함
            scroll.setPadding(
                scroll.paddingLeft,
                scroll.paddingTop,
                scroll.paddingRight,
                if (imeVisible) imeHeight else sysHeight
            )

            // ✅ 버튼 표시 제어
            if (imeVisible && !lastImeVisible) {
                // 키보드가 막 올라올 때
                button.visibility = View.GONE

                // 현재 포커스된 EditText 자동으로 보이게 스크롤
                root.post {
                    val focused = root.findFocus()
                    if (focused != null && focused.isShown) {
                        val rect = Rect()
                        focused.getDrawingRect(rect)
                        scroll.offsetDescendantRectToMyCoords(focused, rect)
                        scroll.smoothScrollTo(0, rect.bottom)
                    }
                }
            } else if (!imeVisible && lastImeVisible) {
                // 키보드가 내려갈 때 버튼 다시 표시
                button.visibility = View.VISIBLE
            }

            lastImeVisible = imeVisible
            insets
        }
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
            showLoading("등록 중...")
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
                vm.list.collectLatest { st -> historyAdapter.submitList(st.items.toList()) }
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

    private fun bindLoading() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // creating, list.loading 동시에 관찰
                launch {
                    vm.creating.collectLatest { creating ->
                        if (creating) showLoading("등록 중...") else hideLoading()
                    }
                }
                launch {
                    vm.list.collectLatest { st ->
                        // "등록 중"일 땐 creating 수집기가 우선 처리하므로 여기선 등록 중이 아닐 때만 동작
                        if (!vm.creating.value) {
                            if (st.loading) {
                                showLoading("로딩 중...")
                            } else {
                                hideLoading()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(message: String = "등록 중...") {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.tvLoadingText.text = message
    }

    private fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
    }
}