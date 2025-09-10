package com.example.assu_fe_app.presentation.common.mypage

import androidx.fragment.app.DialogFragment
import android.app.Dialog
import android.os.Bundle
import android.view.*
import com.example.assu_fe_app.databinding.DialogSecessionBinding

class SecessionDialogFragment : DialogFragment() {

    private var _binding: DialogSecessionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
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
        _binding = DialogSecessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 바깥 영역 클릭 닫기
        binding.backgroundOverlay.setOnClickListener { dismiss() }

        // 취소
        binding.btnCancel.setOnClickListener { dismiss() }

        // 확인 (회원 탈퇴 api 연결 지점)
        binding.btnConfirm.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): SecessionDialogFragment = SecessionDialogFragment()
    }
}