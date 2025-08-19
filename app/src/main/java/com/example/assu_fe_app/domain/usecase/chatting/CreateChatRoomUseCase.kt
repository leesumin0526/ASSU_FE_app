package com.example.assu_fe_app.domain.usecase.chatting

import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import javax.inject.Inject

//import jakarta.inject.Inject

class CreateChatRoomUseCase @Inject constructor(
    private val repo: ChattingRepository
) {
    suspend operator fun invoke(request: CreateChatRoomRequestDto) = repo.createChatRoom(request)

}