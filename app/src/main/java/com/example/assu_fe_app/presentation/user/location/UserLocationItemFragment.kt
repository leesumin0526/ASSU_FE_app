package com.example.assu_fe_app.presentation.user.location

import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ItemUserLocationBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import kotlin.math.roundToInt

class UserLocationItemFragment :
    BaseFragment<ItemUserLocationBinding>(R.layout.item_user_location) {

    override fun initObserver() = Unit
    override fun initView() = Unit

    // 화면에 뿌릴 아이템 모델
    data class UserStoreItem(
        val shopName: String,
        val criterionType: String,
        val rating: Float // 0.0 ~ 5.0
    )

    fun bind(item: UserStoreItem) {
        binding.tvLocationItemShopName.text = item.shopName
        binding.tvLocationItemDescription.text = item.criterionType
        setRating(item.rating)
    }

    /** 별점(float) 세팅 — 반올림해서 0~5 정수로 처리 */
    fun setRating(rating: Float) {
        // 반올림 + 범위 보정
        val rounded = rating.coerceIn(0f, 5f).roundToInt()
        setRating(rounded)
    }

    /** 별점(int) 세팅 — 0~5 */
    fun setRating(rating: Int) {
        val clamped = rating.coerceIn(0, 5)

        val selected = R.drawable.ic_location_item_star_selected
        val unselected = R.drawable.ic_location_item_star_unselected

        val stars = arrayOf(
            binding.ivLocationItemStar1,
            binding.ivLocationItemStar2,
            binding.ivLocationItemStar3,
            binding.ivLocationItemStar4,
            binding.ivLocationItemStar5
        )

        // 1) 먼저 전부 unselected로 초기화 (재활용 대비)
        stars.forEach { it.setImageResource(unselected) }

        // 2) 필요한 개수만 selected로 채우기
        for (i in 0 until clamped) {
            stars[i].setImageResource(selected)
        }
    }
}