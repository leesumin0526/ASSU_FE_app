package com.assu.app.data.dto.chatting.response

import com.assu.app.domain.model.chatting.GetChattingRoomListModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChattingRoomListResponseDto(
    val roomId: Long,
    val lastMessage: String? = null,
    val lastMessageTime: String? = null,
    val unreadMessagesCount: Long ?= 0,
    val opponentId: Long,
    val opponentName: String,
    val opponentProfileImage: String? = null,
    val phoneNumber: String?=null,
) {
    fun toModel() = GetChattingRoomListModel(
        roomId = this.roomId,
        lastMessage = this.lastMessage ?: "",
        lastMessageTime = lastMessageTime ?: "",
        unreadMessagesCount = this.unreadMessagesCount ?: 0,
        opponentId = this.opponentId,
        opponentName = this.opponentName,
        opponentProfileImage = this.opponentProfileImage ?: "",
        phoneNumber = this.phoneNumber ?:""
    )
}
