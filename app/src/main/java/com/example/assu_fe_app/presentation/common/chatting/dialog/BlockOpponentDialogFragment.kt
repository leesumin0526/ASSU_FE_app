package com.example.assu_fe_app.presentation.common.chatting.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.chatting.request.BlockRequestDto
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import kotlinx.coroutines.launch

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

    override fun onStart() {
        super.onStart()
        val context = context ?: return
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            val width = windowMetrics.bounds.width() - insets.left - insets.right

            // 화면 가로 너비의 85% 정도로 설정 (원하는 비율로 조절)
            val dialogWidth = (width * 0.85).toInt()
            dialog?.window?.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)

        } else {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)

            // 화면 가로 너비의 85% 정도로 설정 (원하는 비율로 조절)
            val dialogWidth = (size.x * 0.85).toInt()
            dialog?.window?.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        // 이 시점에는 dialog 와 window 가 확실히 존재합니다.
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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