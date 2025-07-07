sealed class ChattingMessageItem {
    data class MyMessage(
        val message: String,
        val sentAt: String,
        val isRead: Boolean
    ) : ChattingMessageItem()

    data class OtherMessage(
        val profileImageUrl: String,
        val message: String,
        val sentAt: String
    ) : ChattingMessageItem()
}