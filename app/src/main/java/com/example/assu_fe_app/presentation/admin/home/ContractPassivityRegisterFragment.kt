package com.example.assu_fe_app.presentation.admin.home

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.partnership.request.ContractImageParam
import com.example.assu_fe_app.data.dto.partnership.request.ManualPartnershipRequestDto
import com.example.assu_fe_app.data.dto.partnership.request.OptionDto
import com.example.assu_fe_app.data.dto.partnership.request.SelectedPlaceDto
import com.example.assu_fe_app.databinding.FragmentContractPassiveRegisterBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.partnership.PassiveProposalViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.AndroidEntryPoint
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

    private fun submit() {
        val rawStart = binding.etFragmentContractPassiveRegisterStartDate.text?.toString().orEmpty()
        val rawEnd   = binding.etFragmentContractPassiveRegisterEndDate.text?.toString().orEmpty()

        val start = normalizeDateToIso(rawStart)
        val end   = normalizeDateToIso(rawEnd)

        if (start.isBlank() || end.isBlank()) {
            Log.d("ContractPassivityRegisterFragment","제휴 기간을 입력해 주세요.")
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
            partnershipPeriodStart = start,
            partnershipPeriodEnd = end,
            options = passedOptions
        )

        viewModel.submit(req, pickedImage)
    }

    private fun normalizeDateToIso(input: String): String {
        // 숫자만 추출: "2025 - 2 - 3", "2025.2.3", "2025/02/03", "2025 2 3" 모두 OK
        val nums = Regex("""\d+""").findAll(input).map { it.value }.toList()
        if (nums.size < 3) return "" // 연-월-일 최소 3개 숫자 필요

        var year  = nums[0]
        var month = nums[1]
        var day   = nums[2]

        // 2자리 연도가 들어오면 20xx로 보정 (원치 않으면 이 블록 삭제)
        if (year.length == 2) year = "20$year"

        // 월/일 0 패딩
        month = month.padStart(2, '0')
        day   = day.padStart(2, '0')

        // 간단 범위 체크 (원하면 더 엄격하게 검사 가능)
        val m = month.toIntOrNull() ?: return ""
        val d = day.toIntOrNull() ?: return ""
        if (year.length != 4 || m !in 1..12 || d !in 1..31) return ""

        return "$year-$month-$day"
    }
}