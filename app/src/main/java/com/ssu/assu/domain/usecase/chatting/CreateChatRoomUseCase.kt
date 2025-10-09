package com.ssu.assu.domain.usecase.chatting

import com.ssu.assu.data.dto.chatting.request.CreateChatRoomRequestDto
import com.ssu.assu.data.repository.chatting.ChattingRepository
import com.ssu.assu.domain.model.chatting.CreateChatRoomModel
import com.ssu.assu.util.RetrofitResult
import javax.inject.Inject


// domain/usecase/chatting/CreateChatRoomUseCase.kt
class CreateChatRoomUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(req: CreateChatRoomRequestDto): RetrofitResult<CreateChatRoomModel> {
        return repo.createChatRoom(req)
    }
}