package com.ssu.assu.domain.usecase.review

import android.util.Log
import com.ssu.assu.data.dto.review.response.PageReviewList
import com.ssu.assu.data.repository.review.ReviewRepository
import com.ssu.assu.util.RetrofitResult
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