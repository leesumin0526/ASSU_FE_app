package com.ssu.assu.data.dto.chatting

sealed class ChattingMessageItem {
    data class MyMessage(
        val messageId: Long,
        val message: String,
        val sentAt: String,
        val isRead: Boolean,
        val unreadCountForSender: Int = 0
    ) : ChattingMessageItem()

    data class OtherMessage(
        val messageId: Long,
        val profileImageUrl: String,
        val message: String,
        val sentAt: String,
        val isRead: Boolean,
    ) : ChattingMessageItem()

    data class DateSeparatorItem(
        val date: String
    ) : ChattingMessageItem()

    data class GuideMessageItem(
        val messageId: Long,
        val guideMessage: String,
        val sentAt: String,
    ): ChattingMessageItem()
}