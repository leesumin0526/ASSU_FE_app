package com.ssu.assu.presentation.admin.home.adapter

import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssu.assu.R
import com.ssu.assu.data.dto.OfferType
import com.ssu.assu.data.dto.ProposalItem
import com.ssu.assu.databinding.ItemServiceProposalSetBinding
import java.text.NumberFormat
import java.util.Locale

class ServicePassiveProposalAdapter(
    private val onItemChanged: () -> Unit
) : RecyclerView.Adapter<ServicePassiveProposalAdapter.ServiceProposalViewHolder>() {

    private val items = mutableListOf<ProposalItem>()

    fun setItems(newItems: List<ProposalItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
        onItemChanged()
    }

    fun addItem(item: ProposalItem = ProposalItem()) {
        if (item.contents.isEmpty()) item.contents.add("")
        items.add(item)
        notifyItemInserted(items.size - 1)
        onItemChanged()
    }

    fun getItems(): List<ProposalItem> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceProposalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServiceProposalSetBinding.inflate(inflater, parent, false)
        return ServiceProposalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceProposalViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ServiceProposalViewHolder(
        private val binding: ItemServiceProposalSetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // ------ 유틸 ------
        private fun replaceTextWatcher(et: EditText, watcher: TextWatcher) {
            (et.tag as? TextWatcher)?.let { old -> et.removeTextChangedListener(old) }
            et.addTextChangedListener(watcher)
            et.tag = watcher
        }

        private fun ensureInContainer(view: View, container: ViewGroup) {
            val parent = view.parent
            if (parent is ViewGroup && parent !== container) {
                parent.removeView(view)
            }
            if (view.parent == null) {
                container.addView(view)
            }
        }

        private fun String.digitsOnly(): String = filter(Char::isDigit)

        private fun formatWon(digits: String): String {
            if (digits.isEmpty()) return ""
            return try {
                NumberFormat.getNumberInstance(Locale.KOREA).format(digits.toLong())
            } catch (_: Exception) {
                digits
            }
        }

        // ------ 본문 ------
        fun bind(item: ProposalItem) {
            val ctx = binding.root.context

            // 드롭다운 기본 닫힘
            binding.clDropdownMenu.visibility = View.GONE

            // 상단 드롭다운(타이틀) 표시
            binding.tvFragmentServiceProposalDropDown.text = item.content.ifEmpty { "서비스 제공" }

            // 상단 제목 입력
            binding.etFragmentServiceProposalContent2.setText(item.content)
            replaceTextWatcher(
                binding.etFragmentServiceProposalContent2,
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val newVal = s?.toString().orEmpty()
                        if (item.content != newVal) {
                            item.content = newVal
                            Log.d("AdapterData", "Main content updated: ${item.content}")
                            onItemChanged()
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }
            )

            // 드롭다운 열기
            binding.layoutFragmentServiceProposalDropDownBg.setOnClickListener {
                binding.clDropdownMenu.bringToFront()
                binding.tvProposalDropDown.text = item.content.ifEmpty { "서비스 제공" }
                binding.clDropdownMenu.visibility = View.VISIBLE
            }

            // 카드 삭제
            binding.tvProposalDelete.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    items.removeAt(pos)
                    notifyItemRemoved(pos)
                    onItemChanged()
                }
            }

            // 조건(가격/인원) UI 동기화
            fun applyConditionUI() {
                val cost = item.condition == ProposalItem.CONDITION_COST
                val people = item.condition == ProposalItem.CONDITION_PEOPLE

                binding.tvProposalConditionCost.setTextColor(
                    ContextCompat.getColor(ctx, if (cost) R.color.assu_main else R.color.assu_font_sub)
                )
                binding.ivProposalConditionCost.setImageResource(
                    if (cost) R.drawable.ic_check_selected else R.drawable.ic_check_unselected
                )

                binding.tvProposalConditionPeople.setTextColor(
                    ContextCompat.getColor(ctx, if (people) R.color.assu_main else R.color.assu_font_sub)
                )
                binding.ivProposalConditionPeople.setImageResource(
                    if (people) R.drawable.ic_check_selected else R.drawable.ic_check_unselected
                )

                if (cost) {
                    binding.etFragmentServiceProposalContent.hint = item.least.ifEmpty { "10,000" }
                    binding.tvProposalConditionUnit.text = "원 이상일 경우,"
                } else if (people) {
                    binding.etFragmentServiceProposalContent.hint = "2"
                    binding.tvProposalConditionUnit.text = "명 이상일 경우,"
                }
            }

            binding.layoutProposalConditionCost.setOnClickListener {
                item.condition = ProposalItem.CONDITION_COST
                applyConditionUI()
                onItemChanged()
            }
            binding.layoutProposalConditionPeople.setOnClickListener {
                item.condition = ProposalItem.CONDITION_PEOPLE
                applyConditionUI()
                onItemChanged()
            }

            replaceTextWatcher(
                binding.etFragmentServiceProposalContent,
                object : TextWatcher {
                    private var selfChanging = false
                    override fun afterTextChanged(s: Editable?) {
                        if (selfChanging) return
                        val raw = s?.toString().orEmpty()
                        val digits = raw.digitsOnly()
                        item.num = digits
                        onItemChanged()
                        // 보기 좋게 천단위 포맷으로 즉시 표시 (커서 끝으로)
                        val formatted = formatWon(digits)
                        if (raw != formatted) {
                            selfChanging = true
                            binding.etFragmentServiceProposalContent.setText(formatted)
                            binding.etFragmentServiceProposalContent.setSelection(formatted.length)
                            selfChanging = false
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }
            )
            run {
                val want = formatWon(item.num)
                val has  = binding.etFragmentServiceProposalContent.text?.toString().orEmpty()
                if (want != has) {
                    binding.etFragmentServiceProposalContent.setText(want)
                    binding.etFragmentServiceProposalContent.setSelection(want.length)
                }
            }

            // 서비스 선택
            binding.tvProposalOption1.setOnClickListener {
                binding.tvFragmentServiceProposalDropDown.text = "서비스 제공"
                item.content = "서비스 제공"
                item.offerType = OfferType.SERVICE
                item.placeholder = "캔콜라"

                if (item.contents.isEmpty()) item.contents.add("")

                binding.ivFragmentServiceProposalAddGoodsBtn.text = "+  추가"
                binding.ivFragmentServiceProposalAddGoodsBtn.visibility = View.VISIBLE

                binding.clDropdownMenu.visibility = View.GONE
                onItemChanged()
                notifyItemChanged(bindingAdapterPosition)

                binding.layoutProposalProvideCategory.visibility =
                    if (item.contents.size >= 2) View.VISIBLE else View.GONE
            }

            // 할인 선택
            binding.tvProposalOption2.setOnClickListener {
                binding.tvFragmentServiceProposalDropDown.text = "할인 혜택"
                item.content = "할인 혜택"
                item.offerType = OfferType.DISCOUNT
                item.placeholder = "10" // 10%

                // 할인: 1칸 고정(값 보존)
                if (item.contents.isEmpty()) {
                    item.contents.add("")
                } else if (item.contents.size > 1) {
                    val keep = item.contents.firstOrNull { it.isNotBlank() } ?: item.contents.first()
                    item.contents.clear()
                    item.contents.add(keep)
                }

                binding.ivFragmentServiceProposalAddGoodsBtn.text = "할인"
                binding.ivFragmentServiceProposalAddGoodsBtn.visibility = View.VISIBLE

                binding.clDropdownMenu.visibility = View.GONE
                onItemChanged()
                notifyItemChanged(bindingAdapterPosition)
                binding.layoutProposalProvideCategory.visibility = View.GONE
            }

            // ====== 옵션 입력칸 렌더링 ======
            val container = binding.flexboxServiceProposalItem
            val addBtn = binding.ivFragmentServiceProposalAddGoodsBtn

            container.removeAllViews()

            val isDiscount = item.offerType == OfferType.DISCOUNT
            if (isDiscount) {
                addBtn.text = "할인"
                addBtn.visibility = View.VISIBLE
                if (item.contents.isEmpty()) item.contents.add("")
                if (item.contents.size > 1) {
                    val keep = item.contents.firstOrNull { it.isNotBlank() } ?: item.contents.first()
                    item.contents.clear()
                    item.contents.add(keep)
                }
            } else {
                addBtn.text = "+  추가"
                addBtn.visibility = View.VISIBLE
                if (item.contents.isEmpty()) item.contents.add("")
            }

            // --- 옵션 EditText들 + 더블클릭 삭제(서비스 모드 한정) ---
            val DOUBLE_CLICK_MS = 250L
            item.contents.forEachIndexed { idx, text ->
                val optionView = LayoutInflater.from(container.context)
                    .inflate(R.layout.item_proposal_option_et, container, false)

                val et = optionView.findViewById<EditText>(R.id.et_proposal_item)
                et.hint = if (text.isEmpty()) item.placeholder else ""
                et.setText(text)

                // 값 바인딩
                replaceTextWatcher(et, object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val newVal = s?.toString().orEmpty()
                        if (idx < item.contents.size && item.contents[idx] != newVal) {
                            item.contents[idx] = newVal
                            Log.d("AdapterData", "contents[$idx] = $newVal (pos=$bindingAdapterPosition)")
                            onItemChanged()
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })

                optionView.setOnClickListener {
                    if (item.offerType != OfferType.SERVICE) return@setOnClickListener

                    val now = SystemClock.uptimeMillis()
                    val last = (optionView.tag as? Long) ?: 0L
                    optionView.tag = now

                    if (now - last <= 500L) { // 250ms 이내 = 더블클릭
                        if (item.contents.size > 1) {
                            item.contents.removeAt(idx)
                            notifyItemChanged(bindingAdapterPosition)
                            onItemChanged()

                            binding.layoutProposalProvideCategory.visibility =
                                if (item.offerType == OfferType.SERVICE && item.contents.size >= 2)
                                    View.VISIBLE else View.GONE
                        }
                    }
                }

                container.addView(optionView)
            }

            container.addView(addBtn)

            // 항상 컨테이너 맨 아래에 버튼 붙이기
            ensureInContainer(addBtn, container)

            // 버튼 동작: 서비스는 추가, 할인은 동작 없음(표시만 "할인")
            addBtn.setOnClickListener {
                if (item.offerType == OfferType.DISCOUNT) {
                    return@setOnClickListener
                }
                item.contents.add("")
                notifyItemChanged(bindingAdapterPosition)  // 재바인딩 → 금액은 위 복구 로직으로 유지
                onItemChanged()

                binding.layoutProposalProvideCategory.visibility =
                    if (item.contents.size >= 2) View.VISIBLE else View.GONE
            }

            // ===== 카테고리 UI 바인딩 =====
            fun updateCategoryVisibility() {
                val show = item.offerType == OfferType.SERVICE && item.contents.size >= 2
                binding.layoutProposalProvideCategory.visibility = if (show) View.VISIBLE else View.GONE
            }

            // 값 복원(재바인딩 대비)
            val wantCat = item.category.orEmpty()
            val hasCat  = binding.etProposalProvideCategory.text?.toString().orEmpty()
            if (wantCat != hasCat) binding.etProposalProvideCategory.setText(wantCat)

            // EditText ↔ 상태 동기화
            replaceTextWatcher(
                binding.etProposalProvideCategory,
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val v = s?.toString()?.trim().orEmpty()
                        if (item.category != v) {
                            item.category = v
                            onItemChanged()
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }
            )

            // 현재 표시 상태 반영
            updateCategoryVisibility()

            applyConditionUI()

        }
    }


}