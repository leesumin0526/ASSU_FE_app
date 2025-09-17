package com.example.assu_fe_app

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.assu_fe_app.presentation.common.chatting.ChattingActivity
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LeaveChatRoomDialog : DialogFragment() {

    private val viewModel: ChattingViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val roomId = requireArguments().getLong("roomId")

//        val view = LayoutInflater.from(requireContext())
//            .inflate(R.layout.fragment_leave_chatting_dialog, null)

        val view = layoutInflater.inflate(R.layout.fragment_leave_chatting_dialog, null)

        val cancelBtn = view.findViewById<TextView>(R.id.btnCancel)
        val cross = view.findViewById<ImageView>(R.id.ivCross)
        val leaveBtn = view.findViewById<TextView>(R.id.btnLeave)

        cancelBtn.setOnClickListener { dismiss() }
        cross.setOnClickListener { dismiss() }
        leaveBtn.setOnClickListener {
            viewModel.leaveChattingRoom(roomId)
            (activity as? ChattingActivity)?.navigateToChatting()
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
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