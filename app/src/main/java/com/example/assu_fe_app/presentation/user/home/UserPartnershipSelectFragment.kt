package com.example.assu_fe_app.presentation.user.home

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserPartnershipSelectBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class UserPartnershipSelectFragment :
    BaseFragment<FragmentUserPartnershipSelectBinding>(R.layout.fragment_user_partnership_select) {

    private lateinit var partnershipButtons: List<View>
    private var selectedIndex: Int? = null

    override fun initView() {
        // 버튼 리스트 초기화
        partnershipButtons = listOf(
            binding.btnPartnership1,
            binding.btnPartnership2,
            binding.btnPartnership3
        )

        // 버튼 클릭 이벤트 연결
        partnershipButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                updateSelection(index)
            }
        }

        // 선택 완료 버튼 초기 상태
        binding.btnSelectPartnershipComplete.isEnabled = false
        binding.btnSelectPartnershipComplete.setBackgroundResource(R.drawable.btn_basic_unselected)
    }

    private fun updateSelection(selected: Int) {
        selectedIndex = selected

        partnershipButtons.forEachIndexed { index, layout ->
            val isSelected = (index == selected)

            // 배경 변경
            layout.setBackgroundResource(
                if (isSelected) R.drawable.bg_partnership_selected
                else R.drawable.bg_partnership_unselected
            )

            // 투명도
            layout.alpha = if (isSelected) 1.0f else 0.5f

            // 텍스트 색상 변경
            val titleText = layout.findViewById<View>(
                resources.getIdentifier("tv_partnership_${index + 1}_title", "id", requireContext().packageName)
            ) as? TextView

            val descText = layout.findViewById<View>(
                resources.getIdentifier("tv_partnership_${index + 1}_desc", "id", requireContext().packageName)
            ) as? TextView

            val color = if (isSelected) R.color.assu_main else R.color.assu_font_main
            titleText?.setTextColor(ContextCompat.getColor(requireContext(), color))
            descText?.setTextColor(ContextCompat.getColor(requireContext(), color))
        }

        // 선택 완료 버튼 활성화
        binding.btnSelectPartnershipComplete.apply {
            isEnabled = true
            binding.btnSelectPartnershipComplete.setBackgroundResource(R.drawable.btn_basic_selected)
        }

        // 선택완료 버튼 클릭시 이동
        binding.btnSelectPartnershipComplete.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, UserPartnershipVerifyCompleteFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    override fun initObserver() {
        // 필요한 경우 LiveData 옵저빙
    }
}