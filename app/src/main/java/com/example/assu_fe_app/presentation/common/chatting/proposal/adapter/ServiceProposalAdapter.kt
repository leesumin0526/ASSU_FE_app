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
    override fun areItemsTheSame(oldItem: BenefitItem, newItem: BenefitItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BenefitItem, newItem: BenefitItem): Boolean {
        return oldItem == newItem
    }

    // ✅ 변경사항이 있을 때 payload 제공
    override fun getChangePayload(oldItem: BenefitItem, newItem: BenefitItem): Any? {
        return if (oldItem != newItem) "UPDATE" else null
    }
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

    // ✅ 깜빡임 방지를 위한 payload 지원
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            // payload가 있으면 부분 업데이트만 수행
            holder.bindWithPayload(getItem(position), payloads)
        }
    }

    inner class ViewHolder(
        private val binding: ItemServiceProposalSetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var isBinding = false

        init {
            // 드롭다운 펼치기/접기 (이것은 ViewModel까지 갈 필요 없는 순수 UI 상태)
            binding.layoutFragmentServiceProposalDropDownBg.setOnClickListener {
                binding.clDropdownMenu.bringToFront()
                binding.clDropdownMenu.visibility = if (binding.clDropdownMenu.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }

            // 이벤트 전달 리스너들
            binding.tvProposalDelete.setOnClickListener { handleEvent(BenefitItemEvent.ItemRemoved) }
            binding.tvProposalOption1.setOnClickListener {
                handleEvent(BenefitItemEvent.OptionTypeChanged(OptionType.SERVICE))
                binding.clDropdownMenu.visibility = View.GONE
            }
            binding.tvProposalOption2.setOnClickListener {
                handleEvent(BenefitItemEvent.OptionTypeChanged(OptionType.DISCOUNT))
                binding.clDropdownMenu.visibility = View.GONE
            }
            binding.layoutProposalConditionCost.setOnClickListener { handleEvent(BenefitItemEvent.CriterionTypeChanged(CriterionType.PRICE)) }
            binding.layoutProposalConditionPeople.setOnClickListener { handleEvent(BenefitItemEvent.CriterionTypeChanged(CriterionType.HEADCOUNT)) }
//            binding.tvProposalOption2.setOnClickListener {
//                handleEvent(BenefitItemEvent.OptionTypeChanged(OptionType.DISCOUNT))
//                binding.clDropdownMenu.visibility = View.GONE
//            }

            // 포커스를 잃었을 때만 ViewModel 업데이트
            binding.etFragmentServiceProposalContent.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isBinding) { // 포커스를 잃었을 때
                    handleEvent(BenefitItemEvent.CriterionValueChanged(binding.etFragmentServiceProposalContent.text.toString()))
                }
            }
            binding.etProposalProvideCategory.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isBinding) { // 포커스를 잃었을 때
                    handleEvent(BenefitItemEvent.CategoryChanged(binding.etProposalProvideCategory.text.toString()))
                }
            }
        }

        fun bind(item: BenefitItem) {
            isBinding = true

            binding.layoutProposalProvideCategory.visibility = if (item.goods.size >= 2 && item.optionType == OptionType.SERVICE) View.VISIBLE else View.GONE
            updateCriterionUi(item.criterionType)

            if (item.optionType == OptionType.SERVICE) {
                binding.tvFragmentServiceProposalDropDown.text = "서비스 제공"
                binding.tvProposalProvide.text = "제공 항목"
                setupGoodsLayout(item)
            } else { // DISCOUNT
                binding.tvFragmentServiceProposalDropDown.text = "할인 혜택"
                binding.tvProposalProvide.text = "할인율"
                setupDiscountLayout(item)
            }

            // EditText 값 설정
            if (binding.etFragmentServiceProposalContent.text.toString() != item.criterionValue) {
                binding.etFragmentServiceProposalContent.setText(item.criterionValue)
            }
            if (binding.etProposalProvideCategory.text.toString() != item.category) {
                binding.etProposalProvideCategory.setText(item.category)
            }

            isBinding = false

//            // 드롭다운 펼치기
//            binding.layoutFragmentServiceProposalDropDownBg.setOnClickListener {
//                binding.clDropdownMenu.visibility = View.VISIBLE
//            }
//
//            // 삭제하기
//            binding.tvProposalDelete.setOnClickListener {
//                handleEvent(BenefitItemEvent.ItemRemoved)
//            }
//
//            // '서비스 제공' 선택
//            binding.tvProposalOption1.setOnClickListener {
//                handleEvent(BenefitItemEvent.OptionTypeChanged(OptionType.SERVICE))
//                binding.clDropdownMenu.visibility = View.GONE
//            }
//            // '할인 혜택' 선택
//            binding.tvProposalOption2.setOnClickListener {
//                handleEvent(BenefitItemEvent.OptionTypeChanged(OptionType.DISCOUNT))
//                binding.clDropdownMenu.visibility = View.GONE
//            }
//
//            // '금액' 기준 선택
//            binding.layoutProposalConditionCost.setOnClickListener {
//                handleEvent(BenefitItemEvent.CriterionTypeChanged(CriterionType.PRICE))
//            }
//            // '인원' 기준 선택
//            binding.layoutProposalConditionPeople.setOnClickListener {
//                handleEvent(BenefitItemEvent.CriterionTypeChanged(CriterionType.HEADCOUNT))
//            }
//
//            // 기준값 입력
//            binding.etFragmentServiceProposalContent.doAfterTextChanged { text ->
//                handleEvent(BenefitItemEvent.CriterionValueChanged(text.toString()))
//            }
//            // 카테고리 입력
//            binding.etProposalProvideCategory.doAfterTextChanged { text ->
//                handleEvent(BenefitItemEvent.CategoryChanged(text.toString()))
//            }
//
//            binding.ivFragmentServiceProposalAddGoodsBtn.text =
//                if(item.optionType == OptionType.SERVICE) "+  추가"
//                else "%  할인"

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
//            // 삭제하기 클릭 리스너
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

        // ✅ Payload를 이용한 부분 업데이트
        fun bindWithPayload(item: BenefitItem, payloads: MutableList<Any>) {
            // 필요한 경우 특정 부분만 업데이트
            bind(item)
        }

        private fun updateCriterionUi(type: CriterionType) {
            val ctx = binding.root.context
            val isPrice = type == CriterionType.PRICE

            binding.ivProposalConditionCost.setImageResource(if (isPrice) R.drawable.ic_check_selected else R.drawable.ic_check_unselected)
            binding.tvProposalConditionCost.setTextColor(ContextCompat.getColor(ctx, if (isPrice) R.color.assu_main else R.color.assu_font_sub))

            binding.ivProposalConditionPeople.setImageResource(if (!isPrice) R.drawable.ic_check_selected else R.drawable.ic_check_unselected)
            binding.tvProposalConditionPeople.setTextColor(ContextCompat.getColor(ctx, if (!isPrice) R.color.assu_main else R.color.assu_font_sub))

            binding.etFragmentServiceProposalContent.hint = if (isPrice) "10,000" else "2"
            binding.tvProposalConditionUnit.text = if (isPrice) "원 이상일 경우," else "인 이상일 경우,"
        }

        private fun setupGoodsLayout(item: BenefitItem) {
            val container = binding.flexboxServiceProposalItem
            val addBtn = binding.ivFragmentServiceProposalAddGoodsBtn

            clearDynamicViews(container)

            addBtn.text = "+  추가"
            addBtn.setBackgroundResource(R.drawable.bg_message_mine)
            addBtn.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            addBtn.isEnabled = true
            addBtn.isClickable = true
            addBtn.setOnClickListener { handleEvent(BenefitItemEvent.GoodAdded) }

            // ✅ 서비스 제공 항목들 생성 (최소 1개는 보장)
            val goodsList = if (item.goods.isEmpty()) listOf("") else item.goods

            goodsList.forEachIndexed { index, text ->
                addGoodEditText(container, addBtn, index, text, "캔콜라")
            }
        }

        private fun setupDiscountLayout(item: BenefitItem) {
            val container = binding.flexboxServiceProposalItem
            val addBtn = binding.ivFragmentServiceProposalAddGoodsBtn

            clearDynamicViews(container)

            addBtn.text = "% 할인"
            addBtn.background = null // 배경 제거
            addBtn.setTextColor(ContextCompat.getColor(binding.root.context, R.color.assu_font_sub))
            addBtn.isEnabled = false
            addBtn.isClickable = false
            addBtn.setOnClickListener(null)

            addDiscountEditText(container, addBtn, item.discountRate, "10")
        }

        // ✅ 동적 뷰들을 깨끗하게 제거하는 함수
        private fun clearDynamicViews(container: ViewGroup) {
            val viewsToRemove = container.children.filter { view ->
                view.tag == "dynamic_good" || view.tag == "dynamic_discount"
            }.toList()
            viewsToRemove.forEach { container.removeView(it) }
        }

        // ✅ 서비스 제공 항목 EditText 추가
        private fun addGoodEditText(container: ViewGroup, addBtn: View, index: Int, text: String, hint: String) {
            val goodBinding = ItemProposalOptionEtBinding.inflate(
                LayoutInflater.from(container.context),
                container,
                false
            )
            val editText = goodBinding.etProposalItem

            editText.setText(text)
            editText.tag = "dynamic_good"
            editText.hint = hint

            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isBinding) {
                    handleEvent(BenefitItemEvent.GoodUpdated(index, editText.text.toString()))
                }
            }

            container.addView(goodBinding.root, container.indexOfChild(addBtn))
        }

        // ✅ 할인율 EditText 추가
        private fun addDiscountEditText(container: ViewGroup, addBtn: View, text: String, hint: String) {
            val goodBinding = ItemProposalOptionEtBinding.inflate(
                LayoutInflater.from(container.context),
                container,
                false
            )
            val editText = goodBinding.etProposalItem

            editText.setText(text)
            editText.hint = hint
            editText.tag = "dynamic_discount"

            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isBinding) {
                    handleEvent(BenefitItemEvent.DiscountRateChanged(editText.text.toString()))
                }
            }

            container.addView(goodBinding.root, container.indexOfChild(addBtn))
        }

        private fun handleEvent(event: BenefitItemEvent) {
            if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                onItemEvent(bindingAdapterPosition, event)
            }
        }
    }
}