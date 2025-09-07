package com.example.assu_fe_app.data.dto.chatting.response

import com.example.assu_fe_app.domain.model.chatting.GetChattingRoomListModel
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class GetChattingRoomListResponseDto(
    val roomId: Long,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadMessageCount: Long,
    val opponentId: Long,
    val opponentName: String,
    val opponentProfileImage: String
) {
    fun toModel() = GetChattingRoomListModel(
        roomId = this.roomId,
        lastMessage = this.lastMessage,
        lastMessageTime = lastMessageTime,
        unreadMessageCount = this.unreadMessageCount,
        opponentId = this.opponentId,
        opponentName = this.opponentName,
        opponentProfileImage = this.opponentProfileImage
    )
}
