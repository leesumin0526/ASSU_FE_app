package com.example.assu_fe_app.data.dto.usage

import java.time.LocalDateTime

data class ServiceRecord(
    var id: Long,
    var storeId: Long,
    var partnerId: Long,
    var marketName: String,
    var adminName:String,
    var serviceContent: String,
    var dateTime : String,
    var isReviewd : Boolean
)