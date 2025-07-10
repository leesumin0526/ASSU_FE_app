package com.example.assu_fe_app.presentation.common.chatting.proposal.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.OfferType
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
            binding.etFragmentServiceProposalContent2.hint = item.placeholder
            binding.etFragmentServiceProposalContent.hint = item.least

            binding.ivFragmentServiceProposalAddGoodsBtn.text =
                if(item.offerType == OfferType.SERVICE) "+  추가"
                else "%  할인"

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

            // 삭제하기 클릭 리스너
            binding.tvProposalDelete.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    items.removeAt(pos)
                    notifyItemRemoved(pos)
                    onItemChanged()
                }
            }

            // 인원수 vs 금액
            val ctx = binding.root.context  // 뷰의 컨텍스트를 가져옴

            binding.layoutProposalConditionCost.setOnClickListener {
                // “금액” 선택됐을 때
                // 1) 금액 텍스트 색 바꾸기
                binding.tvProposalConditionCost.setTextColor(
                    ContextCompat.getColor(ctx, R.color.assu_main)
                )
                // 2) 금액 아이콘(체크) 변경
                binding.ivProposalConditionCost.setImageResource(R.drawable.ic_check_selected)

                // 3) 인원 수는 원래 색(비활성)으로
                binding.tvProposalConditionPeople.setTextColor(
                    ContextCompat.getColor(ctx, R.color.assu_font_sub)
                )
                binding.ivProposalConditionPeople.setImageResource(R.drawable.ic_check_unselected)

                binding.etFragmentServiceProposalContent.hint = "10,000"
                binding.tvProposalConditionUnit.setText("원 이상일 경우,")

                // 저장 로직
                item.condition = ProposalItem.CONDITION_COST
                onItemChanged()
            }

            binding.layoutProposalConditionPeople.setOnClickListener {
                // “인원 수” 선택됐을 때
                // 1) 인원 수 텍스트 색 바꾸기
                binding.tvProposalConditionPeople.setTextColor(
                    ContextCompat.getColor(ctx, R.color.assu_main)
                )
                // 2) 인원 수 아이콘(체크) 변경
                binding.ivProposalConditionPeople.setImageResource(R.drawable.ic_check_selected)

                // 3) 금액은 원래 색(비활성)으로
                binding.tvProposalConditionCost.setTextColor(
                    ContextCompat.getColor(ctx, R.color.assu_font_sub)
                )
                binding.ivProposalConditionCost.setImageResource(R.drawable.ic_check_unselected)

                binding.etFragmentServiceProposalContent.hint = "2"
                binding.tvProposalConditionUnit.setText("명 이상일 경우,")

                // 저장 로직
                item.condition = ProposalItem.CONDITION_PEOPLE
                onItemChanged()
            }

            binding.tvProposalOption1.setOnClickListener {
                selectOption("서비스 제공",item)
                item.offerType = OfferType.SERVICE
                // 내부 초기화
                item.contents.clear()
                item.contents.add("")            // EditText 1개
                item.placeholder = "캔콜라"     // 힌트 복원
                notifyItemChanged(adapterPosition)
                binding.ivFragmentServiceProposalAddGoodsBtn.text = "+  추가"
                onItemChanged
            }

            binding.tvProposalOption2.setOnClickListener {
                selectOption("할인 혜택",item)
                item.offerType = OfferType.DISCOUNT
                // 내부 초기화
                item.contents.clear()
                item.contents.add("")            // EditText 1개
                item.placeholder = "10"        // 새로운 힌트
                notifyItemChanged(adapterPosition)
                binding.ivFragmentServiceProposalAddGoodsBtn.text = "% 할인"
                onItemChanged()
            }

            // 1) FlexboxLayout 초기화
            val container = binding.flexboxServiceProposalItem
            val addBtn = binding.ivFragmentServiceProposalAddGoodsBtn

            // ➊ “할인 혜택” 모드면 비활성화
            if (item.offerType == OfferType.DISCOUNT) {
                addBtn.apply {
                    isEnabled    = false
                    isClickable  = false
                    setOnClickListener(null)
                }
            } else {
                // ➋ “서비스 제공” 모드면 활성화
                addBtn.apply {
                    isEnabled = true
                    isClickable = true
                    alpha = 1f
                    setOnClickListener {
                        item.contents.add("")
                        notifyItemChanged(adapterPosition)
                        onItemChanged()
                    }
                }
            }

            for (i in container.childCount - 1 downTo 0) {
                val child = container.getChildAt(i)
                if (child is EditText) {
                    container.removeViewAt(i)
                }
            }

            if (item.contents.isEmpty()) {
                item.contents.add("")
            }

            // 2) contents 에 담긴 만큼 EditText 추가
            item.contents.forEachIndexed { idx, text ->
                val et = LayoutInflater.from(container.context)
                    .inflate(R.layout.item_proposal_option_et, container, false)
                    .findViewById<EditText>(R.id.et_proposal_item)

                et.setText(text)
                et.hint = if (text.isEmpty()) item.placeholder else ""
                et.addTextChangedListener {
                    item.contents[idx] = it.toString()
                    onItemChanged()
                }
                et.setOnClickListener {
                    item.contents.removeAt(idx)
                    notifyItemChanged(adapterPosition)
                    onItemChanged()
                }

                // 버튼의 현재 인덱스를 구해서, 그 직전에 삽입
                val btnIndex = container.indexOfChild(addBtn)
                container.addView(et, btnIndex)
            }
            addBtn.setOnClickListener {
                item.contents.add("")
                notifyItemChanged(adapterPosition)
                onItemChanged
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