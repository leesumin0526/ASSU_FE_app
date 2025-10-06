package com.ssu.assu.data.dto.converter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.LocalDate

object LocalDateAdapter {
    @FromJson
    fun fromJson(date: String): LocalDate = LocalDate.parse(date)

    @ToJson
    fun toJson(value: LocalDate): String = value.toString()
}