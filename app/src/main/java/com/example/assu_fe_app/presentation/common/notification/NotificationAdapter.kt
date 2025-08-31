package com.example.assu_fe_app.presentation.common.notification

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val items: List<NotificationItem>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(val binding: ItemNotificationBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvItemNotiType.text = item.type
            tvItemNotiContent.text = item.message
            tvItemNotiTime.text = item.time

            // 읽음/안읽음 배경 처리
            root.setBackgroundColor(
                if (item.isRead) Color.WHITE else Color.parseColor("#F0F8FF")
            )
        }
    }

    override fun getItemCount(): Int = items.size
}
data class NotificationItem(
    val type: String, // 제휴 제안인지(admin), 주문 안내인지(partner)
    val message: String,
    val time: String,
    val isRead: Boolean
)