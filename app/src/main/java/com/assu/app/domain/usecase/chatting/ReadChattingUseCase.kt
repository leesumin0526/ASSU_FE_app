package com.assu.app.domain.usecase.chatting

import com.assu.app.data.repository.chatting.ChattingRepository
import com.assu.app.domain.model.chatting.ReadChattingModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject

class  ReadChattingUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(roomId: Long): RetrofitResult<ReadChattingModel> {
        return repo.readChatting(roomId)
    }
}