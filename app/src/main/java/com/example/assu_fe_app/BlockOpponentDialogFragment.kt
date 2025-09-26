package com.example.assu_fe_app

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.assu_fe_app.data.dto.chatting.request.BlockRequestDto
import com.example.assu_fe_app.presentation.common.chatting.ChattingActivity
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
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
            //TODO 여기 수정하기
            viewModel.blockOpponent(BlockRequestDto(opponentId))
//            viewModel.leaveChattingRoom(roomId)
//            viewModel.getChattingRoomList()
//            (activity as? ChattingActivity)?.navigateToChatting()
            dismiss()
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