package com.example.assu_fe_app.data.repositoryImpl.certification

import com.example.assu_fe_app.data.dto.certification.request.PersonalCertificationRequestDto
import com.example.assu_fe_app.data.dto.certification.request.UserSessionRequestDto
import com.example.assu_fe_app.data.dto.certification.response.UserSessionResponseDto
import com.example.assu_fe_app.data.repository.certification.CertificationRepository
import com.example.assu_fe_app.data.service.certification.CertificationService
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
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
    : RetrofitResult<Void> {
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