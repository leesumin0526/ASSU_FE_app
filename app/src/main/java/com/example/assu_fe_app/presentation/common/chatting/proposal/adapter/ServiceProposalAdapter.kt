package com.example.assu_fe_app.presentation.common.chatting.proposal.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.OfferType
import com.example.assu_fe_app.data.dto.ProposalItem
import com.example.assu_fe_app.data.dto.partnership.BenefitItem
import com.example.assu_fe_app.data.dto.partnership.CriterionType
import com.example.assu_fe_app.data.dto.partnership.OptionType
import com.example.assu_fe_app.databinding.ItemProposalOptionEtBinding
import com.example.assu_fe_app.databinding.ItemServiceProposalSetBinding
import com.example.assu_fe_app.ui.partnership.BenefitItemEvent
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel

object BenefitDiffCallback : DiffUtil.ItemCallback<BenefitItem>() {
    override fun areItemsTheSame(oldItem: BenefitItem, newItem: BenefitItem): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: BenefitItem, newItem: BenefitItem): Boolean = oldItem == newItem
}

class ServiceProposalAdapter(
    private val onItemEvent: (Int, BenefitItemEvent) -> Unit
) : ListAdapter<BenefitItem, ServiceProposalAdapter.ViewHolder>(BenefitDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemServiceProposalSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemServiceProposalSetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BenefitItem) {
            binding.clDropdownMenu.visibility = View.GONE

            when (item.optionType) {
                OptionType.SERVICE -> {
                    binding.tvFragmentServiceProposalDropDown.text = "서비스 제공"
                    binding.layoutProposalProvideCategory.visibility = if (item.goods.size >= 2) View.VISIBLE else View.GONE
                    binding.flexboxServiceProposalItem.visibility = View.VISIBLE
                }
                OptionType.DISCOUNT -> {
                    binding.tvFragmentServiceProposalDropDown.text = "할인 혜택"
                    binding.layoutProposalProvideCategory.visibility = View.GONE
                    binding.flexboxServiceProposalItem.visibility = View.GONE
                }
            }

            val ctx = binding.root.context
            when (item.criterionType) {
                CriterionType.PRICE -> {
                    binding.ivProposalConditionCost.setImageResource(R.drawable.ic_check_selected)
                    binding.tvProposalConditionCost.setTextColor(ContextCompat.getColor(ctx, R.color.assu_main))
                    binding.ivProposalConditionPeople.setImageResource(R.drawable.ic_check_unselected)
                    binding.tvProposalConditionPeople.setTextColor(ContextCompat.getColor(ctx, R.color.assu_font_sub))
                    binding.etFragmentServiceProposalContent.hint = "10,000"
                    binding.tvProposalConditionUnit.text = "원 이상일 경우,"
                }
                CriterionType.HEADCOUNT -> {
                    binding.ivProposalConditionCost.setImageResource(R.drawable.ic_check_unselected)
                    binding.tvProposalConditionCost.setTextColor(ContextCompat.getColor(ctx, R.color.assu_font_sub))
                    binding.ivProposalConditionPeople.setImageResource(R.drawable.ic_check_selected)
                    binding.tvProposalConditionPeople.setTextColor(ContextCompat.getColor(ctx, R.color.assu_main))
                    binding.etFragmentServiceProposalContent.hint = "2"
                    binding.tvProposalConditionUnit.text = "명 이상일 경우,"
                }
            }

            if (binding.etFragmentServiceProposalContent.text.toString() != item.criterionValue) {
                binding.etFragmentServiceProposalContent.setText(item.criterionValue)
            }
            if (binding.etProposalProvideCategory.text.toString() != item.category) {
                binding.etProposalProvideCategory.setText(item.category)
            }

            setupGoodsLayout(item)

            // 드롭다운 펼치기
            binding.layoutFragmentServiceProposalDropDownBg.setOnClickListener {
                binding.clDropdownMenu.visibility = View.VISIBLE
            }

            // 삭제하기
            binding.tvProposalDelete.setOnClickListener {
                onItemEvent(bindingAdapterPosition, BenefitItemEvent.ItemRemoved)
            }

            // '서비스 제공' 선택
            binding.tvProposalOption1.setOnClickListener {
                onItemEvent(bindingAdapterPosition, BenefitItemEvent.OptionTypeChanged(OptionType.SERVICE))
                binding.clDropdownMenu.visibility = View.GONE
            }
            // '할인 혜택' 선택
            binding.tvProposalOption2.setOnClickListener {
                onItemEvent(bindingAdapterPosition, BenefitItemEvent.OptionTypeChanged(OptionType.DISCOUNT))
                binding.clDropdownMenu.visibility = View.GONE
            }

            // '금액' 기준 선택
            binding.layoutProposalConditionCost.setOnClickListener {
                onItemEvent(bindingAdapterPosition, BenefitItemEvent.CriterionTypeChanged(CriterionType.PRICE))
            }
            // '인원' 기준 선택
            binding.layoutProposalConditionPeople.setOnClickListener {
                onItemEvent(bindingAdapterPosition, BenefitItemEvent.CriterionTypeChanged(CriterionType.HEADCOUNT))
            }

            // 기준값 입력
            binding.etFragmentServiceProposalContent.doAfterTextChanged { text ->
                onItemEvent(bindingAdapterPosition, BenefitItemEvent.CriterionValueChanged(text.toString()))
            }
            // 카테고리 입력
            binding.etProposalProvideCategory.doAfterTextChanged { text ->
                onItemEvent(bindingAdapterPosition, BenefitItemEvent.CategoryChanged(text.toString()))
            }

//            binding.ivFragmentServiceProposalAddGoodsBtn.text =
//                if(item.optionType == OptionType.SERVICE) "+  추가"
//                else "%  할인"
//
//            binding.etFragmentServiceProposalContent2.setText(item.content)
//            binding.etFragmentServiceProposalContent2.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                    item.content = s.toString()
//                    onItemChanged()
//                }
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            })
//
//            binding.layoutFragmentServiceProposalDropDownBg.setOnClickListener {
//                binding.clDropdownMenu.bringToFront()
//
//                // 2) 메뉴 내부 헤더에만 현재 선택값(또는 기본값) 세팅
//                val current = item.content.ifEmpty { "서비스 제공" }
//                binding.tvProposalDropDown.text = current
//                binding.clDropdownMenu.visibility = View.VISIBLE
//            }
//
            // 삭제하기 클릭 리스너
//            binding.tvProposalDelete.setOnClickListener {
//                val pos = bindingAdapterPosition
//                if (pos != RecyclerView.NO_POSITION) {
//                    onItemEvent(pos, BenefitItemEvent.ItemRemoved)
//                }
//            }
//
//            // 인원수 vs 금액
//            val ctx = binding.root.context  // 뷰의 컨텍스트를 가져옴
//
//            binding.layoutProposalConditionCost.setOnClickListener {
//                // “금액” 선택됐을 때
//                // 1) 금액 텍스트 색 바꾸기
//                binding.tvProposalConditionCost.setTextColor(
//                    ContextCompat.getColor(ctx, R.color.assu_main)
//                )
//                // 2) 금액 아이콘(체크) 변경
//                binding.ivProposalConditionCost.setImageResource(R.drawable.ic_check_selected)
//
//                // 3) 인원 수는 원래 색(비활성)으로
//                binding.tvProposalConditionPeople.setTextColor(
//                    ContextCompat.getColor(ctx, R.color.assu_font_sub)
//                )
//                binding.ivProposalConditionPeople.setImageResource(R.drawable.ic_check_unselected)
//
//                binding.etFragmentServiceProposalContent.hint = "10,000"
//                binding.tvProposalConditionUnit.setText("원 이상일 경우,")
//
//                // 저장 로직
//                item.condition = ProposalItem.CONDITION_COST
//                onItemChanged()
//            }
//
//            binding.layoutProposalConditionPeople.setOnClickListener {
//                // “인원 수” 선택됐을 때
//                // 1) 인원 수 텍스트 색 바꾸기
//                binding.tvProposalConditionPeople.setTextColor(
//                    ContextCompat.getColor(ctx, R.color.assu_main)
//                )
//                // 2) 인원 수 아이콘(체크) 변경
//                binding.ivProposalConditionPeople.setImageResource(R.drawable.ic_check_selected)
//
//                // 3) 금액은 원래 색(비활성)으로
//                binding.tvProposalConditionCost.setTextColor(
//                    ContextCompat.getColor(ctx, R.color.assu_font_sub)
//                )
//                binding.ivProposalConditionCost.setImageResource(R.drawable.ic_check_unselected)
//
//                binding.etFragmentServiceProposalContent.hint = "2"
//                binding.tvProposalConditionUnit.setText("명 이상일 경우,")
//
//                // 저장 로직
//                item.condition = ProposalItem.CONDITION_PEOPLE
//                onItemChanged()
//            }
//
//            binding.tvProposalOption1.setOnClickListener {
//                onItemEvent(bindingAdapterPosition, BenefitItemEvent.OptionTypeChanged(OptionType.SERVICE))
//                item.optionType = OptionType.SERVICE
//                binding.clDropdownMenu.visibility = View.GONE
//            }
//
//            binding.tvProposalOption2.setOnClickListener {
//                selectOption("할인 혜택",item)
//                item.offerType = OfferType.DISCOUNT
//                // 내부 초기화
//                item.contents.clear()
//                item.contents.add("")            // EditText 1개
//                item.placeholder = "10"        // 새로운 힌트
//                notifyItemChanged(adapterPosition)
//                binding.ivFragmentServiceProposalAddGoodsBtn.text = "% 할인"
//                onItemChanged()
//            }
//
//            // 1) FlexboxLayout 초기화
//            val container = binding.flexboxServiceProposalItem
//            val addBtn = binding.ivFragmentServiceProposalAddGoodsBtn
//
//            // ➊ “할인 혜택” 모드면 비활성화
//            if (item.offerType == OfferType.DISCOUNT) {
//                addBtn.apply {
//                    isEnabled    = false
//                    isClickable  = false
//                    setOnClickListener(null)
//                }
//            } else {
//                // ➋ “서비스 제공” 모드면 활성화
//                addBtn.apply {
//                    isEnabled = true
//                    isClickable = true
//                    alpha = 1f
//                    setOnClickListener {
//                        item.contents.add("")
//                        notifyItemChanged(adapterPosition)
//                        onItemChanged()
//                    }
//                }
//            }
//
//            for (i in container.childCount - 1 downTo 0) {
//                val child = container.getChildAt(i)
//                if (child is EditText) {
//                    container.removeViewAt(i)
//                }
//            }
//
//            if (item.contents.isEmpty()) {
//                item.contents.add("")
//            }
//
//            // 2) contents 에 담긴 만큼 EditText 추가
//            item.contents.forEachIndexed { idx, text ->
//                val et = LayoutInflater.from(container.context)
//                    .inflate(R.layout.item_proposal_option_et, container, false)
//                    .findViewById<EditText>(R.id.et_proposal_item)
//
//                et.setText(text)
//                et.hint = if (text.isEmpty()) item.placeholder else ""
//                et.addTextChangedListener {
//                    item.contents[idx] = it.toString()
//                    onItemChanged()
//                }
//                et.setOnClickListener {
//                    item.contents.removeAt(idx)
//                    notifyItemChanged(adapterPosition)
//                    onItemChanged()
//                }
//
//                // 버튼의 현재 인덱스를 구해서, 그 직전에 삽입
//                val btnIndex = container.indexOfChild(addBtn)
//                container.addView(et, btnIndex)
//            }
//            addBtn.setOnClickListener {
//                item.contents.add("")
//                notifyItemChanged(adapterPosition)
//                onItemChanged()
//            }
        }

        private fun setupGoodsLayout(item: BenefitItem) {
            val container = binding.flexboxServiceProposalItem
            // 기존에 동적으로 추가된 EditText들만 제거
            val viewsToRemove = container.children.filter { it.tag == "dynamic_good" }.toList()
            viewsToRemove.forEach { container.removeView(it) }

            val addBtn = binding.ivFragmentServiceProposalAddGoodsBtn

            // '+ 추가' 버튼 리스너
            addBtn.setOnClickListener {
                onItemEvent(bindingAdapterPosition, BenefitItemEvent.GoodAdded)
            }
            // '할인' 모드일 때 추가 버튼 비활성화
            addBtn.isEnabled = item.optionType == OptionType.SERVICE

            // 데이터에 맞게 EditText 동적 생성
            item.goods.forEachIndexed { index, text ->
                val inflater = LayoutInflater.from(container.context)
                // item_proposal_option_et.xml 레이아웃을 사용한다고 가정
                val goodBinding = ItemProposalOptionEtBinding.inflate(inflater, container, false)

                goodBinding.etProposalItem.setText(text)
                goodBinding.etProposalItem.tag = "dynamic_good" // 동적으로 추가된 뷰임을 표시

                goodBinding.etProposalItem.doAfterTextChanged { editable ->
                    onItemEvent(bindingAdapterPosition, BenefitItemEvent.GoodUpdated(index, editable.toString()))
                }

                // 삭제 버튼 리스너 (item_proposal_option_et.xml 안에 삭제 버튼이 있다고 가정)
                // goodBinding.btnDeleteGood.setOnClickListener {
                //     onItemEvent(bindingAdapterPosition, BenefitItemEvent.GoodRemoved(index))
                // }

                val btnIndex = container.indexOfChild(addBtn)
                container.addView(goodBinding.root, btnIndex)
            }
        }
    }
}