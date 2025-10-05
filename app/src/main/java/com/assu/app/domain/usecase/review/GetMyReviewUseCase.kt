package com.assu.app.domain.usecase.review

import android.util.Log
import com.assu.app.data.dto.review.response.PageReviewList
import com.assu.app.data.repository.review.ReviewRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetMyReviewUseCase @Inject constructor(
    private val repo: ReviewRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        sort: String
    ) : RetrofitResult<PageReviewList>{
        Log.d("UseCase✨", "usecase is runed")
        return repo.getReview(page, size, sort)
    }

}