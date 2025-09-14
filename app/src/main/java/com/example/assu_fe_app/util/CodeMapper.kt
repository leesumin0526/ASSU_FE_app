package com.example.assu_fe_app.util

fun String.toDepartmentName(): String {
    return when (this) {
        "COM" -> "컴퓨터학부"
        "SW" -> "소프트웨어학부"
        "GM" -> "글로벌미디어학부"
        "EE" -> "전자정보공학부"
        "IP" -> "정보보호학과"
        "AI" -> "AI융합학부"
        "MB" -> "미디어경영학과"
        else -> this
    }
}

fun String.toEnrollmentStatus(): String {
    return when (this) {
        "ENROLLED" -> "재학생"
        "LEAVE" -> "휴학생"
        "GRADUATED" -> "졸업생"
        else -> this
    }
}