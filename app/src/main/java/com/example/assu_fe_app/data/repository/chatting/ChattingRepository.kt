package com.example.assu_fe_app.data.repository.chatting

import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.domain.model.chatting.CreateChatRoomModel
import com.example.assu_fe_app.domain.model.chatting.GetChatHistoryModel
import com.example.assu_fe_app.domain.model.chatting.GetChattingRoomListModel
import com.example.assu_fe_app.domain.model.chatting.LeaveChattingRoomModel
import com.example.assu_fe_app.domain.model.chatting.ReadChattingModel
import com.example.assu_fe_app.util.RetrofitResult

interface ChattingRepository {
    suspend fun createChatRoom(request: CreateChatRoomRequestDto): RetrofitResult<CreateChatRoomModel>
    suspend fun getChattingRoomList(): RetrofitResult<List<GetChattingRoomListModel>>
    suspend fun getChatHistory(roomId: Long): RetrofitResult<GetChatHistoryModel>
    suspend fun leaveChattingRoom(roomId: Long): RetrofitResult<LeaveChattingRoomModel>
    suspend fun readChatting(roomId: Long): RetrofitResult<ReadChattingModel>
}