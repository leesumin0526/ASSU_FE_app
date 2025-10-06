package com.ssu.assu.domain.usecase.chatting

import com.ssu.assu.data.repository.chatting.ChattingRepository
import com.ssu.assu.domain.model.chatting.UnblockOpponentModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class UnblockOpponentUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(blockedId: Long): RetrofitResult<UnblockOpponentModel> {
        return repo.unblockOpponent(blockedId)
    }
}