package com.example.assu_fe_app.presentation.admin.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.DialogAdminDeleteContractBinding

class AdminDeleteContractDialogFragment : DialogFragment() {

    private var _binding: DialogAdminDeleteContractBinding? = null
    private val binding get() = _binding!!

    private var contract: PendingContract? = null
    private var onDeleteConfirmed: ((PendingContract) -> Unit)? = null

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
        _binding = DialogAdminDeleteContractBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 계약서 정보 설정
        contract?.let { contract ->
            binding.tvDialogStoreName.text = contract.storeName
        }

        // 배경 클릭 시 다이얼로그 닫기
        binding.backgroundOverlay.setOnClickListener {
            dismiss()
        }

        // 취소 버튼
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        // 확인 버튼
        binding.btnConfirm.setOnClickListener {
            contract?.let { contract ->
                onDeleteConfirmed?.invoke(contract)
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            contract: PendingContract,
            onDeleteConfirmed: ((PendingContract) -> Unit)? = null
        ): AdminDeleteContractDialogFragment {
            return AdminDeleteContractDialogFragment().apply {
                this.contract = contract
                this.onDeleteConfirmed = onDeleteConfirmed
            }
        }
    }
}
