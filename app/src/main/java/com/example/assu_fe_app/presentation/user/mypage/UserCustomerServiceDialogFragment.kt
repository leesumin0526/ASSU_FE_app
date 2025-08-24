package com.example.assu_fe_app.presentation.user.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.data.dto.InquiryItem
import com.example.assu_fe_app.data.dto.InquiryStatus
import com.example.assu_fe_app.databinding.FragmentUserCustomerServiceBinding

class UserCustomerServiceDialogFragment : DialogFragment() {

    private var _binding: FragmentUserCustomerServiceBinding? = null
    private val binding get() = _binding!!

    private lateinit var inquiryHistoryAdapter: InquiryHistoryAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserCustomerServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupTabNavigation()
        setupInquiryHistory()
        loadSampleInquiryData()
    }

    private fun setupViews() {
        // 뒤로가기 버튼
        binding.btnCsBack.setOnClickListener {
            dismiss()
        }

        // 문의하기 작성하기 버튼
        binding.inquiryLayout.btnSubmitInquiry.setOnClickListener {
            submitInquiry()
        }
    }

    private fun setupTabNavigation() {
        // 문의하기 탭 클릭
        binding.tabInquiry.setOnClickListener {
            switchToInquiryTab()
        }

        // 문의내역확인 탭 클릭
        binding.tabHistory.setOnClickListener {
            switchToHistoryTab()
        }

        // 기본적으로 문의하기 탭 선택
        switchToInquiryTab()
    }

    private fun submitInquiry() {
        val title = binding.inquiryLayout.etInquiryTitle.text.toString().trim()
        val content = binding.inquiryLayout.etInquiryContent.text.toString().trim()
        val email = binding.inquiryLayout.etInquiryEmail.text.toString().trim()

        // 입력 검증
        when {
            title.isEmpty() -> {
                binding.inquiryLayout.etInquiryTitle.error = "제목을 입력해주세요"
                return
            }
            content.isEmpty() -> {
                binding.inquiryLayout.etInquiryContent.error = "문의 내용을 입력해주세요"
                return
            }
            email.isEmpty() -> {
                binding.inquiryLayout.etInquiryEmail.error = "이메일을 입력해주세요"
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.inquiryLayout.etInquiryEmail.error = "올바른 이메일 형식을 입력해주세요"
                return
            }
        }

        // TODO: 실제 서버에 문의사항 전송
        // 여기서는 간단히 토스트 메시지만 표시
        android.widget.Toast.makeText(requireContext(), "문의사항이 성공적으로 등록되었습니다.", android.widget.Toast.LENGTH_SHORT).show()

        // 입력 필드 초기화
        binding.inquiryLayout.etInquiryTitle.text.clear()
        binding.inquiryLayout.etInquiryContent.text.clear()
        binding.inquiryLayout.etInquiryEmail.text.clear()

        // 문의내역확인 탭으로 이동
        switchToHistoryTab()
    }

    private fun switchToInquiryTab() {
        // 탭 텍스트 색상 변경
        binding.tvTabInquiry.setTextColor(resources.getColor(com.example.assu_fe_app.R.color.assu_font_main, null))
        binding.tvTabHistory.setTextColor(resources.getColor(com.example.assu_fe_app.R.color.assu_font_sub, null))

        // 탭 하단 라인 표시/숨김
        binding.tabInquiryBottomLine.visibility = View.VISIBLE
        binding.tabHistoryBottomLine.visibility = View.GONE

        // 컨텐츠 변경
        binding.inquiryLayout.root.visibility = View.VISIBLE
        binding.historyLayout.root.visibility = View.GONE
    }

    private fun setupInquiryHistory() {
        inquiryHistoryAdapter = InquiryHistoryAdapter(
            onItemClick = { inquiryItem ->
                // 문의내역 클릭 시 상세보기 다이얼로그 표시
                val detailDialogFragment = UserInquiryDetailDialogFragment.newInstance(inquiryItem)
                detailDialogFragment.show(childFragmentManager, "InquiryDetailDialog")
            }
        )

        binding.historyLayout.rvInquiryHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = inquiryHistoryAdapter
            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(requireContext(), androidx.recyclerview.widget.DividerItemDecoration.VERTICAL).apply {
                    setDrawable(resources.getDrawable(com.example.assu_fe_app.R.drawable.divider_inquiry_item, null))
                }
            )
        }
    }

    private fun loadSampleInquiryData() {
        val sampleInquiries = listOf(
            InquiryItem(
                id = "1",
                title = "상호명 변경 문의드립니다.",
                content = "안녕하세요. 역전할머니 맥주 숭실대점 대표 이수민입니다. 다름이 아니라 상호명이 역전할머니 맥주 숭실대점에서 역후할머니 맥주 숭실대점으로 변경하게 되어 이를 변경 요청하고자 문의드렸습니다. 감사합니다.",
                email = "example@gmail.com",
                date = "2025-03-15",
                time = "18:36",
                status = InquiryStatus.PENDING
            ),
            InquiryItem(
                id = "2",
                title = "제휴 관련 추가정보 문의 드립니다.",
                content = "제휴 신청 시 필요한 추가 정보가 있는지 궁금합니다.",
                email = "example@gmail.com",
                date = "2025-03-15",
                time = "18:36",
                status = InquiryStatus.COMPLETED,
                answer = "안녕하세요. A:SSU입니다.\n\n제휴 신청 시 필요한 추가 정보는 다음과 같습니다:\n1. 사업자등록증\n2. 대표자 신분증\n3. 매장 사진\n\n추가 문의사항이 있으시면 언제든 연락주세요."
            ),
            InquiryItem(
                id = "3",
                title = "제휴 인증이 안돼요 ㅜ",
                content = "제휴 인증 과정에서 문제가 발생했습니다. 도움을 받을 수 있을까요?",
                email = "example@gmail.com",
                date = "2025-03-15",
                time = "18:36",
                status = InquiryStatus.COMPLETED,
                answer = "안녕하세요. A:SSU입니다.\n\n제휴 인증 문제를 해결해드리겠습니다. 구체적인 오류 메시지나 상황을 알려주시면 더 정확한 도움을 드릴 수 있습니다."
            )
        )

        inquiryHistoryAdapter.submitList(sampleInquiries)
        
        // 데이터가 없을 때 빈 상태 UI 표시
        if (sampleInquiries.isEmpty()) {
            binding.historyLayout.emptyStateContainer.visibility = View.VISIBLE
            binding.historyLayout.rvInquiryHistory.visibility = View.GONE
        } else {
            binding.historyLayout.emptyStateContainer.visibility = View.GONE
            binding.historyLayout.rvInquiryHistory.visibility = View.VISIBLE
        }
    }

    private fun switchToHistoryTab() {
        // 탭 텍스트 색상 변경
        binding.tvTabInquiry.setTextColor(resources.getColor(com.example.assu_fe_app.R.color.assu_font_sub, null))
        binding.tvTabHistory.setTextColor(resources.getColor(com.example.assu_fe_app.R.color.assu_font_main, null))

        // 탭 하단 라인 표시/숨김
        binding.tabInquiryBottomLine.visibility = View.GONE
        binding.tabHistoryBottomLine.visibility = View.VISIBLE

        // 컨텐츠 변경
        binding.inquiryLayout.root.visibility = View.GONE
        binding.historyLayout.root.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
