package com.example.assu_fe_app.presentation.common.chatting.proposal.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.ProposalItem
import com.example.assu_fe_app.databinding.ItemServiceProposalSetBinding

class ServiceProposalAdapter(
    private val onItemChanged: () -> Unit
) : RecyclerView.Adapter<ServiceProposalAdapter.ServiceProposalViewHolder>() {

    private val items = mutableListOf<ProposalItem>()

    inner class ServiceProposalViewHolder(
        private val binding: ItemServiceProposalSetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private fun updateConstraints(bindToDropdown: Boolean) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.clFragmentServiceProposalItemSet)
            val topId = if (bindToDropdown) binding.dropdownMenu.id else binding.viewFragmentServiceProposalMg14.id
            constraintSet.connect(binding.llFragmentServiceProposalItem.id, ConstraintSet.TOP, topId, ConstraintSet.BOTTOM)
            constraintSet.applyTo(binding.clFragmentServiceProposalItemSet)
        }

        fun bind(item: ProposalItem) {
            binding.llFragmentServiceProposalItem.visibility = View.GONE
            binding.dropdownMenu.visibility = View.GONE

            binding.etFragmentServiceProposalNum2.setText(item.num)
            binding.etFragmentServiceProposalContent2.setText(item.content)

            binding.etFragmentServiceProposalNum2.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.num = s.toString()
                    onItemChanged()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            binding.etFragmentServiceProposalContent2.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.content = s.toString()
                    onItemChanged()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            val showDropdown = View.OnClickListener {
                binding.ivFragmentServiceProposalDropDownBg.visibility = View.GONE
                binding.ivFragmentServiceProposalDropDownIc.visibility = View.GONE
                binding.tvFragmentServiceProposalDropDown.visibility = View.GONE
                binding.dropdownMenu.visibility = View.VISIBLE
                updateConstraints(true)
            }

            binding.ivFragmentServiceProposalDropDownBg.setOnClickListener(showDropdown)
            binding.ivFragmentServiceProposalDropDownIc.setOnClickListener(showDropdown)
            binding.tvFragmentServiceProposalDropDown.setOnClickListener(showDropdown)

            binding.tvProposalOption1.setOnClickListener {
                applySelection("서비스 제공")
            }

            binding.tvProposalOption2.setOnClickListener {
                applySelection("할인 혜택")
            }
        }

        private fun applySelection(option: String) {
            binding.tvFragmentServiceProposalDropDown.text = option
            binding.tvFragmentServiceProposalWhich.text = if (option == "서비스 제공") "제공" else "할인"
            binding.etFragmentServiceProposalContent2.hint = if (option == "서비스 제공") "캔콜라" else "10%"

            binding.ivFragmentServiceProposalDropDownBg.visibility = View.VISIBLE
            binding.ivFragmentServiceProposalDropDownIc.visibility = View.VISIBLE
            binding.tvFragmentServiceProposalDropDown.visibility = View.VISIBLE
            binding.dropdownMenu.visibility = View.GONE
            binding.llFragmentServiceProposalItem.visibility = View.VISIBLE

            onItemChanged()
            updateConstraints(false)
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