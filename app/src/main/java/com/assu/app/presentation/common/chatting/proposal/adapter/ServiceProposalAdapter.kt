package com.assu.app.presentation.common.chatting.proposal.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.assu.app.R
import com.assu.app.data.dto.partnership.BenefitItem
import com.assu.app.data.dto.partnership.CriterionType
import com.assu.app.data.dto.partnership.OptionType
import com.assu.app.databinding.ItemProposalOptionEtBinding
import com.assu.app.databinding.ItemServiceProposalSetBinding
import com.assu.app.ui.partnership.BenefitItemEvent

object BenefitDiffCallback : DiffUtil.ItemCallback<BenefitItem>() {
    override fun areItemsTheSame(oldItem: BenefitItem, newItem: BenefitItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BenefitItem, newItem: BenefitItem): Boolean {
        // 텍스트 필드는 무시하고 구조적 변경만 체크
        return oldItem.id == newItem.id &&
                oldItem.optionType == newItem.optionType &&
                oldItem.criterionType == newItem.criterionType &&
                oldItem.goods.size == newItem.goods.size
    }
}

class ServiceProposalAdapter(
    private val onItemEvent: (Int, BenefitItemEvent) -> Unit
) : ListAdapter<BenefitItem, ServiceProposalAdapter.ViewHolder>(BenefitDiffCallback) {

    // 로컬 캐시 - ViewModel 업데이트 없이 임시 저장
    private val localTextCache = mutableMapOf<String, String>()

    // 새로 추가된 goods의 인덱스 추적
    private var focusToGoodIndex = mutableMapOf<Int, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemServiceProposalSetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ViewHolder(
        private val binding: ItemServiceProposalSetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var isInternalUpdate = false
        private val textWatchers = mutableMapOf<String, TextWatcher>()

        init {
            setupClickListeners()
        }

        private fun setupClickListeners() {
            // 드롭다운 토글
            binding.layoutFragmentServiceProposalDropDownBg.setOnClickListener {
                binding.clDropdownMenu.bringToFront()
                binding.clDropdownMenu.visibility =
                    if (binding.clDropdownMenu.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }

            // 삭제 버튼
            binding.tvProposalDelete.setOnClickListener {
                handleEvent(BenefitItemEvent.ItemRemoved)
            }

            // 옵션 선택
            binding.tvProposalOption1.setOnClickListener {
                handleEvent(BenefitItemEvent.OptionTypeChanged(OptionType.SERVICE))
                binding.clDropdownMenu.visibility = View.GONE
            }

            binding.tvProposalOption2.setOnClickListener {
                handleEvent(BenefitItemEvent.OptionTypeChanged(OptionType.DISCOUNT))
                binding.clDropdownMenu.visibility = View.GONE
            }

            // 조건 타입 선택
            binding.layoutProposalConditionCost.setOnClickListener {
                handleEvent(BenefitItemEvent.CriterionTypeChanged(CriterionType.PRICE))
            }

            binding.layoutProposalConditionPeople.setOnClickListener {
                handleEvent(BenefitItemEvent.CriterionTypeChanged(CriterionType.HEADCOUNT))
            }
        }

        fun bind(item: BenefitItem, position: Int) {
            isInternalUpdate = true

            // 기본 UI 설정
            binding.etFragmentServiceProposalContent2.visibility = View.GONE
            binding.layoutProposalProvideCategory.visibility =
                if (item.goods.size >= 2 && item.optionType == OptionType.SERVICE) View.VISIBLE else View.GONE

            // Criterion UI 업데이트
            updateCriterionUi(item.criterionType)

            // 옵션 타입에 따른 UI 설정
            if (item.optionType == OptionType.SERVICE) {
                binding.tvFragmentServiceProposalDropDown.text = "서비스 제공"
                binding.tvProposalProvide.text = "제공 항목"
                setupGoodsLayout(item, position)
            } else {
                binding.tvFragmentServiceProposalDropDown.text = "할인 혜택"
                binding.tvProposalProvide.text = "할인율"
                setupDiscountLayout(item, position)
            }

            // 메인 EditText 설정
            setupMainEditTexts(item, position)

            isInternalUpdate = false
        }

        private fun setupMainEditTexts(item: BenefitItem, position: Int) {
            val criterionKey = "criterion_$position"
            val categoryKey = "category_$position"

            // 기존 TextWatcher 제거
            textWatchers["criterion"]?.let {
                binding.etFragmentServiceProposalContent.removeTextChangedListener(it)
            }
            textWatchers["category"]?.let {
                binding.etProposalProvideCategory.removeTextChangedListener(it)
            }

            // Criterion EditText
            val criterionText = localTextCache[criterionKey] ?: item.criterionValue
            if (binding.etFragmentServiceProposalContent.text.toString() != criterionText) {
                binding.etFragmentServiceProposalContent.setText(criterionText)
            }

            val criterionWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isInternalUpdate) {
                        localTextCache[criterionKey] = s.toString()
                        // 포커스를 잃었을 때만 ViewModel 업데이트
                    }
                }
            }
            binding.etFragmentServiceProposalContent.addTextChangedListener(criterionWatcher)
            textWatchers["criterion"] = criterionWatcher

            binding.etFragmentServiceProposalContent.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    // 포커스를 잃었을 때만 ViewModel 업데이트
                    val cachedValue = localTextCache[criterionKey]
                    if (cachedValue != null && cachedValue != item.criterionValue) {
                        handleEvent(BenefitItemEvent.CriterionValueChanged(cachedValue))
                    }
                }
            }

            // Category EditText
            val categoryText = localTextCache[categoryKey] ?: item.category
            if (binding.etProposalProvideCategory.text.toString() != categoryText) {
                binding.etProposalProvideCategory.setText(categoryText)
            }

            val categoryWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isInternalUpdate) {
                        localTextCache[categoryKey] = s.toString()
                    }
                }
            }
            binding.etProposalProvideCategory.addTextChangedListener(categoryWatcher)
            textWatchers["category"] = categoryWatcher

            binding.etProposalProvideCategory.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val cachedValue = localTextCache[categoryKey]
                    if (cachedValue != null && cachedValue != item.category) {
                        handleEvent(BenefitItemEvent.CategoryChanged(cachedValue))
                    }
                }
            }
        }

        private fun updateCriterionUi(type: CriterionType) {
            val ctx = binding.root.context
            val isPrice = type == CriterionType.PRICE

            binding.ivProposalConditionCost.setImageResource(
                if (isPrice) R.drawable.ic_check_selected else R.drawable.ic_check_unselected
            )
            binding.tvProposalConditionCost.setTextColor(
                ContextCompat.getColor(ctx, if (isPrice) R.color.assu_main else R.color.assu_font_sub)
            )

            binding.ivProposalConditionPeople.setImageResource(
                if (!isPrice) R.drawable.ic_check_selected else R.drawable.ic_check_unselected
            )
            binding.tvProposalConditionPeople.setTextColor(
                ContextCompat.getColor(ctx, if (!isPrice) R.color.assu_main else R.color.assu_font_sub)
            )

            binding.etFragmentServiceProposalContent.hint = if (isPrice) "10,000" else "2"
            binding.tvProposalConditionUnit.text = if (isPrice) "원 이상일 경우," else "인 이상일 경우,"
        }

        private fun setupGoodsLayout(item: BenefitItem, position: Int) {
            val container = binding.flexboxServiceProposalItem
            val addBtn = binding.ivFragmentServiceProposalAddGoodsBtn

            clearDynamicViews(container)

            // 추가 버튼 설정
            addBtn.text = "+  추가"
            addBtn.setBackgroundResource(R.drawable.bg_message_mine)
            addBtn.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            addBtn.isEnabled = true
            addBtn.isClickable = true
            addBtn.setOnClickListener {
                // 새로 추가될 goods의 인덱스를 저장
                focusToGoodIndex[position] = item.goods.size
                handleEvent(BenefitItemEvent.GoodAdded)
            }

            val goodsList = if (item.goods.isEmpty()) listOf("") else item.goods

            // 포커스해야 할 인덱스 확인
            val shouldFocusIndex = focusToGoodIndex[position]

            goodsList.forEachIndexed { index, text ->
                val shouldFocus = (shouldFocusIndex == index)
                addGoodEditText(container, addBtn, position, index, text, "캔콜라", shouldFocus)
            }

            // 포커스 처리 후 클리어
            if (shouldFocusIndex != null) {
                focusToGoodIndex.remove(position)
            }
        }

        private fun setupDiscountLayout(item: BenefitItem, position: Int) {
            val container = binding.flexboxServiceProposalItem
            val addBtn = binding.ivFragmentServiceProposalAddGoodsBtn

            clearDynamicViews(container)

            addBtn.text = "% 할인"
            addBtn.background = null
            addBtn.setTextColor(ContextCompat.getColor(binding.root.context, R.color.assu_font_sub))
            addBtn.isEnabled = false
            addBtn.isClickable = false
            addBtn.setOnClickListener(null)

            addDiscountEditText(container, addBtn, position, item.discountRate, "10")
        }

        private fun clearDynamicViews(container: ViewGroup) {
            val viewsToRemove = container.children.filter { view ->
                view.tag == "dynamic_good" || view.tag == "dynamic_discount"
            }.toList()
            viewsToRemove.forEach { container.removeView(it) }
        }

        private fun addGoodEditText(
            container: ViewGroup,
            addBtn: View,
            itemPosition: Int,
            goodIndex: Int,
            text: String,
            hint: String,
            shouldFocus: Boolean = false
        ) {
            val goodBinding = ItemProposalOptionEtBinding.inflate(
                LayoutInflater.from(container.context),
                container,
                false
            )
            val editText = goodBinding.etProposalItem
            val cacheKey = "good_${itemPosition}_$goodIndex"

            goodBinding.root.tag = "dynamic_good"
            editText.hint = hint

            // 캐시된 값 우선, 없으면 원본 사용
            val displayText = localTextCache[cacheKey] ?: text
            isInternalUpdate = true
            editText.setText(displayText)
            isInternalUpdate = false

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isInternalUpdate) {
                        localTextCache[cacheKey] = s.toString()
                    }
                }
            })

            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val cachedValue = localTextCache[cacheKey]
                    if (cachedValue != null && cachedValue != text) {
                        handleEvent(BenefitItemEvent.GoodUpdated(goodIndex, cachedValue))
                        localTextCache.remove(cacheKey) // 업데이트 후 캐시 클리어
                    }
                }
            }

            container.addView(goodBinding.root, container.indexOfChild(addBtn))

            // 새로 추가된 필드에 포커스
            if (shouldFocus) {
                editText.post {
                    editText.requestFocus()
                    val imm = container.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }

        private fun addDiscountEditText(
            container: ViewGroup,
            addBtn: View,
            itemPosition: Int,
            text: String,
            hint: String
        ) {
            val goodBinding = ItemProposalOptionEtBinding.inflate(
                LayoutInflater.from(container.context),
                container,
                false
            )
            val editText = goodBinding.etProposalItem
            val cacheKey = "discount_$itemPosition"

            goodBinding.root.tag = "dynamic_discount"
            editText.hint = hint

            val displayText = localTextCache[cacheKey] ?: text
            isInternalUpdate = true
            editText.setText(displayText)
            isInternalUpdate = false

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isInternalUpdate) {
                        localTextCache[cacheKey] = s.toString()
                    }
                }
            })

            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val cachedValue = localTextCache[cacheKey]
                    if (cachedValue != null && cachedValue != text) {
                        handleEvent(BenefitItemEvent.DiscountRateChanged(cachedValue))
                        localTextCache.remove(cacheKey)
                    }
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