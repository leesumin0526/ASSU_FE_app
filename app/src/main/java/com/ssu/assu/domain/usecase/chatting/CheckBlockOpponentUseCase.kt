package com.ssu.assu.domain.usecase.chatting

import com.ssu.assu.data.repository.chatting.ChattingRepository
import com.ssu.assu.domain.model.chatting.CheckBlockModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject

class CheckBlockOpponentUseCase @Inject constructor(
    private val repo: ChattingRepository
){
    suspend operator fun invoke(opponentId: Long) : RetrofitResult<CheckBlockModel> {
        return repo.checkBlockOpponent(opponentId)
    }
}