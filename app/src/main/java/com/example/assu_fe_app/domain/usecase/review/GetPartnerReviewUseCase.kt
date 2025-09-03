package com.example.assu_fe_app.domain.usecase.review

import android.util.Log
import com.example.assu_fe_app.data.dto.review.PageReviewList
import com.example.assu_fe_app.data.repository.review.ReviewRepository
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class GetPartnerReviewUseCase @Inject constructor(
    private val repo: ReviewRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        sort: String
    ) : RetrofitResult<PageReviewList>{
        Log.d("UseCase✨", "usecase is runed")
        return repo.getPartnerReview(page, size, sort)
    }
}