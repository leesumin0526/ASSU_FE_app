package com.example.assu_fe_app.presentation.user.home

import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserTableNumberSelectBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class UserTableNumberSelectFragment :
    BaseFragment<FragmentUserTableNumberSelectBinding>(R.layout.fragment_user_table_number_select) {

    override fun initObserver() {}

    override fun initView() {
        initializeUI()
        setupTableInput()
        setupCompleteButton()
    }

    private fun initializeUI() {
        // 초기 상태: 빈 값
        binding.tvDigitLeft.text = ""
        binding.tvDigitRight.text = ""
        binding.btnTableNumberSelectComplete.isEnabled = false
        binding.btnTableNumberSelectComplete.setBackgroundResource(R.drawable.btn_basic_unselected)
    }

    private fun setupTableInput() {
        val input = binding.hiddenInputEditText.apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(2))
            imeOptions = EditorInfo.IME_ACTION_DONE
            isCursorVisible = false
            isFocusableInTouchMode = true
            isFocusable = true
        }

        binding.llTableNumber.setOnClickListener {
            input.setText("") // 초기화
            binding.tvDigitLeft.text = ""
            binding.tvDigitRight.text = ""
            input.requestFocus()

            // 키보드 띄우기 (post로 감싸야 안정적임)
            input.post {
                val imm = requireContext().getSystemService(InputMethodManager::class.java)
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val raw = s.toString()
                val first = raw.getOrNull(0)?.toString() ?: ""
                val second = raw.getOrNull(1)?.toString() ?: ""

                binding.tvDigitLeft.text = first
                binding.tvDigitRight.text = second

                if (raw.length == 2) {
                    binding.btnTableNumberSelectComplete.isEnabled = true
                    binding.btnTableNumberSelectComplete.setBackgroundResource(R.drawable.btn_basic_selected)

                    val imm = requireContext().getSystemService(InputMethodManager::class.java)
                    imm.hideSoftInputFromWindow(input.windowToken, 0)
                } else {
                    binding.btnTableNumberSelectComplete.isEnabled = false
                    binding.btnTableNumberSelectComplete.setBackgroundResource(R.drawable.btn_basic_unselected)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }


    private fun setupCompleteButton() {
        binding.btnTableNumberSelectComplete.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, UserPartnershipSelectFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
