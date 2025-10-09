package com.ssu.assu.data.repositoryImpl.certification

import com.ssu.assu.data.dto.certification.request.PersonalCertificationRequestDto
import com.ssu.assu.data.dto.certification.request.UserSessionRequestDto
import com.ssu.assu.data.dto.certification.response.NoneDataResponseDto
import com.ssu.assu.data.dto.certification.response.UserSessionResponseDto
import com.ssu.assu.data.repository.certification.CertificationRepository
import com.ssu.assu.data.service.certification.CertificationService
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
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