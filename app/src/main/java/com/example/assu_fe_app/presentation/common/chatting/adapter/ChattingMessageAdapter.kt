package com.example.assu_fe_app.presentation.common.chatting.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
            ): Boolean = old == new

            // 읽음 상태만 바뀐 경우에 부분 갱신 신호 주기
            override fun getChangePayload(
                oldItem: ChattingMessageItem,
                newItem: ChattingMessageItem
            ): Any? {
                return when {
                    oldItem is ChattingMessageItem.MyMessage && newItem is ChattingMessageItem.MyMessage ->
                        if (oldItem.isRead != newItem.isRead) Payload.ReadChanged(newItem.isRead) else null

                    oldItem is ChattingMessageItem.OtherMessage && newItem is ChattingMessageItem.OtherMessage ->
                        if (oldItem.isRead != newItem.isRead) Payload.ReadChanged(newItem.isRead) else null

                    else -> null
                }
            }
        }

        private sealed interface Payload {
            data class ReadChanged(val isRead: Boolean) : Payload
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
            val p = payloads.firstOrNull { it is Payload.ReadChanged } as? Payload.ReadChanged
            if (p != null) {
                when (holder) {
                    is MyMessageViewHolder -> holder.setUnread(p.isRead)
//                    is OtherMessageViewHolder -> holder.setUnread(p.isRead)
                }
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
            binding.tvMyMessage.text = item.message
            binding.tvMyMessageTime.text = item.sentAt
            setUnread(item.isRead)
        }

        fun setUnread(isRead: Boolean) {
            binding.tvUnreadBadge.apply {
                // invisible로 해야 레이아웃 흔들리지 않음
                visibility = if (isRead) View.INVISIBLE else View.VISIBLE
                text = if (isRead) "" else "1"
            }
        }
    }

    inner class OtherMessageViewHolder(private val binding: ItemChatOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChattingMessageItem.OtherMessage) {
            // 레이아웃 id는 네 xml에 맞게 사용 (예: tvOtherMessage, tvOtherMessageTime 등)
            binding.tvOtherMessage.text = item.message
            binding.tvOtherMessageTime.text = item.sentAt
            // 프로필 이미지 로딩 필요하면 여기에 Glide 등 사용
        }

//        fun setUnread(isRead: Boolean) {
//            binding.tvUnreadBadge.apply {
//                visibility = if (isRead) View.INVISIBLE else View.VISIBLE
//                text = if (isRead) "" else "1"
//            }
//        }
    }
}