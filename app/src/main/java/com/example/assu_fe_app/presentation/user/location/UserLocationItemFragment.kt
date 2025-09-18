package com.example.assu_fe_app.presentation.user.location

import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ItemUserLocationBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class UserLocationItemFragment :
    BaseFragment<ItemUserLocationBinding>(R.layout.item_user_location) {

    override fun initObserver() = Unit
    override fun initView() = Unit

    // 화면에 뿌릴 아이템 모델 (필요시 프로젝트 모델로 교체해도 됨)
    data class UserStoreItem(
        val shopName: String,
        val criterionType: String, // 설명/기준 텍스트
        val rating: Float          // 0.0 ~ 5.0
    )

    /** 외부에서 호출해서 바인딩 */
    fun bind(item: UserStoreItem) {
        binding.tvLocationItemShopName.text = item.shopName
        binding.tvLocationItemDescription.text = item.criterionType
        setRating(item.rating)
    }

    /** 별점(float) 세팅 — 반올림해서 0~5 정수로 처리 */
    fun setRating(rating: Float) {
        val rounded = rating.coerceIn(0f, 5f).toInt()
        setRating(rounded)
    }

    /** 별점(int) 세팅 — 0~5 */
    fun setRating(rating: Int) {
        val selected = R.drawable.ic_location_item_star_selected
        val unselected = R.drawable.ic_location_item_star_unselected

        val stars = arrayOf(
            binding.ivLocationItemStar1,
            binding.ivLocationItemStar2,
            binding.ivLocationItemStar3,
            binding.ivLocationItemStar4,
            binding.ivLocationItemStar5
        )

        stars.forEachIndexed { idx, iv ->
            val res = if (idx < rating) selected else unselected
            iv.setImageResource(res)      // setBackground 말고 setImageResource 권장
        }
    }
}