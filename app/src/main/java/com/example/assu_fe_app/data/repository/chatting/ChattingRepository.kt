package com.example.assu_fe_app.data.repository.chatting

import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.domain.model.chatting.CreateChatRoomModel

interface ChattingRepository {
    suspend fun createChatRoom(request: CreateChatRoomRequestDto): CreateChatRoomModel
}