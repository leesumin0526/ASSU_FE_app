package com.assu.app.domain.model.auth

import com.assu.app.data.dto.auth.SelectedPlaceDto
import java.io.File

// 회원가입 데이터 클래스
data class SignUpData(
    val phoneNumber: String? = null,
    val userType: String? = null, // "user", "admin", "partner"
    val university: String? = null,
    val sToken: String? = null,
    val sIdno: String? = null,
    val marketingAgree: Boolean = false,
    val locationAgree: Boolean = false,
    // 관리자 회원가입 관련 필드들
    val email: String? = null,
    val password: String? = null,
    val department: String? = null,
    val major: String? = null,
    val detailAddress: String? = null,
    val selectedPlace: SelectedPlaceDto? = null,
    val signImageFile: File? = null,
    // 제휴업체 회원가입 관련 필드들
    val companyName: String? = null,
    val businessNumber: String? = null,
    val representativeName: String? = null,
    val licenseImageFile: File? = null
)
