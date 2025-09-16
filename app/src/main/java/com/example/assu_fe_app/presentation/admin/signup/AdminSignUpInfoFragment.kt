package com.example.assu_fe_app.presentation.admin.signup

import SignUpDropdownAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.DropdownAdminPartBinding
import com.example.assu_fe_app.databinding.FragmentAdminSignUpInfoBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.util.setProgressBarFillAnimated
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminSignUpInfoFragment :
    BaseFragment<FragmentAdminSignUpInfoBinding>(R.layout.fragment_admin_sign_up_info) {

    private lateinit var partDropdownAdapter: SignUpDropdownAdapter
    private lateinit var departmentDropdownAdapter: SignUpDropdownAdapter
    private lateinit var majorDropdownAdapter: SignUpDropdownAdapter

    private var currentPartSelection: String? = null
    private var currentDepartmentSelection: String? = null
    private var currentMajorSelection: String? = null

    private var isAddressSearchClicked = false

    // 예시 데이터: 단과대학별 학과/부 목록
    private val majorData = mapOf(
        "IT대학" to listOf("컴퓨터학부", "소프트웨어학부", "글로벌미디어학부", "AI융합학부","정보보호학과", "전자정보공학부"),
        "인문대학" to listOf("국어국문학과", "영어영문학과", "사학과", "일어일문학과","독어독문학과","철학과"),
        "경영대학" to listOf("경영학부"),
        "공과대학" to listOf("신소재공학과","기계공학과","전자공학과","전기공학과","산업정보시스템공학과"),
        "사회과학대학" to listOf("건축학부","기독교학과","평생교육학과")
    )

    override fun initObserver() {}

    override fun initView() {

        parentFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
            val resultData = bundle.getString("selectedAddress")
            Log.d("SignupInfoFragment", "받은 데이터: $resultData")
            binding.tvAdminAddress.text = resultData
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
        val items = listOf("인문대학", "자연과학대학", "경영대학", "사회과학대학", "공과대학", "IT대학")
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
}