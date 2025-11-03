package com.ssu.assu.presentation.user.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.databinding.ItemUserPartnershipListBinding
import com.ssu.assu.domain.model.user.GetUsablePartnershipModel

class UserPartnershipListAdapter :
    ListAdapter<GetUsablePartnershipModel, UserPartnershipListAdapter.UserPartnershipListViewHolder> (
        DiffCallback
    ) {
    class UserPartnershipListViewHolder(private val binding: ItemUserPartnershipListBinding):
            RecyclerView.ViewHolder(binding.root) {
                fun bind(item: GetUsablePartnershipModel) {
                    binding.tvPartnershipPartner.text = item.partnerName
                    binding.tvAdmin.text = item.adminName
                    binding.tvPartnershipContent.text = item.note
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPartnershipListViewHolder {
        val binding =
            ItemUserPartnershipListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserPartnershipListViewHolder(binding)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: UserPartnershipListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // DiffUtil 콜백 (ListAdapter에 필수)
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<GetUsablePartnershipModel>() {
            override fun areItemsTheSame(
                oldItem: GetUsablePartnershipModel,
                newItem: GetUsablePartnershipModel
            ): Boolean {
//                return oldItem.paperId == newItem.paperId
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GetUsablePartnershipModel,
                newItem: GetUsablePartnershipModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}