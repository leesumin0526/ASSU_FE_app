package com.example.assu_fe_app.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.example.assu_fe_app.data.dto.auth.AdminSignUpRequestDto
import com.example.assu_fe_app.data.dto.auth.CommonAuthDto
import com.example.assu_fe_app.data.dto.auth.CommonInfoDto
import com.example.assu_fe_app.data.dto.auth.PartnerCommonAuthDto
import com.example.assu_fe_app.data.dto.auth.PartnerSignUpRequestDto
import com.example.assu_fe_app.data.dto.auth.SelectedPlaceDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenAuthPayloadDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenSignUpRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenVerifyRequestDto
import com.example.assu_fe_app.data.dto.auth.StudentTokenVerifyResponseDto
import com.example.assu_fe_app.data.dto.auth.EmailVerificationRequestDto
import com.example.assu_fe_app.data.repository.auth.AuthRepository
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.domain.model.auth.LoginModel
import com.example.assu_fe_app.domain.model.auth.SignUpData
import com.example.assu_fe_app.domain.model.enums.Department
import com.example.assu_fe_app.domain.model.enums.Major
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val studentSignUpUseCase: StudentSignUpUseCase,
    private val studentTokenVerifyUseCase: StudentTokenVerifyUseCase,
    private val adminSignUpUseCase: AdminSignUpUseCase,
    private val partnerSignUpUseCase: PartnerSignUpUseCase,
    private val authTokenLocalStore: AuthTokenLocalStore,
    private val authRepository: AuthRepository
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

    // 이메일 중복 검증 상태
    private val _isEmailVerifying = MutableStateFlow(false)
    val isEmailVerifying: StateFlow<Boolean> = _isEmailVerifying.asStateFlow()

    private val _emailVerificationResult = MutableStateFlow<Boolean?>(null)
    val emailVerificationResult: StateFlow<Boolean?> = _emailVerificationResult.asStateFlow()

    private val _emailVerificationMessage = MutableStateFlow<String?>(null)
    val emailVerificationMessage: StateFlow<String?> = _emailVerificationMessage.asStateFlow()

    // 이메일 검증 상태 초기화
    fun resetEmailVerification() {
        _emailVerificationResult.value = null
        _emailVerificationMessage.value = null
        _isEmailVerifying.value = false
    }

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

    // 개인정보 처리방침 동의 설정
    fun setPrivacyAgree(agree: Boolean) {
        _signUpData.value = _signUpData.value.copy(privacyAgree = agree)
    }

    // 마케팅 동의 설정
    fun setMarketingAgree(agree: Boolean) {
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

    // 이메일 중복 검증
    fun checkEmailVerification(email: String) {
        if (email.isBlank()) {
            _emailVerificationResult.value = false
            _emailVerificationMessage.value = "이메일을 입력해주세요."
            return
        }

        // 이메일 형식 검증
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        if (!emailPattern.matcher(email).matches()) {
            _emailVerificationResult.value = false
            _emailVerificationMessage.value = "올바른 이메일 형식이 아닙니다."
            return
        }

        viewModelScope.launch {
            _isEmailVerifying.value = true
            _emailVerificationMessage.value = null

            val request = EmailVerificationRequestDto(email = email)

            when (val result = authRepository.checkEmailVerification(request)) {
                is RetrofitResult.Success -> {
                    _emailVerificationResult.value = true
                    _emailVerificationMessage.value = "사용 가능한 이메일입니다."
                }
                is RetrofitResult.Fail -> {
                    _emailVerificationResult.value = false
                    // 서버에서 받은 구체적인 오류 메시지 처리
                    val errorMessage = try {
                        when (result.statusCode) {
                            400 -> {
                                // 400 Bad Request - 이메일 형식 오류
                                // JSON 응답에서 구체적인 오류 메시지 추출
                                val gson = Gson()
                                val jsonObject = gson.fromJson(result.message, JsonObject::class.java)
                                val resultObject = jsonObject.getAsJsonObject("result")
                                val emailError = resultObject?.get("email")?.asString
                                emailError ?: "올바른 이메일 형식을 입력해주세요."
                            }
                            404 -> {
                                // 404 Not Found - 이미 존재하는 이메일
                                "이미 사용 중인 이메일입니다."
                            }
                            409 -> {
                                // 409 Conflict - 이미 가입된 이메일
                                "이미 가입된 이메일입니다."
                            }
                            else -> {
                                // 기타 오류 - 서버 메시지 사용
                                result.message ?: "이메일 검증에 실패했습니다."
                            }
                        }
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 기본 메시지 사용
                        when (result.statusCode) {
                            400 -> "올바른 이메일 형식을 입력해주세요."
                            404 -> "이미 사용 중인 이메일입니다."
                            409 -> "이미 가입된 이메일입니다."
                            else -> result.message ?: "이메일 검증에 실패했습니다."
                        }
                    }
                    _emailVerificationMessage.value = errorMessage
                }
                is RetrofitResult.Error -> {
                    _emailVerificationResult.value = false
                    _emailVerificationMessage.value = "네트워크 오류가 발생했습니다. 다시 시도해주세요."
                }
            }
            _isEmailVerifying.value = false
        }
    }

    // 관리자 이름 자동 생성 (가장 하위 단위 + "학생회")
    private fun generateAdminName(university: String?, department: String?, major: String?): String {
        return when {
            !major.isNullOrEmpty() -> {
                // Major enum에서 displayName 찾기
                val majorEnum = Major.values().find { it.name == major }
                (majorEnum?.displayName ?: major) + " 학생회"
            }
            !department.isNullOrEmpty() -> {
                // Department enum에서 displayName 찾기
                val departmentEnum = Department.values().find { it.name == department }
                (departmentEnum?.displayName ?: department) + " 학생회"
            }
            !university.isNullOrEmpty() -> "$university 학생회"
            else -> "학생회"
        }
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
                    // 전공과 학번만 있으면 성공으로 처리
                    if (!result.data.major.isNullOrEmpty() && !result.data.studentNumber.isNullOrEmpty()) {
                        _studentVerifyResult.value = result.data
                    } else {
                        _errorMessage.value = "학생 정보를 가져올 수 없습니다."
                    }
                }
                is RetrofitResult.Fail -> {
                    // 서버 에러 메시지는 토스트로 표시하지 않음
                    // 로그만 남기고 다음 화면으로 진행하지 않음
                    Log.d("SignUpViewModel", "학생 토큰 검증 실패: ${result.message}")
                    _errorMessage.value = "학생 인증에 실패했습니다. 다시 시도해주세요."
                }
                is RetrofitResult.Error -> {
                    Log.d("SignUpViewModel", "학생 토큰 검증 에러: ${result.exception.message}")
                    _errorMessage.value = "네트워크 오류가 발생했습니다."
                }
            }
            _isLoading.value = false
        }
    }

    // 회원가입 실행
    fun signUp() {
        val data = _signUpData.value
        val userType = data.userType
        
        Log.d("SignUpViewModel", "=== 회원가입 시작 ===")
        Log.d("SignUpViewModel", "사용자 타입: $userType")
        Log.d("SignUpViewModel", "현재 로딩 상태: ${_isLoading.value}")
        Log.d("SignUpViewModel", "현재 에러 메시지: ${_errorMessage.value}")
        Log.d("SignUpViewModel", "현재 회원가입 결과: ${_signUpResult.value}")

        when (userType) {
            "user" -> {
                Log.d("SignUpViewModel", "학생 회원가입 시작")
                signUpStudent()
            }
            "admin" -> {
                Log.d("SignUpViewModel", "관리자 회원가입 시작")
                signUpAdmin()
            }
            "partner" -> {
                Log.d("SignUpViewModel", "제휴업체 회원가입 시작")
                signUpPartner()
            }
            else -> {
                Log.e("SignUpViewModel", "잘못된 사용자 타입: $userType")
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

            // API 호출 직전 전달되는 정보 로그 출력
            Log.d("SignUpViewModel", "=== 학생 회원가입 API 호출 - 전달되는 정보 ===")
            Log.d("SignUpViewModel", "Phone Number: ${request.phoneNumber}")
            Log.d("SignUpViewModel", "Marketing Agree: ${request.marketingAgree}")
            Log.d("SignUpViewModel", "Location Agree: ${request.locationAgree}")
            Log.d("SignUpViewModel", "Student Token: ${request.studentTokenAuth.sToken}")
            Log.d("SignUpViewModel", "Student ID: ${request.studentTokenAuth.sIdno}")
            Log.d("SignUpViewModel", "University: ${request.studentTokenAuth.university}")
            Log.d("SignUpViewModel", "==========================================")

            when (val result = studentSignUpUseCase(request)) {
                is RetrofitResult.Success -> {
                    Log.d("SignUpViewModel", "=== 학생 회원가입 API 성공 ===")
                    Log.d("SignUpViewModel", "받은 데이터: ${result.data}")
                    
                    // 토큰 저장
                    authTokenLocalStore.saveLoginData(result.data)
                    
                    // 저장된 정보 로그 출력
                    val savedLoginModel = authTokenLocalStore.getLoginModel()
                    Log.d("SignUpViewModel", "=== 학생 회원가입 성공 - 저장된 정보 ===")
                    Log.d("SignUpViewModel", "Access Token: ${savedLoginModel?.accessToken?.take(20)}...")
                    Log.d("SignUpViewModel", "Refresh Token: ${savedLoginModel?.refreshToken?.take(20)}...")
                    Log.d("SignUpViewModel", "User ID: ${savedLoginModel?.userId}")
                    Log.d("SignUpViewModel", "Username: ${savedLoginModel?.username}")
                    Log.d("SignUpViewModel", "User Role: ${savedLoginModel?.userRole}")
                    Log.d("SignUpViewModel", "Email(id): ${savedLoginModel?.email}")
                    Log.d("SignUpViewModel", "Profile Image Url: ${savedLoginModel?.profileImageUrl}")
                    Log.d("SignUpViewModel", "Status: ${savedLoginModel?.status}")
                    savedLoginModel?.basicInfo?.let { basicInfo ->
                        Log.d("SignUpViewModel", "=== Basic Info ===")
                        Log.d("SignUpViewModel", "Name: ${basicInfo.name}")
                        Log.d("SignUpViewModel", "University: ${basicInfo.university}")
                        Log.d("SignUpViewModel", "Department: ${basicInfo.department}")
                        Log.d("SignUpViewModel", "Major: ${basicInfo.major}")
                    } ?: Log.d("SignUpViewModel", "Basic Info: null")
                    Log.d("SignUpViewModel", "================================")
                    
                    _signUpResult.value = result.data
                }
                is RetrofitResult.Fail -> {
                    Log.e("SignUpViewModel", "=== 학생 회원가입 API 실패 ===")
                    Log.e("SignUpViewModel", "Status Code: ${result.statusCode}")
                    Log.e("SignUpViewModel", "Message: ${result.message}")
                    _errorMessage.value = result.message
                }
                is RetrofitResult.Error -> {
                    Log.e("SignUpViewModel", "=== 학생 회원가입 API 에러 ===")
                    Log.e("SignUpViewModel", "Exception: ${result.exception}")
                    _errorMessage.value = result.exception.message ?: "네트워크 오류가 발생했습니다."
                }
            }
            _isLoading.value = false
        }
    }

    // 관리자 회원가입
    private fun signUpAdmin() {
        val data = _signUpData.value
        Log.d("SignUpViewModel", "=== 관리자 회원가입 데이터 유효성 검사 시작 ===")
        
        if (!isAdminSignUpDataValid(data)) {
            Log.e("SignUpViewModel", "관리자 회원가입 데이터 유효성 검사 실패")
            _errorMessage.value = "필수 정보가 누락되었습니다."
            return
        }
        
        Log.d("SignUpViewModel", "관리자 회원가입 데이터 유효성 검사 통과")

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
                    department = data.department?.takeIf { it.isNotEmpty() } ?: "N/A",
                    major = data.major?.takeIf { it.isNotEmpty() } ?: "N/A",
                    university = data.university!!
                ),
                commonInfo = CommonInfoDto(
                    name = generateAdminName(data.university, data.department, data.major),
                    detailAddress = data.detailAddress!!,
                    selectedPlace = data.selectedPlace!!
                )
            )

            // 이미지 파일을 MultipartBody.Part로 변환
            val signImageFile = data.signImageFile!!
            val requestBody = signImageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val signImagePart = MultipartBody.Part.createFormData("signImage", signImageFile.name, requestBody)

            // API 호출 직전 전달되는 정보 로그 출력
            Log.d("SignUpViewModel", "=== 관리자 회원가입 API 호출 - 전달되는 정보 ===")
            Log.d("SignUpViewModel", "Phone Number: ${request.phoneNumber}")
            Log.d("SignUpViewModel", "Marketing Agree: ${request.marketingAgree}")
            Log.d("SignUpViewModel", "Location Agree: ${request.locationAgree}")
            Log.d("SignUpViewModel", "Email: ${request.commonAuth.email}")
            Log.d("SignUpViewModel", "Password: ${request.commonAuth.password}")
            Log.d("SignUpViewModel", "Department: ${request.commonAuth.department}")
            Log.d("SignUpViewModel", "Major: ${request.commonAuth.major}")
            Log.d("SignUpViewModel", "University: ${request.commonAuth.university}")
            Log.d("SignUpViewModel", "Name: ${request.commonInfo.name}")
            Log.d("SignUpViewModel", "Detail Address: ${request.commonInfo.detailAddress}")
            Log.d("SignUpViewModel", "Selected Place: ${request.commonInfo.selectedPlace}")
            Log.d("SignUpViewModel", "  - Place ID: ${request.commonInfo.selectedPlace.placeId}")
            Log.d("SignUpViewModel", "  - Name: ${request.commonInfo.selectedPlace.name}")
            Log.d("SignUpViewModel", "  - Address: ${request.commonInfo.selectedPlace.address}")
            Log.d("SignUpViewModel", "  - Road Address: ${request.commonInfo.selectedPlace.roadAddress}")
            Log.d("SignUpViewModel", "  - Latitude: ${request.commonInfo.selectedPlace.latitude}")
            Log.d("SignUpViewModel", "  - Longitude: ${request.commonInfo.selectedPlace.longitude}")
            Log.d("SignUpViewModel", "Sign Image File: ${signImageFile.name}")
            
            // JSON 직렬화 테스트
            try {
                val gson = com.google.gson.Gson()
                val jsonString = gson.toJson(request)
                Log.d("SignUpViewModel", "=== 관리자 회원가입 JSON 직렬화 결과 ===")
                Log.d("SignUpViewModel", "전체 JSON:")
                Log.d("SignUpViewModel", jsonString)
                Log.d("SignUpViewModel", "JSON 길이: ${jsonString.length}")
                Log.d("SignUpViewModel", "==========================================")
            } catch (e: Exception) {
                Log.e("SignUpViewModel", "JSON 직렬화 실패: ${e.message}")
            }
            
            Log.d("SignUpViewModel", "==========================================")

            when (val result = adminSignUpUseCase(request, signImagePart)) {
                is RetrofitResult.Success -> {
                    Log.d("SignUpViewModel", "=== 관리자 회원가입 API 성공 ===")
                    Log.d("SignUpViewModel", "받은 데이터: ${result.data}")
                    
                    // 토큰 저장
                    authTokenLocalStore.saveLoginData(result.data)
                    
                    // 저장된 정보 로그 출력
                    val savedLoginModel = authTokenLocalStore.getLoginModel()
                    Log.d("SignUpViewModel", "=== 관리자 회원가입 성공 - 저장된 정보 ===")
                    Log.d("SignUpViewModel", "Access Token: ${savedLoginModel?.accessToken?.take(20)}...")
                    Log.d("SignUpViewModel", "Refresh Token: ${savedLoginModel?.refreshToken?.take(20)}...")
                    Log.d("SignUpViewModel", "User ID: ${savedLoginModel?.userId}")
                    Log.d("SignUpViewModel", "Username: ${savedLoginModel?.username}")
                    Log.d("SignUpViewModel", "User Role: ${savedLoginModel?.userRole}")
                    Log.d("SignUpViewModel", "Email(id): ${savedLoginModel?.email}")
                    Log.d("SignUpViewModel", "Profile Image Url: ${savedLoginModel?.profileImageUrl}")
                    Log.d("SignUpViewModel", "Status: ${savedLoginModel?.status}")
                    savedLoginModel?.basicInfo?.let { basicInfo ->
                        Log.d("SignUpViewModel", "=== Basic Info ===")
                        Log.d("SignUpViewModel", "Name: ${basicInfo.name}")
                        Log.d("SignUpViewModel", "University: ${basicInfo.university}")
                        Log.d("SignUpViewModel", "Department: ${basicInfo.department}")
                        Log.d("SignUpViewModel", "Major: ${basicInfo.major}")
                    } ?: Log.d("SignUpViewModel", "Basic Info: null")
                    Log.d("SignUpViewModel", "================================")
                    
                    _signUpResult.value = result.data
                }
                is RetrofitResult.Fail -> {
                    Log.e("SignUpViewModel", "=== 관리자 회원가입 API 실패 ===")
                    Log.e("SignUpViewModel", "Status Code: ${result.statusCode}")
                    Log.e("SignUpViewModel", "Message: ${result.message}")
                    _errorMessage.value = result.message
                }
                is RetrofitResult.Error -> {
                    Log.e("SignUpViewModel", "=== 관리자 회원가입 API 에러 ===")
                    Log.e("SignUpViewModel", "Exception: ${result.exception}")
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
                commonAuth = PartnerCommonAuthDto(
                    email = data.email!!,
                    password = data.password!!
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

            // API 호출 직전 전달되는 정보 로그 출력
            Log.d("SignUpViewModel", "=== 제휴업체 회원가입 API 호출 - 전달되는 정보 ===")
            Log.d("SignUpViewModel", "Phone Number: ${request.phoneNumber}")
            Log.d("SignUpViewModel", "Marketing Agree: ${request.marketingAgree}")
            Log.d("SignUpViewModel", "Location Agree: ${request.locationAgree}")
            Log.d("SignUpViewModel", "Email: ${request.commonAuth.email}")
            Log.d("SignUpViewModel", "Password: ${request.commonAuth.password}")
            Log.d("SignUpViewModel", "Company Name: ${request.commonInfo.name}")
            Log.d("SignUpViewModel", "Detail Address: ${request.commonInfo.detailAddress}")
            Log.d("SignUpViewModel", "Selected Place: ${request.commonInfo.selectedPlace}")
            Log.d("SignUpViewModel", "  - Place ID: ${request.commonInfo.selectedPlace.placeId}")
            Log.d("SignUpViewModel", "  - Name: ${request.commonInfo.selectedPlace.name}")
            Log.d("SignUpViewModel", "  - Address: ${request.commonInfo.selectedPlace.address}")
            Log.d("SignUpViewModel", "  - Road Address: ${request.commonInfo.selectedPlace.roadAddress}")
            Log.d("SignUpViewModel", "  - Latitude: ${request.commonInfo.selectedPlace.latitude}")
            Log.d("SignUpViewModel", "  - Longitude: ${request.commonInfo.selectedPlace.longitude}")
            Log.d("SignUpViewModel", "License Image File: ${licenseImageFile.name}")
            
            // JSON 직렬화 테스트
            try {
                val gson = com.google.gson.Gson()
                val jsonString = gson.toJson(request)
                Log.d("SignUpViewModel", "=== 제휴업체 회원가입 JSON 직렬화 결과 ===")
                Log.d("SignUpViewModel", "전체 JSON:")
                Log.d("SignUpViewModel", jsonString)
                Log.d("SignUpViewModel", "JSON 길이: ${jsonString.length}")
                Log.d("SignUpViewModel", "==========================================")
            } catch (e: Exception) {
                Log.e("SignUpViewModel", "JSON 직렬화 실패: ${e.message}")
            }
            
            Log.d("SignUpViewModel", "==========================================")

            when (val result = partnerSignUpUseCase(request, licenseImagePart)) {
                is RetrofitResult.Success -> {
                    Log.d("SignUpViewModel", "=== 제휴업체 회원가입 API 성공 ===")
                    Log.d("SignUpViewModel", "받은 데이터: ${result.data}")
                    
                    // 토큰 저장
                    authTokenLocalStore.saveLoginData(result.data)
                    
                    // 저장된 정보 로그 출력
                    val savedLoginModel = authTokenLocalStore.getLoginModel()
                    Log.d("SignUpViewModel", "=== 제휴업체 회원가입 성공 - 저장된 정보 ===")
                    Log.d("SignUpViewModel", "Access Token: ${savedLoginModel?.accessToken?.take(20)}...")
                    Log.d("SignUpViewModel", "Refresh Token: ${savedLoginModel?.refreshToken?.take(20)}...")
                    Log.d("SignUpViewModel", "User ID: ${savedLoginModel?.userId}")
                    Log.d("SignUpViewModel", "Username: ${savedLoginModel?.username}")
                    Log.d("SignUpViewModel", "User Role: ${savedLoginModel?.userRole}")
                    Log.d("SignUpViewModel", "Email(id): ${savedLoginModel?.email}")
                    Log.d("SignUpViewModel", "Profile Image Url: ${savedLoginModel?.profileImageUrl}")
                    Log.d("SignUpViewModel", "Status: ${savedLoginModel?.status}")
                    savedLoginModel?.basicInfo?.let { basicInfo ->
                        Log.d("SignUpViewModel", "=== Basic Info ===")
                        Log.d("SignUpViewModel", "Name: ${basicInfo.name}")
                        Log.d("SignUpViewModel", "University: ${basicInfo.university}")
                        Log.d("SignUpViewModel", "Department: ${basicInfo.department}")
                        Log.d("SignUpViewModel", "Major: ${basicInfo.major}")
                    } ?: Log.d("SignUpViewModel", "Basic Info: null")
                    Log.d("SignUpViewModel", "================================")
                    
                    _signUpResult.value = result.data
                }
                is RetrofitResult.Fail -> {
                    Log.e("SignUpViewModel", "=== 제휴업체 회원가입 API 실패 ===")
                    Log.e("SignUpViewModel", "Status Code: ${result.statusCode}")
                    Log.e("SignUpViewModel", "Message: ${result.message}")
                    _errorMessage.value = result.message
                }
                is RetrofitResult.Error -> {
                    Log.e("SignUpViewModel", "=== 제휴업체 회원가입 API 에러 ===")
                    Log.e("SignUpViewModel", "Exception: ${result.exception}")
                    _errorMessage.value = result.exception.message ?: "네트워크 오류가 발생했습니다."
                }
            }
            _isLoading.value = false
        }
    }

    // 학생 회원가입 데이터 유효성 검사
    private fun isStudentSignUpDataValid(data: SignUpData): Boolean {
        val isValid = !data.phoneNumber.isNullOrEmpty() &&
                !data.sToken.isNullOrEmpty() &&
                !data.sIdno.isNullOrEmpty() &&
                data.privacyAgree &&
                data.termsAgree &&
                data.userType == "user"
        
        // 유효성 검사 결과 로그 출력
        Log.d("SignUpViewModel", "=== 학생 회원가입 데이터 유효성 검사 ===")
        Log.d("SignUpViewModel", "Phone Number: '${data.phoneNumber}' (valid: ${!data.phoneNumber.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Student Token: '${data.sToken}' (valid: ${!data.sToken.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Student ID: '${data.sIdno}' (valid: ${!data.sIdno.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Privacy Agree: ${data.privacyAgree}")
        Log.d("SignUpViewModel", "Terms Agree: ${data.termsAgree}")
        Log.d("SignUpViewModel", "User Type: '${data.userType}' (valid: ${data.userType == "user"})")
        Log.d("SignUpViewModel", "Overall Valid: $isValid")
        Log.d("SignUpViewModel", "=====================================")
        
        return isValid
    }

    // 관리자 회원가입 데이터 유효성 검사
    private fun isAdminSignUpDataValid(data: SignUpData): Boolean {
        val isValid = !data.phoneNumber.isNullOrEmpty() &&
                !data.email.isNullOrEmpty() &&
                !data.password.isNullOrEmpty() &&
                !data.university.isNullOrEmpty() &&
                !data.detailAddress.isNullOrEmpty() &&
                data.selectedPlace != null &&
                data.signImageFile != null &&
                data.privacyAgree &&
                data.termsAgree &&
                data.userType == "admin"
        
        // 유효성 검사 결과 로그 출력
        Log.d("SignUpViewModel", "=== 관리자 회원가입 데이터 유효성 검사 ===")
        Log.d("SignUpViewModel", "Phone Number: '${data.phoneNumber}' (valid: ${!data.phoneNumber.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Email: '${data.email}' (valid: ${!data.email.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Password: '${data.password}' (valid: ${!data.password.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Department: '${data.department}' (optional: ${data.department.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Major: '${data.major}' (optional: ${data.major.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "University: '${data.university}' (valid: ${!data.university.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Detail Address: '${data.detailAddress}' (valid: ${!data.detailAddress.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Selected Place: ${data.selectedPlace} (valid: ${data.selectedPlace != null})")
        if (data.selectedPlace != null) {
            Log.d("SignUpViewModel", "  - Place ID: '${data.selectedPlace.placeId}'")
            Log.d("SignUpViewModel", "  - Name: '${data.selectedPlace.name}'")
            Log.d("SignUpViewModel", "  - Address: '${data.selectedPlace.address}'")
            Log.d("SignUpViewModel", "  - Road Address: '${data.selectedPlace.roadAddress}'")
            Log.d("SignUpViewModel", "  - Latitude: ${data.selectedPlace.latitude}")
            Log.d("SignUpViewModel", "  - Longitude: ${data.selectedPlace.longitude}")
        }
        Log.d("SignUpViewModel", "Sign Image File: ${data.signImageFile?.name ?: "null"} (valid: ${data.signImageFile != null})")
        Log.d("SignUpViewModel", "Privacy Agree: ${data.privacyAgree}")
        Log.d("SignUpViewModel", "Terms Agree: ${data.termsAgree}")
        Log.d("SignUpViewModel", "User Type: '${data.userType}' (valid: ${data.userType == "admin"})")
        Log.d("SignUpViewModel", "Overall Valid: $isValid")
        Log.d("SignUpViewModel", "=====================================")
        
        return isValid
    }

    // 제휴업체 회원가입 데이터 유효성 검사
    private fun isPartnerSignUpDataValid(data: SignUpData): Boolean {
        val isValid = !data.phoneNumber.isNullOrEmpty() &&
                !data.email.isNullOrEmpty() &&
                !data.password.isNullOrEmpty() &&
                !data.companyName.isNullOrEmpty() &&
                !data.detailAddress.isNullOrEmpty() &&
                data.selectedPlace != null &&
                data.licenseImageFile != null &&
                data.privacyAgree &&
                data.termsAgree &&
                data.userType == "partner"
        
        // 유효성 검사 결과 로그 출력
        Log.d("SignUpViewModel", "=== 제휴업체 회원가입 데이터 유효성 검사 ===")
        Log.d("SignUpViewModel", "Phone Number: '${data.phoneNumber}' (valid: ${!data.phoneNumber.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Email: '${data.email}' (valid: ${!data.email.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Password: '${data.password}' (valid: ${!data.password.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Department: '${data.department}' (optional: ${data.department.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Major: '${data.major}' (optional: ${data.major.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "University: '${data.university}' (optional: ${data.university.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Company Name: '${data.companyName}' (valid: ${!data.companyName.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Business Number: '${data.businessNumber}' (optional: ${data.businessNumber.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Representative Name: '${data.representativeName}' (optional: ${data.representativeName.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Detail Address: '${data.detailAddress}' (valid: ${!data.detailAddress.isNullOrEmpty()})")
        Log.d("SignUpViewModel", "Selected Place: ${data.selectedPlace} (valid: ${data.selectedPlace != null})")
        if (data.selectedPlace != null) {
            Log.d("SignUpViewModel", "  - Place ID: '${data.selectedPlace.placeId}'")
            Log.d("SignUpViewModel", "  - Name: '${data.selectedPlace.name}'")
            Log.d("SignUpViewModel", "  - Address: '${data.selectedPlace.address}'")
            Log.d("SignUpViewModel", "  - Road Address: '${data.selectedPlace.roadAddress}'")
            Log.d("SignUpViewModel", "  - Latitude: ${data.selectedPlace.latitude}")
            Log.d("SignUpViewModel", "  - Longitude: ${data.selectedPlace.longitude}")
        }
        Log.d("SignUpViewModel", "License Image File: ${data.licenseImageFile?.name ?: "null"} (valid: ${data.licenseImageFile != null})")
        Log.d("SignUpViewModel", "Privacy Agree: ${data.privacyAgree}")
        Log.d("SignUpViewModel", "Terms Agree: ${data.termsAgree}")
        Log.d("SignUpViewModel", "User Type: '${data.userType}' (valid: ${data.userType == "partner"})")
        Log.d("SignUpViewModel", "Overall Valid: $isValid")
        Log.d("SignUpViewModel", "=====================================")
        
        return isValid
    }

    // 에러 메시지 초기화
    fun clearError() {
        _errorMessage.value = null
    }
}

