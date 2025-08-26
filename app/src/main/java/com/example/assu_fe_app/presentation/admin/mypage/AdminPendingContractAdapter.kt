package com.example.assu_fe_app.presentation.admin.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.databinding.ItemAdminPendingContractBinding

class AdminPendingContractAdapter(
    private val onDeleteClick: (PendingContract) -> Unit
) : ListAdapter<PendingContract, AdminPendingContractAdapter.ViewHolder>(ContractDiffCallback()) {

    private var onDeleteConfirmed: ((PendingContract) -> Unit)? = null

    fun setOnDeleteConfirmedListener(listener: (PendingContract) -> Unit) {
        onDeleteConfirmed = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminPendingContractBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun removeContract(contract: PendingContract) {
        val currentList = currentList.toMutableList()
        currentList.remove(contract)
        submitList(currentList)
        onDeleteConfirmed?.invoke(contract)
    }

    inner class ViewHolder(
        private val binding: ItemAdminPendingContractBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contract: PendingContract) {
            binding.tvStoreName.text = contract.storeName
            binding.tvProposalDate.text = contract.proposalDate

            binding.btnDelete.setOnClickListener {
                onDeleteClick(contract)
            }
        }
    }

    private class ContractDiffCallback : DiffUtil.ItemCallback<PendingContract>() {
        override fun areItemsTheSame(
            oldItem: PendingContract,
            newItem: PendingContract
        ): Boolean {
            return oldItem.storeName == newItem.storeName && oldItem.proposalDate == newItem.proposalDate
        }

        override fun areContentsTheSame(
            oldItem: PendingContract,
            newItem: PendingContract
        ): Boolean {
            return oldItem == newItem
        }
    }
}
