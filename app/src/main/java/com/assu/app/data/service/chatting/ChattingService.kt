package com.assu.app.data.service.chatting

import com.assu.app.data.dto.BaseResponse
import com.assu.app.data.dto.chatting.request.BlockRequestDto
import com.assu.app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.assu.app.data.dto.chatting.response.BlockResponseDto
import com.assu.app.data.dto.chatting.response.CheckBlockResponseDto
import com.assu.app.data.dto.chatting.response.CreateChatRoomResponseDto
import com.assu.app.data.dto.chatting.response.GetBlockListResponseDto
import com.assu.app.data.dto.chatting.response.GetChatHistoryResponseDto
import com.assu.app.data.dto.chatting.response.GetChattingRoomListResponseDto
import com.assu.app.data.dto.chatting.response.LeaveChattingRoomResponseDto
import com.assu.app.data.dto.chatting.response.ReadChattingResponseDto
import com.assu.app.data.dto.chatting.response.UnblockResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChattingService {
    // 채팅방 생성 api
    @POST("chat/rooms")
    suspend fun createChatRoom(
        @Body request: CreateChatRoomRequestDto
    ): BaseResponse<CreateChatRoomResponseDto>

    // 채팅방 목록 조회 api
    @GET("chat/rooms")
    suspend fun getChattingRoomList(
    ): BaseResponse<List<GetChattingRoomListResponseDto>>

    // 채팅방 상세 조회 api
    @GET("chat/rooms/{roomId}/messages")
    suspend fun getChatHistory(
        @Path("roomId") roomId: Long
    ): BaseResponse<GetChatHistoryResponseDto>

    // 메시지 읽음 처리 api
    @PATCH("chat/rooms/{roomId}/read")
    suspend fun readChatMessage(
        @Path("roomId") roomId: Long
    ): BaseResponse<ReadChattingResponseDto>

    // 채팅방 나가기 api
    @DELETE("chat/rooms/{roomId}/leave")
    suspend fun leaveChatRoom(
        @Path("roomId") roomId: Long
    ): BaseResponse<LeaveChattingRoomResponseDto>

    @POST("chat/block")
    suspend fun blockOpponent(
        @Body request: BlockRequestDto
    ): BaseResponse<BlockResponseDto>

    @GET("chat/check/block/{opponentId}")
    suspend fun checkBlockOpponent(
        @Path("opponentId") opponentId: Long
    ): BaseResponse<CheckBlockResponseDto>

    @GET("chat/blockList")
    suspend fun getBlockList(
    ): BaseResponse<List<GetBlockListResponseDto>>

    @DELETE("chat/unblock")
    suspend fun unblockOpponent(
        @Query("opponentId") blockedId: Long,
    ): BaseResponse<UnblockResponseDto>
}