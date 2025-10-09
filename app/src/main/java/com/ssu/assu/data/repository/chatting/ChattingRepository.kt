package com.ssu.assu.data.repository.chatting

import com.ssu.assu.data.dto.chatting.request.BlockRequestDto
import com.ssu.assu.data.dto.chatting.request.CreateChatRoomRequestDto
import com.ssu.assu.domain.model.chatting.BlockOpponentModel
import com.ssu.assu.domain.model.chatting.CheckBlockModel
import com.ssu.assu.domain.model.chatting.CreateChatRoomModel
import com.ssu.assu.domain.model.chatting.GetBlockListModel
import com.ssu.assu.domain.model.chatting.GetChatHistoryModel
import com.ssu.assu.domain.model.chatting.GetChattingRoomListModel
import com.ssu.assu.domain.model.chatting.LeaveChattingRoomModel
import com.ssu.assu.domain.model.chatting.ReadChattingModel
import com.ssu.assu.domain.model.chatting.UnblockOpponentModel
import com.ssu.assu.util.RetrofitResult

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