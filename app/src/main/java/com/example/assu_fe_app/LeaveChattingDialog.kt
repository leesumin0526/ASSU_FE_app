package com.example.assu_fe_app

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment


class LeaveChatRoomDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_leave_chatting_dialog, null)

        val cancelBtn = view.findViewById<Button>(R.id.btnCancel)
        val leaveBtn = view.findViewById<Button>(R.id.btnLeave)

        cancelBtn.setOnClickListener { dismiss() }
        leaveBtn.setOnClickListener {

            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}