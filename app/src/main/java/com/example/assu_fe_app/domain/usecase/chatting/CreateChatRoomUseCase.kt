package com.example.assu_fe_app.domain.usecase.chatting

import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.domain.model.chatting.CreateChatRoomModel
import com.example.assu_fe_app.util.RetrofitResult
import javax.inject.Inject


// domain/usecase/chatting/CreateChatRoomUseCase.kt
class CreateChatRoomUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(req: CreateChatRoomRequestDto): RetrofitResult<CreateChatRoomModel> {
        return repo.createChatRoom(req)
    }
}