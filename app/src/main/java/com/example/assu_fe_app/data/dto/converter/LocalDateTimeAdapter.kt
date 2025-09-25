package com.example.assu_fe_app.data.dto.converter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

object LocalDateTimeAdapter {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    @FromJson
    fun fromJson(dateTime: String): LocalDateTime = LocalDateTime.parse(dateTime, formatter)

    @ToJson
    fun toJson(value: LocalDateTime): String = value.format(formatter)
}