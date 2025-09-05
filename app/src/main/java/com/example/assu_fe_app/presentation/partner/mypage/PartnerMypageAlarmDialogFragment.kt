package com.example.assu_fe_app.presentation.partner.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerMypageAlarmBinding

class PartnerMypageAlarmDialogFragment : DialogFragment() {

    private var _binding: FragmentPartnerMypageAlarmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPartnerMypageAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 뒤로가기 버튼 클릭
        binding.btnAlarmBack.setOnClickListener {
            dismiss()
        }


        //이전 설정 상태를 불러오는 과정
        var isActivated = true


        // 상태 반전
        binding.clPartnerAlarmToggle.setOnClickListener {
            isActivated = !isActivated
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
