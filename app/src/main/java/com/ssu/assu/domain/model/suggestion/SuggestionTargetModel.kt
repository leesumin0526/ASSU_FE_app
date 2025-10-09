package com.ssu.assu.domain.model.suggestion

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SuggestionTargetModel(
    val id: Long,
    val name: String
) : Parcelable
