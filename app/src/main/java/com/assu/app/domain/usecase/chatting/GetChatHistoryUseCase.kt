package com.assu.app.domain.usecase.chatting

import com.assu.app.data.repository.chatting.ChattingRepository
import com.assu.app.domain.model.chatting.GetChatHistoryModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class GetChatHistoryUseCase @Inject constructor(
    private val repo: ChattingRepository
){
    suspend operator fun invoke(
        roomId: Long
    ): RetrofitResult<GetChatHistoryModel> {
        return repo.getChatHistory(roomId)
    }
}