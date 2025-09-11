package com.example.assu_fe_app.domain.model.notification

enum class NotificationTypeModel(val path: String) {
    CHAT("CHAT"),
    ORDER("ORDER"),
    PARTNER_SUGGESTION("PARTNER_SUGGESTION"),
    PARTNER_PROPOSAL("PARTNER_PROPOSAL"),
    PARTNER_ALL("PARTNER_ALL"),
    ADMIN_ALL("ADMIN_ALL")
}
