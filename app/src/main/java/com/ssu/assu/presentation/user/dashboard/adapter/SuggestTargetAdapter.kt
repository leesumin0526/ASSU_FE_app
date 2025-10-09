package com.ssu.assu.presentation.user.dashboard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ssu.assu.databinding.ItemServiceSpinnerBinding

class SuggestTargetAdapter(
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, 0, items) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemServiceSpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
        binding.tvServcieSpinnerItem.text = items[position]
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemServiceSpinnerBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        binding.root.layoutParams.width = parent.width
        binding.tvServcieSpinnerItem.text = items[position]
        return binding.root
    }
}