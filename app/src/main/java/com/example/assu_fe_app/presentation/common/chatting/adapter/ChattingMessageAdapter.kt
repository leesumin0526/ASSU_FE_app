package com.example.assu_fe_app.presentation.common.chatting.adapter

import ChattingMessageItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.databinding.ItemChatMineBinding
import com.example.assu_fe_app.databinding.ItemChatOtherBinding

class ChattingMessageAdapter (
    private val items: List<ChattingMessageItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class MyMessageViewHolder(private val binding: ItemChatMineBinding)
        :RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChattingMessageItem.MyMessage) {
            binding.tvMyMessageTime.text = item.sentAt
            binding.tvMyMessage.text = item.message
        }
    }

    inner class OtherMessageViewHolder(private val binding: ItemChatOtherBinding)
        :RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChattingMessageItem.OtherMessage) {
                binding.tvMyMessageTime.text = item.sentAt
                binding.tvMyMessage.text = item.message
            }
        }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ChattingMessageItem.MyMessage -> 0
            is ChattingMessageItem.OtherMessage -> 1
            else -> throw IllegalArgumentException("Unknown message type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                val binding = ItemChatMineBinding.inflate(inflater, parent, false)
                MyMessageViewHolder(binding)
            }
            1 -> {
                val binding = ItemChatOtherBinding.inflate(inflater, parent, false)
                OtherMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is MyMessageViewHolder -> holder.bind(item as ChattingMessageItem.MyMessage)
            is OtherMessageViewHolder -> holder.bind(item as ChattingMessageItem.OtherMessage)
        }
    }

    override fun getItemCount(): Int = items.size

}