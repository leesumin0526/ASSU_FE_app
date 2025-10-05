package com.assu.app.domain.usecase.chatting

import com.assu.app.data.repository.chatting.ChattingRepository
import com.assu.app.domain.model.chatting.CheckBlockModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class CheckBlockOpponentUseCase @Inject constructor(
    private val repo: ChattingRepository
){
    suspend operator fun invoke(opponentId: Long) : RetrofitResult<CheckBlockModel> {
        return repo.checkBlockOpponent(opponentId)
    }
}