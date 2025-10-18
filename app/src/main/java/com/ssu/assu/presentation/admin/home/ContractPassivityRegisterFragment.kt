package com.ssu.assu.presentation.admin.home

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssu.assu.R
import com.ssu.assu.data.dto.partnership.request.ContractImageParam
import com.ssu.assu.data.dto.partnership.request.ManualPartnershipRequestDto
import com.ssu.assu.data.dto.partnership.request.OptionDto
import com.ssu.assu.data.dto.partnership.request.SelectedPlaceDto
import com.ssu.assu.databinding.FragmentContractPassiveRegisterBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.ui.partnership.PassiveProposalViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.getValue

@AndroidEntryPoint
class ContractPassivityRegisterFragment : BaseFragment<FragmentContractPassiveRegisterBinding>(R.layout.fragment_contract_passive_register) {
    // Moshi (options 복원용)
    private val moshi by lazy { Moshi.Builder().build() }
    private val optionListType by lazy { Types.newParameterizedType(List::class.java, OptionDto::class.java) }
    private val optionListAdapter by lazy { moshi.adapter<List<OptionDto>>(optionListType) }

    // writing → register 로 넘어온 값들 저장
    private var passedStoreName: String = ""
    private var passedAdminName: String? = null
    private var passedSelectedPlace: SelectedPlaceDto =
        SelectedPlaceDto(placeId = null, name = null, address = null, roadAddress = null, latitude = null, longitude = null)
    private var passedOptions: List<OptionDto> = emptyList()

    // 이미지 저장
    private var pickedImage: ContractImageParam? = null
    private val viewModel: PassiveProposalViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    private val DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy - MM - dd")

    override fun initObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.loading.collect { loading ->
                Log.d("ContractPassivityRegisterFragment", "Loading")
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.result.collect { r ->
                r.onSuccess {
                    // 성공 → finish 화면으로 이동
                    findNavController().navigate(
                        R.id.action_contract_passive_register_to_contract_passive_register_finish
                    )
                }.onFailure {
                    Log.d("ContractPassivityRegisterFragment","등록 실패: ${it.message}")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        arguments?.let { args ->
            // 1) 기본 값
            passedStoreName = args.getString("arg_storeName").orEmpty()
            passedAdminName = args.getString("arg_adminName")

            // 2) SelectedPlace 풀셋 복원
            val placeId     = args.getString("arg_storeId")
            val name        = args.getString("arg_storeName")
            val address     = args.getString("arg_selectedPlace_address")
            val roadAddress = args.getString("arg_selectedPlace_roadAddress")

            val latitude  = if (args.containsKey("arg_latitude"))  args.getDouble("arg_latitude")  else Double.NaN
            val longitude = if (args.containsKey("arg_longitude")) args.getDouble("arg_longitude") else Double.NaN

            passedSelectedPlace = SelectedPlaceDto(
                placeId     = placeId,
                name        = name,
                address     = address,
                roadAddress = roadAddress,
                latitude    = latitude.takeIf { !it.isNaN() },
                longitude   = longitude.takeIf { !it.isNaN() }
            )

            // 3) 옵션 복원
            val optionsJson = args.getString("arg_options_json").orEmpty()
            passedOptions = if (optionsJson.isNotEmpty())
                optionListAdapter.fromJson(optionsJson).orEmpty()
            else emptyList()
        }

        // ── 시작일: 오늘부터 선택
        bindDateTriggers(
            triggers = listOf(
                binding.etFragmentContractPassiveRegisterStartDate,   // 입력필드
                binding.ivFragmentContractPassiveRegisterContent,
                binding.ivFragmentContractPassiveRegisterCalendar    // 달력/아이콘(있다면)
            ),
            target = binding.etFragmentContractPassiveRegisterStartDate,
            defaultDateProvider = {
                // 현재 필드에 값이 있으면 그걸 기본값으로
                parseLocalDateFromAny(binding.etFragmentContractPassiveRegisterStartDate.text?.toString())
                    ?: LocalDate.now()
            },
            minDateProvider = { todayMillis() }
        ) { picked ->
            // 시작일 변경 시 종료일 초기화 권장
            binding.etFragmentContractPassiveRegisterEndDate.text?.clear()
            binding.etFragmentContractPassiveRegisterEndDate.hint = "2025 - 05 - 05"
        }

        // ── 종료일: 시작일 이후만 선택
        bindDateTriggers(
            triggers = listOf(
                binding.etFragmentContractPassiveRegisterEndDate,
                binding.ivFragmentContractPassiveRegisterContent3,
                binding.ivFragmentContractPassiveRegisterCalendar2
            ),
            target = binding.etFragmentContractPassiveRegisterEndDate,
            defaultDateProvider = {
                // 종료일 기본값: 현재 종료일 값 or (시작일 있으시면 시작일) or 오늘
                parseLocalDateFromAny(binding.etFragmentContractPassiveRegisterEndDate.text?.toString())
                    ?: parseLocalDateFromAny(binding.etFragmentContractPassiveRegisterStartDate.text?.toString())
                    ?: LocalDate.now()
            },
            minDateProvider = {
                // 시작일을 클릭 시점에 읽어와 minDate로 설정
                parseLocalDateFromAny(binding.etFragmentContractPassiveRegisterStartDate.text?.toString())?.let { d ->
                    Calendar.getInstance().apply {
                        set(d.year, d.monthValue - 1, d.dayOfMonth, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                } ?: todayMillis()
            }
        )

        // 이미지 선택
        binding.layoutFragmentContractPassiveRegister.setOnClickListener { imagePicker.launch("image/*") }

        // 뒤로가기
        binding.ivFragmentContractPassiveRegisterBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.layoutContractPassiveRegisterActivatedButton.setOnClickListener {
            submit()
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<View>(R.id.bottom_navigation_view).visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().findViewById<View>(R.id.bottom_navigation_view).visibility = View.VISIBLE
    }

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@registerForActivityResult
            val param = uriToContractImageParam(uri)
            pickedImage = param

            binding.tvFragmentContractPassiveRegisterUpload.text = param.fileName
        }

    private fun uriToContractImageParam(uri: Uri): ContractImageParam {
        val cr = requireContext().contentResolver
        val mime = cr.getType(uri) ?: "image/*"
        val name = cr.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { c -> if (c.moveToFirst()) c.getString(c.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME)) else null }
            ?: "contract.jpg"
        val bytes = cr.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
        return ContractImageParam(fileName = name, mimeType = mime, bytes = bytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun submit() {
        val rawStart = binding.etFragmentContractPassiveRegisterStartDate.text?.toString().orEmpty()
        val rawEnd   = binding.etFragmentContractPassiveRegisterEndDate.text?.toString().orEmpty()

        val startDate = parseLocalDateFromAny(rawStart).toString()
        val endDate   = parseLocalDateFromAny(rawEnd).toString()

        if (startDate == null || endDate == null) {
            Log.d("ContractPassivityRegisterFragment", "제휴 기간을 입력해 주세요.")
            Toast.makeText(requireContext(), "제휴 기간을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (pickedImage == null) {
            Log.d("ContractPassivityRegisterFragment","계약서 이미지를 선택해 주세요.")
            return
        }
        if (passedOptions.isEmpty()) {
            Log.d("ContractPassivityRegisterFragment","제공 옵션을 한 개 이상 입력해 주세요.")
            return
        }

        val req = ManualPartnershipRequestDto(
            storeName = passedStoreName,
            selectedPlace = passedSelectedPlace,
            storeDetailAddress = passedSelectedPlace.roadAddress,
            partnershipPeriodStart = startDate,
            partnershipPeriodEnd = endDate,
            options = passedOptions
        )

        viewModel.submit(req, pickedImage)
    }

    private fun todayMillis(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    // ── 자유형 문자열(2025-10-16 / 2025.10.16 / 2025 - 10 - 16 등) → LocalDate?
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseLocalDateFromAny(input: String?): LocalDate? {
        if (input.isNullOrBlank()) return null
        // 숫자만 뽑아서 yyyy-MM-dd 구성
        val nums = Regex("""\d+""").findAll(input).map { it.value }.toList()
        if (nums.size < 3) return null
        var (y, m, d) = listOf(nums[0], nums[1], nums[2])
        if (y.length == 2) y = "20$y"
        m = m.padStart(2, '0'); d = d.padStart(2, '0')
        return runCatching { LocalDate.parse("$y-$m-$d") }.getOrNull()
    }

    // ── DatePickerDialog 보여주기
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
            { _, y, m, d -> onPicked(LocalDate.of(y, m + 1, d)) },
            base.year, base.monthValue - 1, base.dayOfMonth
        )
        minDateMillis?.let { dlg.datePicker.minDate = it }
        maxDateMillis?.let { dlg.datePicker.maxDate = it }
        dlg.show()
    }

    // ── 여러 트리거(View)로 하나의 타깃(EditText/TextView)에 날짜 주입
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindDateTriggers(
        triggers: List<View>,
        target: TextView,
        defaultDateProvider: () -> LocalDate? = { null }, // 클릭 시점에 계산
        minDateProvider: () -> Long? = { null },          // 클릭 시점에 계산
        maxDateProvider: () -> Long? = { null },
        onPickedExtra: (LocalDate) -> Unit = {}
    ) {
        if (target is EditText) {
            target.inputType = EditorInfo.TYPE_NULL
            target.isFocusable = false
            target.isCursorVisible = false
        }
        val listener = View.OnClickListener {
            val def = defaultDateProvider()
            val min = minDateProvider()
            val max = maxDateProvider()
            showDatePicker(def, min, max) { picked ->
                target.text = picked.format(DISPLAY_FMT)
                onPickedExtra(picked)
            }
        }
        triggers.forEach { it.setOnClickListener(listener) }
    }
}