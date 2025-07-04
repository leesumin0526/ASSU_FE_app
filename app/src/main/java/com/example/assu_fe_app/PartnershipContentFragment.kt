package com.example.assu_fe_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

class PartnershipContentFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_partnership_content, container, false)
    }
    override fun onStart() {
        super.onStart()
        val width  = (resources.displayMetrics.widthPixels  * 0.9).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.8).toInt()  // 화면 높이의 80%
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}