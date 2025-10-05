package com.assu.app.presentation.admin.signup

import SignUpDropdownAdapter
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.assu.app.R
import com.assu.app.data.dto.auth.SelectedPlaceDto
import com.assu.app.databinding.DropdownAdminPartBinding
import com.assu.app.databinding.FragmentAdminSignUpInfoBinding
import com.assu.app.domain.model.enums.Department
import com.assu.app.domain.model.enums.Major
import com.assu.app.presentation.base.BaseFragment
import com.assu.app.ui.auth.SignUpViewModel
import com.assu.app.util.setProgressBarFillAnimated
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminSignUpInfoFragment :
    BaseFragment<FragmentAdminSignUpInfoBinding>(R.layout.fragment_admin_sign_up_info) {

    private val signUpViewModel: SignUpViewModel by activityViewModels()
    
    private lateinit var partDropdownAdapter: SignUpDropdownAdapter
    private lateinit var departmentDropdownAdapter: SignUpDropdownAdapter
    private lateinit var majorDropdownAdapter: SignUpDropdownAdapter

    private var currentPartSelection: String? = null
    private var currentDepartmentSelection: String? = null
    private var currentMajorSelection: String? = null

    private var isAddressSearchClicked = false


    // Department enum을 사용한 학과/부 목록
    private val majorData = Department.values().associate { department ->
        department.displayName to Major.values()
            .filter { it.department == department }
            .map { it.displayName }
    }

    override fun initObserver() {}

    override fun onResume() {
        super.onResume()
        // Fragment가 다시 보여질 때 드롭다운 상태 복원
        restoreDropdownStates()
    }

    override fun initView() {

    // 주소 검색 결과를 받기 위한 Fragment Result Listener
    parentFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
        val resultData = bundle.getString("selectedAddress")
        Log.d("AdminSignUpInfoFragment", "받은 주소 데이터: $resultData")
        resultData?.let { address ->
            binding.tvAdminAddress.text = address
            // 주소가 설정되면 색상을 assu_font_main으로 변경
            binding.tvAdminAddress.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_main))
            // 상세주소 입력 활성화
            binding.etAdminAddressDetail.isEnabled = true
            isAddressSearchClicked = true
            
            // selectedPlace 객체 생성 및 ViewModel에 저장
            val selectedPlaceName = bundle.getString("selectedPlaceName") ?: ""
            val selectedPlaceId = bundle.getString("selectedPlaceId") ?: ""
            val selectedPlaceRoadAddress = bundle.getString("selectedPlaceRoadAddress") ?: ""
            val selectedPlaceLatitude = bundle.getDouble("selectedPlaceLatitude", 0.0)
            val selectedPlaceLongitude = bundle.getDouble("selectedPlaceLongitude", 0.0)
            
            Log.d("AdminSignUpInfoFragment", "=== selectedPlace 데이터 ===")
            Log.d("AdminSignUpInfoFragment", "Name: '$selectedPlaceName'")
            Log.d("AdminSignUpInfoFragment", "ID: '$selectedPlaceId'")
            Log.d("AdminSignUpInfoFragment", "Address: '$address'")
            Log.d("AdminSignUpInfoFragment", "Road Address: '$selectedPlaceRoadAddress'")
            Log.d("AdminSignUpInfoFragment", "Latitude: $selectedPlaceLatitude")
            Log.d("AdminSignUpInfoFragment", "Longitude: $selectedPlaceLongitude")
            Log.d("AdminSignUpInfoFragment", "========================")
            
            // SignUpViewModel에 selectedPlace 설정
            val selectedPlaceDto = SelectedPlaceDto(
                placeId = selectedPlaceId,
                name = selectedPlaceName,
                address = address,
                roadAddress = selectedPlaceRoadAddress,
                latitude = selectedPlaceLatitude,
                longitude = selectedPlaceLongitude
            )
            signUpViewModel.setSelectedPlace(selectedPlaceDto)
            
            checkAllInputs()
        }
    }

        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.55f,
            toPercent = 0.7f
        )

        // 1. 초기화 함수 중복 호출 수정 (한 번만 호출)
        initPartDropdown()
        initDepartmentDropdown()
        initMajorDropdown()
        setButtonEnabled(false)

        // 초기에는 단위(part) 드롭다운 외에는 모두 숨김
        binding.tvAdminDepartmentLabel.visibility = View.GONE
        binding.dvDepartment.root.visibility = View.GONE
        binding.tvAdminMajorLabel.visibility = View.GONE
        binding.dvMajor.root.visibility = View.GONE

        // 상세주소 입력 비활성화
        binding.etAdminAddressDetail.isEnabled = false

        binding.btnAdminSearch.setOnClickListener {
            binding.etAdminAddressDetail.isEnabled = true
            isAddressSearchClicked = true
            findNavController().navigate(R.id.action_admin_info_to_location)
            checkAllInputs()
        }

        binding.tvAdminAddress.setOnClickListener {
            binding.etAdminAddressDetail.isEnabled = true
            isAddressSearchClicked = true
            findNavController().navigate(R.id.action_admin_info_to_location)
            checkAllInputs()
        }

        binding.etAdminAddressDetail.addTextChangedListener { checkAllInputs() }

        binding.btnCompleted.setOnClickListener {
            if (binding.btnCompleted.isEnabled) {
                // ViewModel에 관리자 정보 저장
                saveAdminInfoToViewModel()
                findNavController().navigate(R.id.action_admin_info_to_seal)
            }
        }
    }

    private fun initPartDropdown() {
        val items = listOf("총학생회", "단과대학 학생회", "학과/부 학생회")
        partDropdownAdapter = SignUpDropdownAdapter(items)
        setupDropdown(binding.dvPart, partDropdownAdapter) { selectedText ->
            if (currentPartSelection != selectedText) {
                resetDepartmentSelection()
                resetMajorSelection()
            }
            currentPartSelection = selectedText
            updateUiBasedOnPartSelection(selectedText) // visibility 및 제약조건 변경
            checkAllInputs()
        }
    }

    private fun initDepartmentDropdown() {
        val items = Department.values().map { it.displayName }
        departmentDropdownAdapter = SignUpDropdownAdapter(items)
        setupDropdown(binding.dvDepartment, departmentDropdownAdapter) { selectedText ->
            if (currentDepartmentSelection != selectedText) {
                resetMajorSelection()
            }
            currentDepartmentSelection = selectedText
            val majors = majorData[selectedText] ?: emptyList()
            majorDropdownAdapter.updateData(majors)
            checkAllInputs()
        }
    }

    private fun initMajorDropdown() {
        majorDropdownAdapter = SignUpDropdownAdapter(emptyList())
        setupDropdown(binding.dvMajor, majorDropdownAdapter) { selectedText ->
            currentMajorSelection = selectedText
            checkAllInputs()
        }
    }

    private fun setupDropdown(
        dropdownBinding: DropdownAdminPartBinding,
        adapter: SignUpDropdownAdapter,
        onItemSelect: (String) -> Unit
    ) {
        dropdownBinding.rvSignupNameList.adapter = adapter
        dropdownBinding.rvSignupNameList.layoutManager = LinearLayoutManager(requireContext())
        dropdownBinding.dropdownHeader.setOnClickListener {
            val container = dropdownBinding.dropdownContainer
            if (!container.isVisible) {
                closeAllDropdowns()
            }
            container.visibility = if (container.isVisible) View.GONE else View.VISIBLE
        }
        adapter.onItemClick = { selectedText ->
            adapter.setSelectedItem(selectedText)
            dropdownBinding.tvSelected.text = selectedText
            dropdownBinding.tvSelected.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_main))
            dropdownBinding.dropdownContainer.visibility = View.GONE
            onItemSelect(selectedText)
        }
    }

    private fun updateUiBasedOnPartSelection(selection: String) {
        val departmentVisible = (selection == "단과대학 학생회" || selection == "학과/부 학생회")
        val majorVisible = (selection == "학과/부 학생회")

        binding.tvAdminDepartmentLabel.visibility = if (departmentVisible) View.VISIBLE else View.GONE
        binding.dvDepartment.root.visibility = if (departmentVisible) View.VISIBLE else View.GONE

        binding.tvAdminMajorLabel.visibility = if (majorVisible) View.VISIBLE else View.GONE
        binding.dvMajor.root.visibility = if (majorVisible) View.VISIBLE else View.GONE

        // 2. 누락되었던 ConstraintSet 로직 추가
        when (selection) {
            "총학생회" -> updateAddressLabelConstraints(R.id.dv_part)
            "단과대학 학생회" -> updateAddressLabelConstraints(R.id.dv_department)
            "학과/부 학생회" -> updateAddressLabelConstraints(R.id.dv_major)
        }
    }

    // 3. ConstraintSet을 사용해 제약조건을 동적으로 변경하는 함수 추가
    private fun updateAddressLabelConstraints(lastVisibleViewId: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.rootLayout)
        // marginTop 30dp 적용
        constraintSet.connect(R.id.tv_admin_address_label, ConstraintSet.TOP, lastVisibleViewId, ConstraintSet.BOTTOM, 30.dpToPx())
        constraintSet.applyTo(binding.rootLayout)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun resetDepartmentSelection() {
        currentDepartmentSelection = null
        departmentDropdownAdapter.setSelectedItem(null)
        binding.dvDepartment.tvSelected.text = "대상을 선택해주세요"
        binding.dvDepartment.tvSelected.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
    }

    private fun resetMajorSelection() {
        currentMajorSelection = null
        majorDropdownAdapter.setSelectedItem(null)
        majorDropdownAdapter.updateData(emptyList())
        binding.dvMajor.tvSelected.text = "대상을 선택해주세요"
        binding.dvMajor.tvSelected.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
    }

    private fun closeAllDropdowns() {
        binding.dvPart.dropdownContainer.visibility = View.GONE
        binding.dvDepartment.dropdownContainer.visibility = View.GONE
        binding.dvMajor.dropdownContainer.visibility = View.GONE
    }

    private fun checkAllInputs() {
        val isAddressFilled = binding.tvAdminAddress.text.isNotBlank()
        val isDetailFilled = binding.etAdminAddressDetail.text.isNotBlank()
        val isDropdownValid = when (currentPartSelection) {
            "총학생회" -> true
            "단과대학 학생회" -> currentDepartmentSelection != null
            "학과/부 학생회" -> currentDepartmentSelection != null && currentMajorSelection != null
            else -> false
        }
        val allValid = isDropdownValid && isAddressFilled && isDetailFilled && isAddressSearchClicked
        setButtonEnabled(allValid)
    }

    private fun setButtonEnabled(enabled: Boolean) {
        binding.btnCompleted.isEnabled = enabled
        binding.btnCompleted.background = ContextCompat.getDrawable(
            requireContext(),
            if (enabled) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
        )
    }
    
    private fun saveAdminInfoToViewModel() {
        // 대학교는 기본적으로 SSU로 설정
        signUpViewModel.setUniversity("SSU")
        
        // 부서 정보 설정 (단과대학) - enum 값 사용
        currentDepartmentSelection?.let { departmentDisplayName ->
            val departmentEnum = Department.values().find { it.displayName == departmentDisplayName }
            departmentEnum?.let { 
                signUpViewModel.setDepartment(it.name) // enum name 사용
            }
        }
        
        // 전공 정보 설정 - enum 값 사용
        currentMajorSelection?.let { majorDisplayName ->
            val majorEnum = Major.values().find { it.displayName == majorDisplayName }
            majorEnum?.let {
                signUpViewModel.setMajor(it.name) // enum name 사용
            }
        }
        
        // 상세 주소 설정
        val detailAddress = binding.etAdminAddressDetail.text.toString().trim()
        signUpViewModel.setDetailAddress(detailAddress)
        
        // 선택된 장소 정보는 주소 검색에서 설정됨
        // TODO: 주소 검색 결과를 SelectedPlaceDto로 변환하여 설정
    }

    // 드롭다운 상태 복원 함수
    private fun restoreDropdownStates() {
        // 단위(part) 선택 상태 복원
        currentPartSelection?.let { partSelection ->
            partDropdownAdapter.setSelectedItem(partSelection)
            binding.dvPart.tvSelected.text = partSelection
            binding.dvPart.tvSelected.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_main))
            updateUiBasedOnPartSelection(partSelection)
        }

        // 단과대학 선택 상태 복원
        currentDepartmentSelection?.let { departmentSelection ->
            departmentDropdownAdapter.setSelectedItem(departmentSelection)
            binding.dvDepartment.tvSelected.text = departmentSelection
            binding.dvDepartment.tvSelected.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_main))
            
            // 전공 목록 업데이트
            val majors = majorData[departmentSelection] ?: emptyList()
            majorDropdownAdapter.updateData(majors)
        }

        // 전공 선택 상태 복원
        currentMajorSelection?.let { majorSelection ->
            majorDropdownAdapter.setSelectedItem(majorSelection)
            binding.dvMajor.tvSelected.text = majorSelection
            binding.dvMajor.tvSelected.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_main))
        }
    }
}