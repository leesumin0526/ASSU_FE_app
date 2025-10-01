package com.example.assu_fe_app.domain.usecase.chatting

import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.domain.model.chatting.UnblockOpponentModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class UnblockOpponentUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(blockedId: Long): RetrofitResult<UnblockOpponentModel> {
        return repo.unblockOpponent(blockedId)
    }
}