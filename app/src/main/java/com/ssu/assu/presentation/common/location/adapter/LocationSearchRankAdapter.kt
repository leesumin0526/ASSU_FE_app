package com.ssu.assu.presentation.common.location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.R
import com.ssu.assu.data.dto.location.LocationSearchItem
import com.ssu.assu.databinding.ItemLocationSearchRankBinding

class LocationSearchRankAdapter(
    private val items: List<LocationSearchItem>
) : RecyclerView.Adapter<LocationSearchRankAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLocationSearchRankBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LocationSearchItem) {
            binding.tvItemLocationSearchRankCount.text = "${item.rank}"
            binding.tvItemLocationSearchRankContent.text = item.name

            val rankColor = if (item.rank <= 3) {
                ContextCompat.getColor(binding.root.context, R.color.assu_main)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.assu_font_sub)
            }

            binding.tvItemLocationSearchRankCount.setTextColor(rankColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLocationSearchRankBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}