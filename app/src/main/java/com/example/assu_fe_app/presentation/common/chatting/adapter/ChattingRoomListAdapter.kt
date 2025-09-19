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
import com.example.assu_fe_app.data.local.AuthTokenLocalStoreImpl
import com.example.assu_fe_app.databinding.ItemChattingListBinding
import com.example.assu_fe_app.domain.model.chatting.GetChattingRoomListModel

class ChattingRoomListAdapter (
    private val onItemClick: (GetChattingRoomListModel) -> Unit,
    private val authTokenLocalStoreImpl: AuthTokenLocalStoreImpl
) : ListAdapter<GetChattingRoomListModel, ChattingRoomListAdapter.ViewHolder>(Diff){

    object Diff: DiffUtil.ItemCallback<GetChattingRoomListModel>() {
        override fun areItemsTheSame(
            oldItem: GetChattingRoomListModel,
            newItem: GetChattingRoomListModel
        ): Boolean {
            return oldItem.roomId == newItem.roomId
        }
        override fun areContentsTheSame(
            oldItem: GetChattingRoomListModel,
            newItem: GetChattingRoomListModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: ItemChattingListBinding)
        :RecyclerView.ViewHolder(binding.root) {
            val opponentRole = if (authTokenLocalStoreImpl.getUserRole() == "ADMIN") {
                "PARTNER"
            } else {
                "ADMIN"
            }
            fun bind(item: GetChattingRoomListModel, isLastItem: Boolean) = with(binding){
                val opponentImage = if (opponentRole == "PARTNER") {
                    R.drawable.img_partner
                } else {
                    // TODO: img_admin으로 바꾸기
                    R.drawable.img_partner
                }
                ivItemChattingListRestaurantProfile.load(item.opponentProfileImage) {
                    crossfade(true)
                    placeholder(opponentImage)
                    error(R.drawable.img_partner)
                    transformations(CircleCropTransformation())
                }
                Log.d("BIND", "id=${item.roomId}, name=${item.opponentName}")
                val opponentName = if (item.opponentId == -1L) {
                    "알 수 없음"
                } else {
                    item.opponentName
                }
                tvChattingCounterpart.text = opponentName
                tvChattingLastChat.text = item.lastMessage

                tvUnreadMessageCount.text = item.unreadMessagesCount.toString()
                tvUnreadMessageCount.visibility = if (item.unreadMessagesCount == 0L) View.GONE else View.VISIBLE
                Log.d("BIND", "room=${item.roomId}, unread=${item.unreadMessagesCount}")


                // 마지막 아이템이면 선 숨기기
                viewLocationSearchResultItemLine.visibility =
                    if (isLastItem) View.GONE else View.VISIBLE

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingRoomListAdapter.ViewHolder {
        val binding = ItemChattingListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isLast = position == itemCount - 1
        holder.bind(getItem(position), isLast)
    }
}