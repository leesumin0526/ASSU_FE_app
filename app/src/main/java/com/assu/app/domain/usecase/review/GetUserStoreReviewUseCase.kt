package com.assu.app.domain.usecase.review

import com.assu.app.data.dto.review.response.PageReviewList
import com.assu.app.data.repository.review.ReviewRepository
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetUserStoreReviewUseCase @Inject constructor(
    private val repo: ReviewRepository
) {
    suspend operator fun invoke(
        storeId: Long,
        page: Int,
        size: Int,
        sort: String
    ) : RetrofitResult<PageReviewList>{
        return repo.getStoreReview(storeId ,page, size, sort)
    }
}