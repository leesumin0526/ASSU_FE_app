package com.assu.app.domain.usecase.chatting

import com.assu.app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.assu.app.data.repository.chatting.ChattingRepository
import com.assu.app.domain.model.chatting.CreateChatRoomModel
import com.assu.app.util.RetrofitResult
import javax.inject.Inject


// domain/usecase/chatting/CreateChatRoomUseCase.kt
class CreateChatRoomUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(req: CreateChatRoomRequestDto): RetrofitResult<CreateChatRoomModel> {
        return repo.createChatRoom(req)
    }
}