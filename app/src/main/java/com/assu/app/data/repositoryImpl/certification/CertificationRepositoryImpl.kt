package com.assu.app.data.repositoryImpl.certification

import com.assu.app.data.dto.certification.request.PersonalCertificationRequestDto
import com.assu.app.data.dto.certification.request.UserSessionRequestDto
import com.assu.app.data.dto.certification.response.NoneDataResponseDto
import com.assu.app.data.dto.certification.response.UserSessionResponseDto
import com.assu.app.data.repository.certification.CertificationRepository
import com.assu.app.data.service.certification.CertificationService
import com.assu.app.util.RetrofitResult
import com.assu.app.util.apiHandler
import javax.inject.Inject

class CertificationRepositoryImpl @Inject constructor(
    private val api: CertificationService
) : CertificationRepository {
    override suspend fun requestSessionId(
        request: UserSessionRequestDto
    ) : RetrofitResult<UserSessionResponseDto>{
        return try{
            apiHandler(
                {api.requestSessionId(request)},
                {dto -> dto})
        }
        catch(e: Exception) {
            RetrofitResult.Error(e)
        }
    }

    override suspend fun postPersonalData(
        request: PersonalCertificationRequestDto)
    : RetrofitResult<NoneDataResponseDto> {
        return try{
            apiHandler(
                {api.postPersonalData(request)},
                {dto -> dto}
            )
        } catch(e: Exception){
            RetrofitResult.Error(e)
        }
    }
}