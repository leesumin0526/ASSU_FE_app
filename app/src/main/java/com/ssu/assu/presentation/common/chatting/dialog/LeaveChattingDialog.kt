package com.ssu.assu.presentation.common.chatting.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ssu.assu.R
import com.ssu.assu.presentation.common.chatting.ChattingActivity
import com.ssu.assu.ui.chatting.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LeaveChatRoomDialog : DialogFragment() {

    private val viewModel: ChattingViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val roomId = requireArguments().getLong("roomId")

        val view = layoutInflater.inflate(R.layout.fragment_leave_chatting_dialog, null)

        val cancelBtn = view.findViewById<TextView>(R.id.btnCancel)
        val cross = view.findViewById<ImageView>(R.id.ivCross)
        val leaveBtn = view.findViewById<TextView>(R.id.btnLeave)

        cancelBtn.setOnClickListener { dismiss() }
        cross.setOnClickListener { dismiss() }
        leaveBtn.setOnClickListener {
            viewModel.leaveChattingRoom(roomId)
            viewModel.getChattingRoomList()
            (activity as? ChattingActivity)?.navigateToChatting()
            dismiss()
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
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(android.view.WindowInsets.Type.systemBars())
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
        fun newInstance(roomId: Long): LeaveChatRoomDialog {
            return LeaveChatRoomDialog().apply {
                arguments = Bundle().apply {
                    putLong("roomId", roomId)
                }
            }
        }
    }
}