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
                content = "제휴 신청 시 필요한 추가 정보가 있는지 궁금합니다. 사업자등록증과 대표자 신분증 외에 다른 서류가 필요한지, 그리고 제휴 신청 후 승인까지 얼마나 걸리는지도 궁금합니다. 또한 제휴 신청 시 필요한 서류들을 미리 준비하고 싶어서 문의드립니다.",
                email = "example@gmail.com",
                date = "2025-03-14",
                time = "15:22",
                status = InquiryStatus.COMPLETED,
                answer = "안녕하세요. A:SSU입니다.\n\n제휴 신청 시 필요한 서류는 다음과 같습니다:\n\n📋 필수 서류\n• 사업자등록증 사본\n• 대표자 신분증 사본\n• 매장 내/외부 사진 (최소 3장)\n• 매장 위치 정보\n\n⏰ 승인 소요 기간\n• 서류 검토: 3-5일\n• 현장 확인: 1-2일\n• 최종 승인: 1일\n• 총 소요 기간: 약 5-8일\n\n추가 문의사항이 있으시면 언제든 연락주세요. 감사합니다."
            ),
            InquiryItem(
                id = "3",
                title = "제휴 인증이 안돼요 ㅜ",
                content = "제휴 인증 과정에서 문제가 발생했습니다. 사업자등록증을 업로드했는데 '파일 형식이 올바르지 않습니다'라는 오류가 계속 뜨고 있어요. 어떤 형식으로 업로드해야 하나요? 파일 크기도 확인했는데 5MB 정도로 적당한 크기인 것 같은데 계속 오류가 발생하고 있습니다.",
                email = "example@gmail.com",
                date = "2025-03-13",
                time = "11:45",
                status = InquiryStatus.COMPLETED,
                answer = "안녕하세요. A:SSU입니다.\n\n파일 업로드 오류로 불편을 드려 죄송합니다.\n\n📁 지원 파일 형식\n• 이미지: JPG, JPEG, PNG (최대 10MB)\n• PDF: PDF (최대 10MB)\n\n🔧 해결 방법\n1. 파일 크기가 10MB 이하인지 확인\n2. 파일명에 특수문자나 한글이 포함되지 않았는지 확인\n3. 인터넷 연결 상태 확인 후 재시도\n4. 다른 브라우저로 시도\n\n위 방법으로도 해결되지 않으면 고객센터로 연락주시면 직접 도움드리겠습니다."
            ),
            InquiryItem(
                id = "4",
                title = "매장 정보 수정 요청",
                content = "매장 주소가 변경되어서 정보를 수정하고 싶습니다. 기존 주소는 서울시 강남구 테헤란로 123이고, 새 주소는 서울시 강남구 테헤란로 456입니다. 어떻게 수정할 수 있나요? 매장 전화번호도 함께 변경되었는데 이것도 같이 수정할 수 있나요?",
                email = "example@gmail.com",
                date = "2025-03-12",
                time = "09:30",
                status = InquiryStatus.COMPLETED,
                answer = "안녕하세요. A:SSU입니다.\n\n매장 정보 수정 요청 접수하였습니다.\n\n✅ 수정 완료 사항\n• 매장 주소: 서울시 강남구 테헤란로 456으로 변경\n• 매장 전화번호: 02-1234-5678로 변경\n• 변경일: 2025-03-12\n\n📝 추가 확인 사항\n• 영업시간 변경 여부\n• 메뉴 가격 변동 여부\n\n위 사항들도 변경이 필요하시면 추가로 문의해 주세요. 변경사항은 24시간 내에 반영됩니다."
            ),
            InquiryItem(
                id = "5",
                title = "제휴 해지 신청",
                content = "개인적인 사정으로 제휴를 해지하고 싶습니다. 제휴 해지 절차와 필요한 서류를 알려주세요. 현재 사용 중인 포인트는 어떻게 처리되나요?",
                email = "example@gmail.com",
                date = "2025-03-11",
                time = "16:15",
                status = InquiryStatus.COMPLETED,
                answer = "안녕하세요. A:SSU입니다.\n\n제휴 해지 신청 접수하였습니다.\n\n📋 해지 신청 서류\n• 제휴 해지 신청서 (첨부파일 다운로드)\n• 대표자 신분증 사본\n• 사업자등록증 사본\n\n⏰ 처리 절차\n1. 서류 접수 및 검토: 2-3일\n2. 해지 승인 및 처리: 1일\n3. 최종 완료: 총 3-4일\n\n💡 참고사항\n• 해지 후 30일 이내 재신청 가능\n• 기존 제휴 혜택은 해지일까지 유지\n• 미사용 포인트는 환불 처리\n\n서류 작성에 어려움이 있으시면 언제든 연락주세요."
            ),
            InquiryItem(
                id = "6",
                title = "앱 로그인 오류",
                content = "앱에서 로그인이 안 되고 있어요. 비밀번호를 잊어버려서 재설정하려고 하는데 이메일 인증이 안 오고 있습니다. 어떻게 해야 하나요? 스팸메일함도 확인했는데 없고, 이메일 주소도 정확히 입력했는데 인증 메일이 오지 않습니다.",
                email = "example@gmail.com",
                date = "2025-03-10",
                time = "14:20",
                status = InquiryStatus.PENDING
            ),
            InquiryItem(
                id = "7",
                title = "결제 오류 문의",
                content = "앱에서 결제를 시도했는데 '결제 처리 중 오류가 발생했습니다'라는 메시지가 나오고 결제가 완료되지 않습니다. 카드 정보는 정확히 입력했고, 카드 잔액도 충분한데 왜 이런 오류가 발생하는지 궁금합니다.",
                email = "example@gmail.com",
                date = "2025-03-09",
                time = "20:15",
                status = InquiryStatus.COMPLETED,
                answer = "안녕하세요. A:SSU입니다.\n\n결제 오류로 불편을 드려 죄송합니다.\n\n🔍 확인 사항\n• 카드 정보 입력 오타 여부\n• 카드 한도 초과 여부\n• 결제 금액이 카드사 한도 내인지 확인\n\n🔧 해결 방법\n1. 앱 재시작 후 재시도\n2. 다른 카드로 결제 시도\n3. 카드사에 결제 차단 여부 문의\n\n위 방법으로도 해결되지 않으면 고객센터로 연락주시면 직접 도움드리겠습니다."
            ),
            InquiryItem(
                id = "8",
                title = "배송 문의",
                content = "주문한 상품이 예상 배송일보다 늦게 도착했습니다. 배송 상태를 확인해보니 '배송 중'으로 되어 있는데, 정확한 배송 위치와 도착 예정일을 알 수 있을까요?",
                email = "example@gmail.com",
                date = "2025-03-08",
                time = "12:30",
                status = InquiryStatus.COMPLETED,
                answer = "안녕하세요. A:SSU입니다.\n\n배송 지연으로 불편을 드려 죄송합니다.\n\n📦 배송 현황\n• 현재 위치: 서울시 강남구 배송센터\n• 예상 도착: 오늘 오후 2-4시\n• 배송업체: 대한통운\n\n📞 추가 문의\n배송 관련 추가 문의사항이 있으시면 배송업체 고객센터(1588-1255)로 연락주시거나 저희 고객센터로 문의해 주세요."
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
