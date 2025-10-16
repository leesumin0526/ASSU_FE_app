package com.ssu.assu.presentation.common.chatting.proposal

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentServiceProposalTermWritingBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.common.chatting.ChattingSentProposalFragment
import com.ssu.assu.ui.partnership.PartnershipViewModel
import com.ssu.assu.ui.partnership.WritePartnershipUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@AndroidEntryPoint
class ServiceProposalTermWritingFragment
    : BaseFragment<FragmentServiceProposalTermWritingBinding>(R.layout.fragment_service_proposal_term_writing) {
    private val viewModel: PartnershipViewModel by activityViewModels()
    private var isEditMode: Boolean = false
    @RequiresApi(Build.VERSION_CODES.O)
    private val KOREAN_FMT = DateTimeFormatter.ofPattern("yyyy - MM - dd")
    @RequiresApi(Build.VERSION_CODES.O)
    private val ACCEPTED_FORMATS = listOf(
        DateTimeFormatter.ISO_LOCAL_DATE,                 // 2025-10-16
        DateTimeFormatter.ofPattern("yyyy - MM - dd"),    // 2025 - 10 - 16
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),        // 2025/10/16
        DateTimeFormatter.ofPattern("yyyy.MM.dd")         // 2025.10.16
    )

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. 버튼 활성화 상태를 ViewModel로부터 구독합니다.
                launch {
                    viewModel.isSubmitButtonEnabled.collect { isEnabled ->
                        binding.btnCompleted.isEnabled = isEnabled
                        val tintRes = if (isEnabled) R.color.assu_main else R.color.assu_sub
                        binding.btnCompleted.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), tintRes)
                    }
                }

                // 2. API 호출 결과 상태를 구독하여 화면 이동을 처리합니다.
                launch {
                    viewModel.writePartnershipState.collect { state ->
                        when (state) {
                            is WritePartnershipUiState.Loading -> {
                                binding.btnCompleted.isEnabled = false
                            }
                            is WritePartnershipUiState.Success -> {
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.chatting_fragment_container, ChattingSentProposalFragment())
                                    .addToBackStack(null)
                                    .commit()
                                viewModel.resetWritePartnershipState()
                            }
                            is WritePartnershipUiState.Fail, is WritePartnershipUiState.Error -> {
                                Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                viewModel.resetWritePartnershipState()
                            }
                            else -> { /* Idle */ }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        // ✅ arguments에서 수정 모드 여부 확인
        arguments?.let { bundle ->
            isEditMode = bundle.getBoolean("isEditMode", false)
            Log.d("ServiceProposalTermWritingFragment", "Edit mode: $isEditMode")
        }

        binding.tvCompleted.text = if (isEditMode) "제안서 수정하기" else "제안서 보내기"
        binding.lifecycleOwner = viewLifecycleOwner

        updateSummaryText()

        if (isEditMode) {
            loadExistingValues()
        }

        val startInit = parseFlexibleLocalDateOrNull(viewModel.partnershipStartDate.value)
        val endInit   = parseFlexibleLocalDateOrNull(viewModel.partnershipEndDate.value)

        // 세 개 모두 시작일 DatePicker 트리거로 묶기
        bindDateTriggers(
            triggers = listOf(
                binding.ivFragmentServiceProposalContent,   // 아이콘/이미지뷰
                binding.etFragmentServiceProposalContent2,  // 실제 입력 필드
                binding.ivFragmentServiceProposalCalendar   // 캘린더 아이콘
            ),
            target = binding.etFragmentServiceProposalContent2, // 선택 결과가 들어갈 곳
            defaultDate = startInit,
            minDateMillis = todayMillis() // 오늘부터 선택 가능
        ) { picked ->
            viewModel.partnershipStartDate.value = picked.format(KOREAN_FMT)
        }

        bindDateTriggers(
            triggers = listOf(
                binding.ivFragmentServiceProposalContent3,
                binding.ivFragmentServiceProposalContent4,
                binding.ivFragmentServiceProposalCalendar2),
            target = binding.ivFragmentServiceProposalContent4,
            defaultDate = endInit,
            // 시작일 이후만 선택 가능하게 하려면, 클릭 시점에 start 값을 파싱해 전달
            minDateMillis = viewModel.partnershipStartDate.value
                .takeIf { it.isNotBlank() }?.let { raw ->
                    parseFlexibleLocalDateOrNull(raw)?.let { d ->
                        Calendar.getInstance().apply {
                            set(d.year, d.monthValue - 1, d.dayOfMonth, 0, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                    }
                } ?: todayMillis()
        ) { picked ->
            viewModel.partnershipEndDate.value = picked.format(KOREAN_FMT)
        }

//        binding.etFragmentServiceProposalSign4.addTextChangedListener { text ->
//            viewModel.signature.value = text.toString()
//        } // TODO: (인) 표시 처리에 따라 수정

        binding.btnCompleted.setOnClickListener {
            viewModel.onNextButtonClicked()
        }

        binding.ivFragmentServiceProposalBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun updateSummaryText() {
        val adminName = viewModel.adminName.value
        val partnerName = viewModel.partnerName.value

        val summaryText = buildString {
            append("위와 같이 ")
            append(adminName.ifEmpty { "-" })
            append("와의\n제휴를 제안합니다.\n\n")
            append(partnerName.ifEmpty { "-" })
            append(" 대표")
        }
        binding.tvFragmentServiceProposalSign2.text = summaryText

        binding.tvFragmentServiceProposalSign3.visibility = View.GONE
        binding.etFragmentServiceProposalSign4.visibility = View.GONE
    }

    private fun loadExistingValues() {
        // ViewModel에서 기존 값 가져와서 EditText에 설정
        val startDate = viewModel.partnershipStartDate.value
        val endDate = viewModel.partnershipEndDate.value
        val signature = viewModel.partnerName.value // 기본값으로 가게 이름 설정

        if (startDate.isNotEmpty()) {
            binding.etFragmentServiceProposalContent2.setText(startDate)
        }
        if (endDate.isNotEmpty()) {
            binding.ivFragmentServiceProposalContent4.setText(endDate)
        }
        if (signature.isNotEmpty()) {
            binding.etFragmentServiceProposalSign4.setText(signature)
        }

        Log.d("ServiceProposalTermWritingFragment",
            "Loaded values - Start: $startDate, End: $endDate, Sign: $signature")
    }

    companion object {
        // ✅ 수정 모드용 newInstance
        fun newInstanceForEdit(): ServiceProposalTermWritingFragment {
            return ServiceProposalTermWritingFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isEditMode", true)
                }
            }
        }
    }

    // 공통 DatePicker 호출
    @RequiresApi(Build.VERSION_CODES.O)
    private fun todayMillis(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker(
        defaultDate: LocalDate? = null,
        minDateMillis: Long? = null,
        maxDateMillis: Long? = null,
        onPicked: (LocalDate) -> Unit
    ) {
        val base = defaultDate ?: LocalDate.now()
        val dlg = DatePickerDialog(
            requireContext(),
            R.style.AssuDatePickerTheme,
            { _, y, m, d -> onPicked(LocalDate.of(y, m + 1, d)) },
            base.year, base.monthValue - 1, base.dayOfMonth
        )
        minDateMillis?.let { dlg.datePicker.minDate = it }
        maxDateMillis?.let { dlg.datePicker.maxDate = it }
        dlg.show()
    }

    /** 여러 트리거(View)를 하나의 타깃(TextView/EditText)에 묶어서 DatePicker를 띄운다 */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindDateTriggers(
        triggers: List<View>,
        target: TextView,
        defaultDate: LocalDate? = null,
        minDateMillis: Long? = null,
        maxDateMillis: Long? = null,
        onPickedExtra: (LocalDate) -> Unit = {}
    ) {
        if (target is EditText) {
            target.inputType = EditorInfo.TYPE_NULL
            target.isFocusable = false
            target.isCursorVisible = false
        }
        val listener = View.OnClickListener {
            showDatePicker(defaultDate, minDateMillis, maxDateMillis) { picked ->
                target.text = picked.format(KOREAN_FMT)
                onPickedExtra(picked)
            }
        }
        triggers.forEach { it.setOnClickListener(listener) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseFlexibleLocalDateOrNull(raw: String?): LocalDate? {
        if (raw.isNullOrBlank()) return null
        val s = raw.trim()
        // 1) 준비한 포맷들 시도
        for (fmt in ACCEPTED_FORMATS) {
            runCatching { return LocalDate.parse(s, fmt) }
        }
        // 2) 마지막으로 기본 ISO 파싱도 시도
        return runCatching { LocalDate.parse(s) }.getOrNull()
    }
}