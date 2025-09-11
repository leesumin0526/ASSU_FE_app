package com.example.assu_fe_app.presentation.common.notification

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.databinding.ItemNotificationBinding
import com.example.assu_fe_app.domain.model.notification.NotificationModel

class NotificationAdapter(
    private val onClick: (NotificationModel) -> Unit
) : ListAdapter<NotificationModel, NotificationAdapter.VH>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position), onClick)

    class VH(private val b: ItemNotificationBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: NotificationModel, onClick: (NotificationModel) -> Unit) {
            b.tvItemNotiType.text   = item.title ?: when (item.type) {
                "ORDER" -> "주문 알림"
                "CHAT"  -> "채팅 알림"
                else    -> "알림"
            }
            b.tvItemNotiContent.text = item.preview ?: ""
            b.tvItemNotiTime.text    = item.timeAgo ?: ""

            b.root.setBackgroundColor(
                if (item.isRead) Color.WHITE else Color.parseColor("#F0F8FF")
            )
            b.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<NotificationModel>() {
            override fun areItemsTheSame(a: NotificationModel, b: NotificationModel) = a.id == b.id
            override fun areContentsTheSame(a: NotificationModel, b: NotificationModel) = a == b
        }
    }
}