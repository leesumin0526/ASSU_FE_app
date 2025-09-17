package com.example.assu_fe_app.presentation.common.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.auth.AdminSignUpRequestDto
import com.example.assu_fe_app.data.dto.auth.CommonAuthDto
import com.example.assu_fe_app.data.dto.auth.CommonInfoDto
import com.example.assu_fe_app.data.dto.auth.PartnerSignUpRequestDto
import com.example.assu_fe_app.data.dto.auth.SelectedPlaceDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenAuthPayloadDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenSignUpRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenVerifyRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenVerifyResponseDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.domain.usecase.auth.AdminSignUpUseCase
import com.example.assu_fe_app.domain.usecase.auth.PartnerSignUpUseCase
import com.example.assu_fe_app.domain.usecase.auth.StudentSignUpUseCase
import com.example.assu_fe_app.domain.usecase.auth.StudentTokenVerifyUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val studentSignUpUseCase: StudentSignUpUseCase,
    private val studentTokenVerifyUseCase: StudentTokenVerifyUseCase,
    private val adminSignUpUseCase: AdminSignUpUseCase,
    private val partnerSignUpUseCase: PartnerSignUpUseCase
) : ViewModel() {

    // 회원가입 데이터 상태 관리
    private val _signUpData = MutableStateFlow(SignUpData())
    val signUpData: StateFlow<SignUpData> = _signUpData.asStateFlow()

    // API 호출 상태 관리
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 학생 토큰 검증 결과
    private val _studentVerifyResult = MutableStateFlow<StudentTokenVerifyResponseDto?>(null)
    val studentVerifyResult: StateFlow<StudentTokenVerifyResponseDto?> = _studentVerifyResult.asStateFlow()

    // 회원가입 결과
    private val _signUpResult = MutableStateFlow<LoginModel?>(null)
    val signUpResult: StateFlow<LoginModel?> = _signUpResult.asStateFlow()

    // 전화번호 저장
    fun setPhoneNumber(phoneNumber: String) {
        _signUpData.value = _signUpData.value.copy(phoneNumber = phoneNumber)
    }

    // 사용자 타입 저장
    fun setUserType(userType: String) {
        _signUpData.value = _signUpData.value.copy(userType = userType)
    }

    // 대학교 저장
    fun setUniversity(university: String) {
        _signUpData.value = _signUpData.value.copy(university = university)
    }

    // 학생 토큰 정보 저장
    fun setStudentToken(sToken: String, sIdno: String) {
        _signUpData.value = _signUpData.value.copy(
            sToken = sToken,
            sIdno = sIdno
        )
    }

    // 마케팅 동의 설정
    fun setMarketingAgree(agree: Boolean) {
        _signUpData.value = _signUpData.value.copy(marketingAgree = agree)
    }

    // 위치 정보 동의 설정
    fun setLocationAgree(agree: Boolean) {
        _signUpData.value = _signUpData.value.copy(locationAgree = agree)
    }

    // 개인정보 처리방침 동의 설정
    fun setPrivacyAgree(agree: Boolean) {
        _signUpData.value = _signUpData.value.copy(privacyAgree = agree)
    }

    // 서비스 이용약관 동의 설정
    fun setTermsAgree(agree: Boolean) {
        _signUpData.value = _signUpData.value.copy(termsAgree = agree)
    }

    // 관리자 회원가입 관련 메서드들
    fun setEmail(email: String) {
        _signUpData.value = _signUpData.value.copy(email = email)
    }

    fun setPassword(password: String) {
        _signUpData.value = _signUpData.value.copy(password = password)
    }

    fun setDepartment(department: String) {
        _signUpData.value = _signUpData.value.copy(department = department)
    }

    fun setMajor(major: String) {
        _signUpData.value = _signUpData.value.copy(major = major)
    }

    fun setAdminName(name: String) {
        _signUpData.value = _signUpData.value.copy(adminName = name)
    }

    fun setDetailAddress(address: String) {
        _signUpData.value = _signUpData.value.copy(detailAddress = address)
    }

    fun setSelectedPlace(place: SelectedPlaceDto) {
        _signUpData.value = _signUpData.value.copy(selectedPlace = place)
    }

    fun setSignImageFile(file: File) {
        _signUpData.value = _signUpData.value.copy(signImageFile = file)
    }

    // 제휴업체 회원가입 관련 메서드들
    fun setCompanyName(name: String) {
        _signUpData.value = _signUpData.value.copy(companyName = name)
    }

    fun setBusinessNumber(number: String) {
        _signUpData.value = _signUpData.value.copy(businessNumber = number)
    }

    fun setRepresentativeName(name: String) {
        _signUpData.value = _signUpData.value.copy(representativeName = name)
    }

    fun setLicenseImageFile(file: File) {
        _signUpData.value = _signUpData.value.copy(licenseImageFile = file)
    }

    // 학생 토큰 검증
    fun verifyStudentToken() {
        val data = _signUpData.value
        if (data.sToken.isNullOrEmpty() || data.sIdno.isNullOrEmpty()) {
            _errorMessage.value = "학생 토큰 정보가 없습니다."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val request = StudentTokenVerifyRequestDto(
                sToken = data.sToken!!,
                sIdno = data.sIdno!!
            )

            when (val result = studentTokenVerifyUseCase(request)) {
                is RetrofitResult.Success -> {
                    _studentVerifyResult.value = result.data
                }
                is RetrofitResult.Fail -> {
                    _errorMessage.value = result.message
                }
                is RetrofitResult.Error -> {
                    _errorMessage.value = result.exception.message ?: "네트워크 오류가 발생했습니다."
                }
            }
            _isLoading.value = false
        }
    }

    // 회원가입 실행
    fun signUp() {
        val data = _signUpData.value
        val userType = data.userType

        when (userType) {
            "user" -> signUpStudent()
            "admin" -> signUpAdmin()
            "partner" -> signUpPartner()
            else -> {
                _errorMessage.value = "지원하지 않는 사용자 타입입니다."
            }
        }
    }

    // 학생 회원가입
    private fun signUpStudent() {
        val data = _signUpData.value
        if (!isStudentSignUpDataValid(data)) {
            _errorMessage.value = "필수 정보가 누락되었습니다."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val request = StudentTokenSignUpRequestDto(
                phoneNumber = data.phoneNumber!!,
                marketingAgree = data.marketingAgree,
                locationAgree = data.locationAgree,
                studentTokenAuth = StudentTokenAuthPayloadDto(
                    sToken = data.sToken!!,
                    sIdno = data.sIdno!!,
                    university = data.university ?: "SSU"
                )
            )

            when (val result = studentSignUpUseCase(request)) {
                is RetrofitResult.Success -> {
                    _signUpResult.value = result.data
                }
                is RetrofitResult.Fail -> {
                    _errorMessage.value = result.message
                }
                is RetrofitResult.Error -> {
                    _errorMessage.value = result.exception.message ?: "네트워크 오류가 발생했습니다."
                }
            }
            _isLoading.value = false
        }
    }

    // 관리자 회원가입
    private fun signUpAdmin() {
        val data = _signUpData.value
        if (!isAdminSignUpDataValid(data)) {
            _errorMessage.value = "필수 정보가 누락되었습니다."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val request = AdminSignUpRequestDto(
                phoneNumber = data.phoneNumber!!,
                marketingAgree = data.marketingAgree,
                locationAgree = data.locationAgree,
                commonAuth = CommonAuthDto(
                    email = data.email!!,
                    password = data.password!!,
                    department = data.department!!,
                    major = data.major!!,
                    university = data.university!!
                ),
                commonInfo = CommonInfoDto(
                    name = data.adminName!!,
                    detailAddress = data.detailAddress!!,
                    selectedPlace = data.selectedPlace!!
                )
            )

            // 이미지 파일을 MultipartBody.Part로 변환
            val signImageFile = data.signImageFile!!
            val requestBody = signImageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val signImagePart = MultipartBody.Part.createFormData("signImage", signImageFile.name, requestBody)

            when (val result = adminSignUpUseCase(request, signImagePart)) {
                is RetrofitResult.Success -> {
                    _signUpResult.value = result.data
                }
                is RetrofitResult.Fail -> {
                    _errorMessage.value = result.message
                }
                is RetrofitResult.Error -> {
                    _errorMessage.value = result.exception.message ?: "네트워크 오류가 발생했습니다."
                }
            }
            _isLoading.value = false
        }
    }

    // 제휴업체 회원가입
    private fun signUpPartner() {
        val data = _signUpData.value
        if (!isPartnerSignUpDataValid(data)) {
            _errorMessage.value = "필수 정보가 누락되었습니다."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val request = PartnerSignUpRequestDto(
                phoneNumber = data.phoneNumber!!,
                marketingAgree = data.marketingAgree,
                locationAgree = data.locationAgree,
                commonAuth = CommonAuthDto(
                    email = data.email!!,
                    password = data.password!!,
                    department = data.department!!,
                    major = data.major!!,
                    university = data.university!!
                ),
                commonInfo = CommonInfoDto(
                    name = data.companyName!!,
                    detailAddress = data.detailAddress!!,
                    selectedPlace = data.selectedPlace!!
                )
            )

            // 이미지 파일을 MultipartBody.Part로 변환
            val licenseImageFile = data.licenseImageFile!!
            val requestBody = licenseImageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val licenseImagePart = MultipartBody.Part.createFormData("licenseImage", licenseImageFile.name, requestBody)

            when (val result = partnerSignUpUseCase(request, licenseImagePart)) {
                is RetrofitResult.Success -> {
                    _signUpResult.value = result.data
                }
                is RetrofitResult.Fail -> {
                    _errorMessage.value = result.message
                }
                is RetrofitResult.Error -> {
                    _errorMessage.value = result.exception.message ?: "네트워크 오류가 발생했습니다."
                }
            }
            _isLoading.value = false
        }
    }

    // 학생 회원가입 데이터 유효성 검사
    private fun isStudentSignUpDataValid(data: SignUpData): Boolean {
        return !data.phoneNumber.isNullOrEmpty() &&
                !data.sToken.isNullOrEmpty() &&
                !data.sIdno.isNullOrEmpty() &&
                data.userType == "user"
    }

    // 관리자 회원가입 데이터 유효성 검사
    private fun isAdminSignUpDataValid(data: SignUpData): Boolean {
        return !data.phoneNumber.isNullOrEmpty() &&
                !data.email.isNullOrEmpty() &&
                !data.password.isNullOrEmpty() &&
                !data.department.isNullOrEmpty() &&
                !data.major.isNullOrEmpty() &&
                !data.university.isNullOrEmpty() &&
                !data.adminName.isNullOrEmpty() &&
                !data.detailAddress.isNullOrEmpty() &&
                data.selectedPlace != null &&
                data.signImageFile != null &&
                data.userType == "admin"
    }

    // 제휴업체 회원가입 데이터 유효성 검사
    private fun isPartnerSignUpDataValid(data: SignUpData): Boolean {
        return !data.phoneNumber.isNullOrEmpty() &&
                !data.email.isNullOrEmpty() &&
                !data.password.isNullOrEmpty() &&
                !data.department.isNullOrEmpty() &&
                !data.major.isNullOrEmpty() &&
                !data.university.isNullOrEmpty() &&
                !data.companyName.isNullOrEmpty() &&
                !data.businessNumber.isNullOrEmpty() &&
                !data.representativeName.isNullOrEmpty() &&
                !data.detailAddress.isNullOrEmpty() &&
                data.selectedPlace != null &&
                data.licenseImageFile != null &&
                data.userType == "partner"
    }

    // 에러 메시지 초기화
    fun clearError() {
        _errorMessage.value = null
    }
}

// 회원가입 데이터 클래스
data class SignUpData(
    val phoneNumber: String? = null,
    val userType: String? = null, // "user", "admin", "partner"
    val university: String? = null,
    val sToken: String? = null,
    val sIdno: String? = null,
    val marketingAgree: Boolean = false,
    val locationAgree: Boolean = false,
    val privacyAgree: Boolean = false,
    val termsAgree: Boolean = false,
    // 관리자 회원가입 관련 필드들
    val email: String? = null,
    val password: String? = null,
    val department: String? = null,
    val major: String? = null,
    val adminName: String? = null,
    val detailAddress: String? = null,
    val selectedPlace: SelectedPlaceDto? = null,
    val signImageFile: File? = null,
    // 제휴업체 회원가입 관련 필드들
    val companyName: String? = null,
    val businessNumber: String? = null,
    val representativeName: String? = null,
    val licenseImageFile: File? = null
)
