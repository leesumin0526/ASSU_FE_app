package com.example.assu_fe_app.presentation.user.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.databinding.FragmentGroupVerifyBinding

class GroupVerifyFragment : BaseFragment<FragmentGroupVerifyBinding>(R.layout.fragment_group_verify) {
    override fun initObserver() {

    }

    override fun initView() {
        // 일단 임시로
        binding.btnGroupVerifyComplete.isEnabled = true
        var isEnable = binding.btnGroupVerifyComplete.isEnabled

        if(isEnable){
            binding.btnGroupVerifyComplete.background = resources.getDrawable(R.drawable.btn_basic_selected, null)
        }else{
            binding.btnGroupVerifyComplete.background = resources.getDrawable(R.drawable.btn_basic_unselected, null)
        }


        binding.btnGroupVerifyComplete.setOnClickListener {
            val fragment = SelectServiceFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.user_verify_fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.btnGroupBack.setOnClickListener {
            requireActivity().finish()
        }


    }

}