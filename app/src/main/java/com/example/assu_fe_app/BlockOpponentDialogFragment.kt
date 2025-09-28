package com.example.assu_fe_app

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.example.assu_fe_app.data.dto.chatting.request.BlockRequestDto
import com.example.assu_fe_app.presentation.common.chatting.ChattingActivity
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import kotlinx.coroutines.launch
import kotlin.getValue


class BlockOpponentDialogFragment : DialogFragment() {

    private val viewModel: ChattingViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val opponentId = requireArguments().getLong("opponentId")

        val view = layoutInflater.inflate(R.layout.fragment_block_opponent_dialog, null)

        val cancelBtn = view.findViewById<TextView>(R.id.btnCancel)
        val cross = view.findViewById<ImageView>(R.id.ivCross)
        val blockBtn = view.findViewById<TextView>(R.id.btnBlock)

        cancelBtn.setOnClickListener { dismiss() }
        cross.setOnClickListener { dismiss() }
        blockBtn.setOnClickListener {
            viewModel.blockOpponent(BlockRequestDto(opponentId))
//            dismiss()
        }

        // ViewModel의 차단 상태를 관찰
        lifecycleScope.launch {
            viewModel.blockOpponentState.collect { state ->
                when (state) {
                    is ChattingViewModel.BlockOpponentUiState.Success -> {
                        Toast.makeText(requireContext(), "상대방을 차단했습니다.", Toast.LENGTH_SHORT).show()

                        // ▼▼▼ [핵심 코드] Activity로 차단 성공 신호를 보냄 ▼▼▼
                        setFragmentResult("block_complete", bundleOf("isBlocked" to true))
                        // ▲▲▲ [핵심 코드] ▲▲▲

                        dismiss() // 다이얼로그 닫기
                    }
                    is ChattingViewModel.BlockOpponentUiState.Fail -> {
                        Toast.makeText(requireContext(), "차단에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    else -> Unit
                }
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    companion object {
        fun newInstance(opponentId: Long): BlockOpponentDialogFragment {
            return BlockOpponentDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong("opponentId", opponentId)
                }
            }
        }
    }
}