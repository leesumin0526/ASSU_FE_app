package com.example.assu_fe_app.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateMoshiAdapter : JsonAdapter<LocalDate>() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE // "yyyy-MM-dd" 형식


    @RequiresApi(Build.VERSION_CODES.O)
    override fun fromJson(reader: JsonReader): LocalDate? {
        return if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull()
        } else {
            val dateString = reader.nextString()
            LocalDate.parse(dateString, formatter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun toJson(writer: JsonWriter, value: LocalDate?) {
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value(value.format(formatter))
        }
    }
}