package com.example.assu_fe_app.domain.model.suggestion

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SuggestionTargetModel(
    val id: Long,
    val name: String
) : Parcelable
