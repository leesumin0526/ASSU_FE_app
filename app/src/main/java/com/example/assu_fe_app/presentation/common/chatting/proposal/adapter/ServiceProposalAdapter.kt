package com.example.assu_fe_app.presentation.common.chatting.proposal.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.data.dto.ProposalItem
import com.example.assu_fe_app.databinding.ItemServiceProposalSetBinding

class ServiceProposalAdapter(
    private val onItemChanged: () -> Unit
) : RecyclerView.Adapter<ServiceProposalAdapter.ServiceProposalViewHolder>() {

    private val items = mutableListOf<ProposalItem>()

    inner class ServiceProposalViewHolder(
        private val binding: ItemServiceProposalSetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProposalItem) {
            binding.clDropdownMenu.visibility = View.GONE
            binding.tvFragmentServiceProposalDropDown.text = item.content.ifEmpty { "서비스 제공" }

            binding.etFragmentServiceProposalContent2.setText(item.content)
            binding.etFragmentServiceProposalContent2.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.content = s.toString()
                    onItemChanged()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            binding.layoutFragmentServiceProposalDropDownBg.setOnClickListener {
                binding.clDropdownMenu.bringToFront()

                // 2) 메뉴 내부 헤더에만 현재 선택값(또는 기본값) 세팅
                val current = item.content.ifEmpty { "서비스 제공" }
                binding.tvProposalDropDown.text = current

                binding.clDropdownMenu.visibility = View.VISIBLE
            }

            binding.tvProposalOption1.setOnClickListener {
                selectOption("서비스 제공",item)
            }

            binding.tvProposalOption2.setOnClickListener {
                selectOption("할인 혜택",item)
            }


        }
        private fun selectOption(option: String, item: ProposalItem) {
            binding.tvFragmentServiceProposalDropDown.text = option
            item.content = option
            binding.clDropdownMenu.visibility = View.GONE
            onItemChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceProposalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServiceProposalSetBinding.inflate(inflater, parent, false)
        return ServiceProposalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceProposalViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItem() {
        items.add(ProposalItem())
        notifyItemInserted(items.size - 1)
    }

    fun getItems(): List<ProposalItem> = items

}