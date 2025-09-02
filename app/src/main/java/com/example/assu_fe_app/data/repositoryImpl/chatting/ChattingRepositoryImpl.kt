package com.example.assu_fe_app.data.repositoryImpl.chatting

import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.data.service.chatting.ChattingService
import com.example.assu_fe_app.domain.model.chatting.CreateChatRoomModel
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import javax.inject.Inject

class ChattingRepositoryImpl @Inject constructor(
    private val api: ChattingService
) : ChattingRepository {
        override suspend fun createChatRoom(
            request: CreateChatRoomRequestDto
        ): RetrofitResult<CreateChatRoomModel> {
            return apiHandler(
                {api.createChatRoom(request)},
                {dto -> dto.toModel()}
            )
        }
    }