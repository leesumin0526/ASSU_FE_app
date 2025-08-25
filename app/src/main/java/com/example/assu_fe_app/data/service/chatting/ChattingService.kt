package com.example.assu_fe_app.data.service.chatting

import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.dto.chatting.response.CreateChatRoomResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChattingService {
    // 채팅방 생성 api
    @POST("chat/create/rooms")
    suspend fun createChatRoom(
        @Body request: CreateChatRoomRequestDto
    ): CreateChatRoomResponseDto

    // 메시지 읽음 처리 api
//    @PATCH("chat/rooms/{roomId}/read")
//    suspend fun readChatMessage(
//        @Path("roomId") roomId: Long
//    ): ReadChatMessageResponseDto

    // 채팅방 목록 조회 api
//    @GET("chat/rooms")
//    suspend fun getChatRoomList(
//        @Header("Authorization") token: String
//    ): List<ChatRoomListResponseDto>

    // 채팅방 상세 조회 api
//    @GET("chat/rooms/{roomId}/messages")
//    suspend fun getChatDetails(
//        @Path("roomId") roomId: Long
//    ): List<ChatDetailsResponseDto>

    // 채팅방 나가기 api
//    @DELETE("chat/rooms/{roomId}/leave")
//    suspend fun leaveChatRoom(
//        @Path("roomId") roomId: Long
//    ): LeaveChatRoomResponseDto
}