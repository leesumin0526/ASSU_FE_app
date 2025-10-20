package com.ssu.assu.presentation.user.location

import com.bumptech.glide.Glide
import com.ssu.assu.R
import com.ssu.assu.databinding.ItemUserLocationBinding
import com.ssu.assu.presentation.base.BaseFragment
import kotlin.math.roundToInt

class UserLocationItemFragment :
    BaseFragment<ItemUserLocationBinding>(R.layout.item_user_location) {

    override fun initObserver() = Unit
    override fun initView() = Unit

    // 화면에 뿌릴 아이템 모델
    data class UserStoreItem(
        val shopName: String,
        val criterionType: String,
        val rating: Float, // 0.0 ~ 5.0
        val profileImg: String? // URL일 수도 있으므로 nullable로 변경
    )

    fun bind(item: UserStoreItem) {
        binding.tvLocationItemShopName.text = item.shopName
        binding.tvLocationItemDescription.text = item.criterionType
        setRating(item.rating)
        loadProfile(item.profileImg)
    }

    /** 프로필 이미지 로드 — 없으면 기본이미지(img_partner) */
    private fun loadProfile(imageUrl: String?) {
        val iv = binding.ivLocationItemMainImage
        val fallbackRes = R.drawable.img_partner

        if (imageUrl.isNullOrBlank() || imageUrl.endsWith(".svg", ignoreCase = true)) {
            iv.setImageResource(fallbackRes)
            return
        }

        Glide.with(iv.context)
            .load(imageUrl)
            .placeholder(fallbackRes)
            .error(fallbackRes)
            .into(iv)
    }

    /** 별점(float) 세팅 — 반올림해서 0~5 정수로 처리 */
    fun setRating(rating: Float) {
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

        stars.forEach { it.setImageResource(unselected) }
        for (i in 0 until clamped) {
            stars[i].setImageResource(selected)
        }
    }
}