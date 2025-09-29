package com.example.assu_fe_app.presentation.admin.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import android.content.DialogInterface
import com.example.assu_fe_app.databinding.DialogAdminDeleteContractBinding
import com.example.assu_fe_app.domain.model.partnership.SuspendedPaperModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminDeleteContractDialogFragment : DialogFragment() {
    var onDismissListener: (() -> Unit)? = null

    private var _binding: DialogAdminDeleteContractBinding? = null
    private val binding get() = _binding!!

    private var contract: SuspendedPaperModel? = null
    private var onDeleteConfirmed: ((SuspendedPaperModel) -> Unit)? = null

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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
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
        contract?.let { binding.tvDialogStoreName.text = it.partnerName }

        binding.ivDeleteContractCancle.setOnClickListener { dismiss() }
        binding.backgroundOverlay.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnConfirm.setOnClickListener {
            contract?.let { onDeleteConfirmed?.invoke(it) }
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            contract: SuspendedPaperModel,
            onDeleteConfirmed: ((SuspendedPaperModel) -> Unit)? = null
        ): AdminDeleteContractDialogFragment =
            AdminDeleteContractDialogFragment().apply {
                this.contract = contract
                this.onDeleteConfirmed = onDeleteConfirmed
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
