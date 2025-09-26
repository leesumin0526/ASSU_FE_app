package com.example.assu_fe_app.ui.partnership

data class ChattingBoxUiState(
    val isVisible: Boolean = false,
    val boxType: BoxType = BoxType.NONE,
    val title: String = "",
    val subtitle: String = "",
    val buttonText: String = ""
)

enum class BoxType { NONE, ADMIN, PARTNER }
