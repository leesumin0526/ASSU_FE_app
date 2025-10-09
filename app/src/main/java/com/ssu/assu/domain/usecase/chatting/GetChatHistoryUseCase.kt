package com.ssu.assu.domain.usecase.chatting

import com.ssu.assu.data.repository.chatting.ChattingRepository
import com.ssu.assu.domain.model.chatting.GetChatHistoryModel
import com.ssu.assu.util.RetrofitResult
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