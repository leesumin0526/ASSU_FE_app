package com.assu.app.data.repository.chatting

import com.assu.app.data.dto.chatting.request.BlockRequestDto
import com.assu.app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.assu.app.domain.model.chatting.BlockOpponentModel
import com.assu.app.domain.model.chatting.CheckBlockModel
import com.assu.app.domain.model.chatting.CreateChatRoomModel
import com.assu.app.domain.model.chatting.GetBlockListModel
import com.assu.app.domain.model.chatting.GetChatHistoryModel
import com.assu.app.domain.model.chatting.GetChattingRoomListModel
import com.assu.app.domain.model.chatting.LeaveChattingRoomModel
import com.assu.app.domain.model.chatting.ReadChattingModel
import com.assu.app.domain.model.chatting.UnblockOpponentModel
import com.assu.app.util.RetrofitResult

interface ChattingRepository {
    suspend fun createChatRoom(request: CreateChatRoomRequestDto): RetrofitResult<CreateChatRoomModel>
    suspend fun getChattingRoomList(): RetrofitResult<List<GetChattingRoomListModel>>
    suspend fun getChatHistory(roomId: Long): RetrofitResult<GetChatHistoryModel>
    suspend fun leaveChattingRoom(roomId: Long): RetrofitResult<LeaveChattingRoomModel>
    suspend fun readChatting(roomId: Long): RetrofitResult<ReadChattingModel>
    suspend fun blockOpponent(request: BlockRequestDto): RetrofitResult<BlockOpponentModel>
    suspend fun checkBlockOpponent(opponentId: Long): RetrofitResult<CheckBlockModel>
    suspend fun getBlockList(): RetrofitResult<List<GetBlockListModel>>
    suspend fun unblockOpponent(blockedId: Long): RetrofitResult<UnblockOpponentModel>
}