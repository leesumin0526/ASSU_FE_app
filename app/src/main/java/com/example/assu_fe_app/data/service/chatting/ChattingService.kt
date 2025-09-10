package com.example.assu_fe_app.data.service.chatting

import com.example.assu_fe_app.data.dto.BaseResponse
import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.dto.chatting.response.CreateChatRoomResponseDto
import com.example.assu_fe_app.data.dto.chatting.response.GetChatHistoryResponseDto
import com.example.assu_fe_app.data.dto.chatting.response.GetChattingRoomListResponseDto
import com.example.assu_fe_app.data.dto.chatting.response.LeaveChattingRoomResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

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
//    @PATCH("chat/rooms/{roomId}/read")
//    suspend fun readChatMessage(
//        @Path("roomId") roomId: Long
//    ): BaseResponse<Read>

    // 채팅방 나가기 api
    @DELETE("chat/rooms/{roomId}/leave")
    suspend fun leaveChatRoom(
        @Path("roomId") roomId: Long
    ): BaseResponse<LeaveChattingRoomResponseDto>
}