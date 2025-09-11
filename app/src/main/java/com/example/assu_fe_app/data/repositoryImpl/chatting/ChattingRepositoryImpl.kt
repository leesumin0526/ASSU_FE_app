package com.example.assu_fe_app.data.repositoryImpl.chatting

import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.repository.chatting.ChattingRepository
import com.example.assu_fe_app.data.service.chatting.ChattingService
import com.example.assu_fe_app.domain.model.chatting.CreateChatRoomModel
import com.example.assu_fe_app.domain.model.chatting.GetChatHistoryModel
import com.example.assu_fe_app.domain.model.chatting.GetChattingRoomListModel
import com.example.assu_fe_app.domain.model.chatting.LeaveChattingRoomModel
import com.example.assu_fe_app.domain.model.chatting.ReadChattingModel
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

    override suspend fun getChattingRoomList(
    ): RetrofitResult<List<GetChattingRoomListModel>> {
        return apiHandler(
            {api.getChattingRoomList()},
            {dtoList -> dtoList.map { it.toModel() }}
        )
    }

    override suspend fun getChatHistory(
        roomId: Long
    ): RetrofitResult<GetChatHistoryModel> {
        return apiHandler(
            {api.getChatHistory(roomId)},
            {dto -> dto.toModel()}
        )
    }

    override suspend fun leaveChattingRoom(
        roomId: Long
    ): RetrofitResult<LeaveChattingRoomModel> {
        return apiHandler(
            {api.leaveChatRoom(roomId)},
            {dto -> dto.toModel()}
        )
    }

    override suspend fun readChatting(
        roomId: Long
    ): RetrofitResult<ReadChattingModel> {
        return apiHandler(
            {api.readChatMessage(roomId)},
            {dto -> dto.toModel()}
        )
    }
}