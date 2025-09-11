package com.example.assu_fe_app.presentation.user.home

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.certification.request.PersonalCertificationRequestDto
import com.example.assu_fe_app.data.dto.certification.request.UserSessionRequestDto
import com.example.assu_fe_app.databinding.FragmentUserPartnershipSelectBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.data.dto.store.PaperContent
import com.example.assu_fe_app.data.dto.usage.SaveUsageRequestDto
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserPartnershipSelectFragment :
    BaseFragment<FragmentUserPartnershipSelectBinding>(R.layout.fragment_user_partnership_select) {

    private lateinit var partnershipButtons: List<View>
    private val viewModel: UserVerifyViewModel by activityViewModels()
    private var selectedIndex: Int? = null
    private var contentList: List<PaperContent> = emptyList()

    override fun initView() {
        // 버튼 리스트 초기화
        partnershipButtons = listOf(
            binding.btnPartnership1,
            binding.btnPartnership2,
            binding.btnPartnership3,
            binding.btnPartnership4
        )

        // 선택 완료 버튼 초기 상태
        updateCompleteButtonState(false)

        // 선택완료 버튼 클릭 리스너
        binding.btnSelectPartnershipComplete.setOnClickListener {
            selectedIndex?.let { index ->
                if (index < contentList.size) {
                    // 선택된 제휴사 정보를 ViewModel에 저장
                    viewModel.selectPartnership(contentList[index])
                    navigateToComplete()

                    if(!viewModel.isPeopleType) {
                        viewModel.postPersonalCertification(
                            PersonalCertificationRequestDto(
                                viewModel.selectedAdminId,
                                viewModel.storeId,
                                viewModel.tableNumber.toInt()
                            )
                        )

                        viewModel.postPersonalUsageData(
                            SaveUsageRequestDto(
                                viewModel.selectedAdminName,
                                viewModel.selectedContentId,
                                0,
                                viewModel.selectedPaperContent,
                                viewModel.storeName.toString(),
                                emptyList()

                            )
                        )
                    }
                }
            }
        }

        binding.tvGroupMarketName.text = viewModel.storeName.value


    }

    override fun initObserver() {
        // 제휴사 리스트 옵저빙
        viewModel.contentList.observe(this) { contents ->
            contentList = contents
            bindContentToButtons(contents)
        }
    }

    private fun bindContentToButtons(contents: List<PaperContent>) {
        // 최대 4개까지만 처리
        val maxButtons = minOf(contents.size, partnershipButtons.size)

        partnershipButtons.forEachIndexed { index, button ->
            if (index < maxButtons) {
                button.visibility = View.VISIBLE

                // 제휴사 데이터를 버튼에 바인딩
                val content = contents[index]
                bindSingleContent(button, content, index)

                // 클릭 리스너 설정
                button.setOnClickListener {
                    updateSelection(index)
                }
            } else {
                // 데이터가 없는 버튼은 숨김
                button.visibility = View.GONE
            }
        }
    }

    private fun bindSingleContent(button: View, content: PaperContent, index: Int) {
        // 각 버튼의 TextView들을 찾아서 데이터 바인딩
        val titleTextView = button.findViewById<TextView>(
            resources.getIdentifier("tv_partnership_${index + 1}_title", "id", requireContext().packageName)
        )
        val descTextView = button.findViewById<TextView>(
            resources.getIdentifier("tv_partnership_${index + 1}_desc", "id", requireContext().packageName)
        )

        // PaperContent의 데이터를 UI에 반영
        titleTextView?.text = content.adminName // 또는 다른 적절한 필드
        descTextView?.text = content.paperContent
    }

    private fun updateSelection(selected: Int) {
        selectedIndex = selected

        partnershipButtons.forEachIndexed { index, layout ->
            val isSelected = (index == selected)

            // 선택 상태에 따른 UI 업데이트
            updateButtonAppearance(layout, index, isSelected)
        }

        // 선택 완료 버튼 활성화
        updateCompleteButtonState(true)
    }

    private fun updateButtonAppearance(layout: View, index: Int, isSelected: Boolean) {
        // 배경 변경
        layout.setBackgroundResource(
            if (isSelected) R.drawable.bg_partnership_selected
            else R.drawable.bg_partnership_unselected
        )

        // 투명도 변경
        layout.alpha = if (isSelected) 1.0f else 0.5f

        // 텍스트 색상 변경
        val titleText = layout.findViewById<TextView>(
            resources.getIdentifier("tv_partnership_${index + 1}_title", "id", requireContext().packageName)
        )
        val descText = layout.findViewById<TextView>(
            resources.getIdentifier("tv_partnership_${index + 1}_desc", "id", requireContext().packageName)
        )

        val color = if (isSelected) R.color.assu_main else R.color.assu_font_main
        titleText?.setTextColor(ContextCompat.getColor(requireContext(), color))
        descText?.setTextColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun updateCompleteButtonState(isEnabled: Boolean) {
        binding.btnSelectPartnershipComplete.apply {
            this.isEnabled = isEnabled
            setBackgroundResource(
                if (isEnabled) R.drawable.btn_basic_selected
                else R.drawable.btn_basic_unselected
            )
        }
    }

    private fun navigateToComplete() {
        if(viewModel.isPeopleType){
            val request = UserSessionRequestDto(
                viewModel.selectedAdminId,
                viewModel.selectedPeople,
                viewModel.storeId,
                viewModel.tableNumber.toInt()
            )

            // 세션 요청 미리 호출
            viewModel.requestSessionId(request)


            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, UserGroupVerifyFragment())
                .addToBackStack(null)
                .commit()
            return
        } else{
            if(viewModel.isGoodsList){
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, UserSelectServiceFragment())
                    .addToBackStack(null)
                    .commit()
                return
            } else{
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, UserPriceConfirmFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

    }
}