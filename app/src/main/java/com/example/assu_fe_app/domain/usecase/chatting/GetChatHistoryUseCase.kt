package com.example.assu_fe_app.domain.usecase.chatting

import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.domain.model.chatting.GetChatHistoryModel
import com.example.assu_fe_app.util.RetrofitResult
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