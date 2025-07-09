package com.example.assu_fe_app.presentation.user.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentSelectServiceBinding
import com.example.assu_fe_app.presentation.base.BaseFragment


class SelectServiceFragment : BaseFragment<FragmentSelectServiceBinding>(R.layout.fragment_select_service) {
    override fun initObserver() {

    }

    override fun initView() {
        val optionViews = listOf(binding.tvServiceGoods1, binding.tvServiceGoods2, binding.tvServiceGoods3)

        binding.tvServiceGoods1.isSelected = false
        binding.tvServiceGoods2.isSelected = false
        binding.tvServiceGoods3.isSelected = false
        binding.btnGroupVerifyComplete.isEnabled = false


        optionViews.forEach { view ->
            view.setOnClickListener {
                // 전체 선택 해제
                optionViews.forEach {
                    it.isSelected = false
                    it.setTextColor(resources.getColor(R.color.assu_font_main, null))
                }

                // 현재 선택
                view.isSelected = true
                view.setTextColor(resources.getColor(R.color.assu_main, null))

                // 버튼 활성화
                binding.btnGroupVerifyComplete.isEnabled= true
                binding.btnGroupVerifyComplete.background = resources.getDrawable(R.drawable.btn_basic_selected, null)
                binding.btnGroupVerifyComplete.setOnClickListener {
                    val fragment = PriceConfirmFragment()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.user_verify_fragment_container, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
        }
        binding.btnSelectServiceBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }


}