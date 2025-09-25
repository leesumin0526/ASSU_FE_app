package com.example.assu_fe_app.presentation.common.chatting.proposal.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        private val updateScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        private var updateJob: Job? = null

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
            binding.etFragmentServiceProposalContent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isBinding) {
                        updateJob?.cancel()
                        updateJob = updateScope.launch {
                            delay(300)
                            handleEvent(BenefitItemEvent.CriterionValueChanged(s.toString()))
                        }
                    }
                }
            })

            binding.etProposalProvideCategory.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isBinding) {
                        updateJob?.cancel()
                        updateJob = updateScope.launch {
                            delay(300)
                            handleEvent(BenefitItemEvent.CategoryChanged(s.toString()))
                        }
                    }
                }
            })
        }

        fun bind(item: BenefitItem) {
            binding.etFragmentServiceProposalContent2.visibility = View.GONE

            isBinding = true

            val focusedView = binding.root.findFocus()
            val focusedPosition = if (focusedView is EditText) {
                focusedView.selectionStart
            } else -1

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

            if (focusedView != null && focusedPosition >= 0) {
                focusedView.post {
                    focusedView.requestFocus()
                    if (focusedView is EditText && focusedPosition <= focusedView.text.length) {
                        focusedView.setSelection(focusedPosition)
                    }
                }
            }

            isBinding = false
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

            editText.isFocusable = true
            editText.isFocusableInTouchMode = true

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isBinding) {
                        // 기존 작업 취소
                        updateJob?.cancel()
                        // 300ms 지연 후 업데이트
                        updateJob = updateScope.launch {
                            delay(300)
                            handleEvent(BenefitItemEvent.GoodUpdated(index, s.toString()))
                        }
                    }
                }
            })

            editText.setOnClickListener {
                editText.requestFocus()
                val imm = container.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }

            container.addView(goodBinding.root, container.indexOfChild(addBtn))

//            if (text.isEmpty()) {
//                editText.post {
//                    editText.requestFocus()
//                    val imm = container.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
//                }
//            }
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

            editText.isFocusable = true
            editText.isFocusableInTouchMode = true

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!isBinding) {
                        updateJob?.cancel()
                        updateJob = updateScope.launch {
                            delay(300)
                            handleEvent(BenefitItemEvent.DiscountRateChanged(s.toString()))
                        }
                    }
                }
            })

//            editText.setOnClickListener {
//                editText.requestFocus()
//                val imm = container.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
//            }

            container.addView(goodBinding.root, container.indexOfChild(addBtn))
        }

        private fun handleEvent(event: BenefitItemEvent) {
            if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                onItemEvent(bindingAdapterPosition, event)
            }
        }
    }
}