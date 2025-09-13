package com.example.assu_fe_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.assu_fe_app.databinding.FragmentProposalModifyBinding

class ProposalModifyFragment: Fragment(){

    private var _binding: FragmentProposalModifyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProposalModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llChattingCall.setOnClickListener {
//            dismiss() // 다이얼로그 닫기
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.main, QrSaveFragment())
                addToBackStack(null)
            }
        }
    }
}