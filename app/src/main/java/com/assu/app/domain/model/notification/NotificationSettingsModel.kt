package com.assu.app.domain.model.notification

data class NotificationSettingsModel(
    val chat: Boolean,
    val order: Boolean,
    val suggestion: Boolean,
    val proposal: Boolean
) {
    companion object {
        fun from(map: Map<String, Boolean>) = NotificationSettingsModel(
            chat       = map["CHAT"] ?: true,
            order      = map["ORDER"] ?: true,
            suggestion = map["PARTNER_SUGGESTION"] ?: true,
            proposal   = map["PARTNER_PROPOSAL"] ?: true
        )
    }
}