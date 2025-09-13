package com.example.assu_fe_app.domain.usecase.chatting

import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.domain.model.chatting.ReadChattingModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject

class  ReadChattingUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(roomId: Long): RetrofitResult<ReadChattingModel> {
        return repo.readChatting(roomId)
    }
}