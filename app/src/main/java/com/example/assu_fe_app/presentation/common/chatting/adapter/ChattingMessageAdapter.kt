package com.example.assu_fe_app.presentation.common.chatting.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.chatting.ChattingMessageItem
import com.example.assu_fe_app.databinding.ItemChatMineBinding
import com.example.assu_fe_app.databinding.ItemChatOtherBinding

class ChattingMessageAdapter
    : ListAdapter<ChattingMessageItem, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private const val TYPE_ME = 0
        private const val TYPE_OTHER = 1

        val DIFF = object : DiffUtil.ItemCallback<ChattingMessageItem>() {
            override fun areItemsTheSame(
                old: ChattingMessageItem,
                new: ChattingMessageItem
            ): Boolean {
                return when {
                    old is ChattingMessageItem.MyMessage && new is ChattingMessageItem.MyMessage ->
                        old.messageId == new.messageId

                    old is ChattingMessageItem.OtherMessage && new is ChattingMessageItem.OtherMessage ->
                        old.messageId == new.messageId

                    else -> false
                }
            }

            override fun areContentsTheSame(
                old: ChattingMessageItem,
                new: ChattingMessageItem
            ): Boolean {
                return when {
                    old is ChattingMessageItem.MyMessage && new is ChattingMessageItem.MyMessage -> {
                        old.messageId == new.messageId &&
                                old.message == new.message &&
                                old.sentAt == new.sentAt &&
                                // ↓↓↓ 이 조건을 추가하세요! ↓↓↓
                                old.unreadCountForSender == new.unreadCountForSender
                    }
                    old is ChattingMessageItem.OtherMessage && new is ChattingMessageItem.OtherMessage -> {
                        old.messageId == new.messageId &&
                                old.message == new.message &&
                                old.sentAt == new.sentAt
                    }
                    else -> false
                }
            }

            // 읽음/안읽음 뱃지 값이 바뀐 경우만 부분 갱신
            override fun getChangePayload(
                oldItem: ChattingMessageItem,
                newItem: ChattingMessageItem
            ): Any? {
                return when {
                    oldItem is ChattingMessageItem.MyMessage && newItem is ChattingMessageItem.MyMessage ->
                        if (oldItem.unreadCountForSender != newItem.unreadCountForSender) {
                            Payload.UnreadCountChanged(newItem.unreadCountForSender)
                        } else null
                    else -> null
                }
            }
        }

        // Payload는 명확히 이름을 맞춰줌
        private sealed interface Payload {
            data class UnreadCountChanged(val unreadCount: Int) : Payload
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ChattingMessageItem.MyMessage -> TYPE_ME
        is ChattingMessageItem.OtherMessage -> TYPE_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ME -> MyMessageViewHolder(ItemChatMineBinding.inflate(inf, parent, false))
            else -> OtherMessageViewHolder(ItemChatOtherBinding.inflate(inf, parent, false))
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val p = payloads.firstOrNull { it is Payload.UnreadCountChanged } as? Payload.UnreadCountChanged
            if (p != null && holder is MyMessageViewHolder) {
                holder.setUnread(p.unreadCount)
                return
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyMessageViewHolder -> holder.bind(getItem(position) as ChattingMessageItem.MyMessage)
            is OtherMessageViewHolder -> holder.bind(getItem(position) as ChattingMessageItem.OtherMessage)
        }
    }

    inner class MyMessageViewHolder(private val binding: ItemChatMineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChattingMessageItem.MyMessage) {
            Log.d("ADAPTER", "bind MyMessage id=${item.messageId}, unread=${item.unreadCountForSender}")
            binding.tvMyMessage.text = item.message
            binding.tvMyMessageTime.text = item.sentAt
            setUnread(item.unreadCountForSender)
        }

        fun setUnread(unreadCount: Int) {
            Log.d("ADAPTER", "setUnread called with $unreadCount")
            binding.tvUnreadBadge.apply {
                visibility = if (unreadCount > 0) View.VISIBLE else View.INVISIBLE
                text = if (unreadCount > 0) unreadCount.toString() else ""
            }
        }
    }

    inner class OtherMessageViewHolder(private val binding: ItemChatOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChattingMessageItem.OtherMessage) {
            binding.tvOtherMessage.text = item.message
            binding.tvOtherMessageTime.text = item.sentAt
            binding.ivRestaurantProfileImage.load(item.profileImageUrl) {
                placeholder(R.drawable.img_partner)
                error(R.drawable.img_partner)
                transformations(CircleCropTransformation())
            }
        }
    }
}