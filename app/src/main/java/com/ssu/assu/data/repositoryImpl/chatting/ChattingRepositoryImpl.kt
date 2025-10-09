package com.ssu.assu.data.repositoryImpl.chatting

import com.ssu.assu.data.dto.chatting.request.BlockRequestDto
import com.ssu.assu.data.dto.chatting.request.CreateChatRoomRequestDto
import com.ssu.assu.data.repository.chatting.ChattingRepository
import com.ssu.assu.data.service.chatting.ChattingService
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
import com.ssu.assu.util.apiHandler
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

    override suspend fun blockOpponent(
        request: BlockRequestDto
    ): RetrofitResult<BlockOpponentModel> {
        return apiHandler(
            {api.blockOpponent(request)},
            {dto -> dto.toModel()}
        )
    }

    override suspend fun checkBlockOpponent(
        opponentId: Long
    ): RetrofitResult<CheckBlockModel> {
        return apiHandler(
        {api.checkBlockOpponent(opponentId)},
        {dto -> dto.toModel()}
        )
    }

    override suspend fun getBlockList(
    ): RetrofitResult<List<GetBlockListModel>> {
        return apiHandler(
            {api.getBlockList()},
            {dtoList -> dtoList.map { it.toModel() }}
        )
    }

    override suspend fun unblockOpponent(
        blockedId: Long,
    ): RetrofitResult<UnblockOpponentModel> {
        return apiHandler(
            {api.unblockOpponent(blockedId)},
            {dto -> dto.toModel()}
        )
    }
}