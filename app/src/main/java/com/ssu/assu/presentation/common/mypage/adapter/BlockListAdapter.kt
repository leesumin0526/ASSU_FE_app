package com.ssu.assu.presentation.common.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.databinding.ItemBlockListBinding
import com.ssu.assu.domain.model.chatting.GetBlockListModel

class BlockListAdapter(
    private val onUnblockClick: (GetBlockListModel) -> Unit
) : ListAdapter<GetBlockListModel, BlockListAdapter.BlockViewHolder>(BlockDiffCallback()) {

    // ViewHolder를 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val binding = ItemBlockListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlockViewHolder(binding, onUnblockClick)
    }

    // ViewHolder에 데이터를 바인딩하는 함수
    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // 각 리스트 아이템의 View를 보관하는 클래스
    class BlockViewHolder(
        private val binding: ItemBlockListBinding,
        private val onUnblockClick: (GetBlockListModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: GetBlockListModel) {

            binding.tvBlockOpponentName.text = model.name
            binding.tvBlockDate.text = model.blockDate.split("T")[0]

            // '차단 해제' 버튼 클릭 시, 생성자에서 받은 람다 함수를 호출합니다.
            binding.btnUnblock.setOnClickListener { // 예시: 차단 해제 Button
                onUnblockClick(model)
            }
        }
    }
}

// 리스트의 변경 사항을 계산하기 위한 DiffUtil.ItemCallback
// GetBlockListModel에 유니크한 ID (예: userId)가 있다고 가정합니다.
class BlockDiffCallback : DiffUtil.ItemCallback<GetBlockListModel>() {
    override fun areItemsTheSame(oldItem: GetBlockListModel, newItem: GetBlockListModel): Boolean {
        // 아이템의 고유 ID를 비교하여 같은 아이템인지 확인합니다.
        return oldItem.memberId == newItem.memberId // 실제 모델의 고유 ID 필드로 변경해주세요.
    }

    override fun areContentsTheSame(oldItem: GetBlockListModel, newItem: GetBlockListModel): Boolean {
        // 아이템의 내용이 완전히 같은지 확인합니다. data class는 자동으로 equals를 구현해줍니다.
        return oldItem == newItem
    }
}