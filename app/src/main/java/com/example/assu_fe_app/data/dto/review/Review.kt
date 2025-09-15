package com.example.assu_fe_app.data.dto.review

import java.time.LocalDateTime

data class Review(
    var id: Long,
    var marketName : String, // 후에 it대학 재학생 등으로 표기 가능함
    var rate : Int,
    var content : String,
    var reviewImage : List<String>,
    var date: LocalDateTime
)