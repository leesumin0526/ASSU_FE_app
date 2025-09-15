package com.example.assu_fe_app.presentation.user.home

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.usage.SaveUsageRequestDto
import com.example.assu_fe_app.databinding.FragmentUserSelectServiceBinding
import com.example.assu_fe_app.presentation.base.BaseFragment


class UserSelectServiceFragment : BaseFragment<FragmentUserSelectServiceBinding>(R.layout.fragment_user_select_service) {

    private val viewModel: UserVerifyViewModel by activityViewModels()


    override fun initObserver() {

    }

    override fun initView() {

        val optionViews = listOf(binding.tvServiceGoods1, binding.tvServiceGoods2, binding.tvServiceGoods3)
        val goodsList = viewModel.selectedGoodsList

        optionViews.forEach { it.visibility = View.GONE
        it.isSelected = false}

        optionViews.forEach {
            it.visibility = View.GONE
            it.isSelected = false
        }

        // 데이터 바인딩 및 클릭 리스너를 한 번의 루프로 처리
        goodsList.forEachIndexed { index, goodsItem ->
            if (index < optionViews.size) {
                val textView = optionViews[index]
                textView.text = goodsItem
                textView.visibility = View.VISIBLE

                textView.setOnClickListener {
                    // 전체 선택 해제
                    optionViews.forEach { otherView ->
                        otherView.isSelected = false
                        otherView.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_main))
                    }

                    // 현재 선택
                    it.isSelected = true
                    viewModel.selectService(goodsItem) // ViewModel에 선택된 서비스 저장
                    (it as TextView).setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_main))

                    // 완료 버튼 활성화
                    updateCompleteButtonState(true)
                }
            }
        }
        binding.btnSelectServiceBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    private fun updateCompleteButtonState(isEnabled: Boolean) {
        binding.btnGroupVerifyComplete.apply {
            this.isEnabled = isEnabled
            setBackgroundResource(
                if (isEnabled) R.drawable.btn_basic_selected
                else R.drawable.btn_basic_unselected
            )
            setOnClickListener {
                if (isEnabled) {
                    navigateToNextFragment()
                }
            }
        }
    }

    private fun navigateToNextFragment() {

        if(viewModel.isPriceType){
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, UserPriceConfirmFragment())
                .addToBackStack(null)
                .commit()
        } else{
            viewModel.postPersonalUsageData(
                SaveUsageRequestDto(
                    viewModel.storeId,
                    viewModel.tableNumber,
                    viewModel.selectedAdminName,
                    viewModel.selectedContentId,
                    0,
                    viewModel.selectedPaperContent,
                    viewModel.storeName.value.toString(),
                    emptyList()

                )
            )

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, UserPartnershipVerifyCompleteFragment())
                .addToBackStack(null)
                .commit()
        }
    }


}