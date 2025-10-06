package com.ssu.assu.domain.usecase.review

import com.ssu.assu.data.dto.review.response.PageReviewList
import com.ssu.assu.data.repository.review.ReviewRepository
import com.ssu.assu.util.RetrofitResult
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