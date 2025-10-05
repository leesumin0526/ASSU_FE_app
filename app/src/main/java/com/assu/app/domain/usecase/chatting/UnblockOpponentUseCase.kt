package com.assu.app.domain.usecase.chatting

import com.assu.app.data.repository.chatting.ChattingRepository
import com.assu.app.domain.model.chatting.UnblockOpponentModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class UnblockOpponentUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(blockedId: Long): RetrofitResult<UnblockOpponentModel> {
        return repo.unblockOpponent(blockedId)
    }
}