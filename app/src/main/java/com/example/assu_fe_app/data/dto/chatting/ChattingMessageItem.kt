sealed class ChattingMessageItem {
    data class MyMessage(
        val messageId: Long,
        val message: String,
        val sentAt: String,
        val isRead: Boolean
    ) : ChattingMessageItem()

    data class OtherMessage(
        val messageId: Long,
        val profileImageUrl: String,
        val message: String,
        val sentAt: String,
        val isRead: Boolean
    ) : ChattingMessageItem()
}