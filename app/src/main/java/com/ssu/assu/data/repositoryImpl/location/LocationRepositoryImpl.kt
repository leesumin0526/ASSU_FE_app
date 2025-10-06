package com.ssu.assu.data.repositoryImpl.location

import android.util.Log
import com.ssu.assu.data.dto.location.LocationAdminPartnerSearchResultItem
import com.ssu.assu.data.dto.location.LocationUserSearchResultItem
import com.ssu.assu.data.dto.location.ViewportQuery
import com.ssu.assu.data.dto.location.response.AdminMapResponseDto
import com.ssu.assu.data.dto.location.response.PartnerMapResponseDto
import com.ssu.assu.data.dto.location.response.StoreMapResponseDto
import com.ssu.assu.data.repository.location.LocationRepository
import com.ssu.assu.data.service.location.LocationService
import com.ssu.assu.domain.model.location.AdminOnMap
import com.ssu.assu.domain.model.location.PartnerOnMap
import com.ssu.assu.domain.model.location.StoreOnMap
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.apiHandler
import jakarta.inject.Inject
import kotlin.collections.map


class LocationRepositoryImpl @Inject constructor(
    private val api: LocationService
) : LocationRepository {

    override suspend fun getNearbyPartners(v: ViewportQuery): RetrofitResult<List<PartnerOnMap>> =
        apiHandler(
            execute = { api.getPartners(v.lng1, v.lat1, v.lng2, v.lat2, v.lng3, v.lat3, v.lng4, v.lat4) },
            mapper  = { list -> list.map { it.toModel() } }
        )

    override suspend fun getNearbyAdmins(v: ViewportQuery): RetrofitResult<List<AdminOnMap>> =
        apiHandler(
            execute = { api.getAdmins(v.lng1, v.lat1, v.lng2, v.lat2, v.lng3, v.lat3, v.lng4, v.lat4) },
            mapper  = { list -> list.map { it.toModel() } }
        )

    override suspend fun getNearbyStores(v: ViewportQuery): RetrofitResult<List<StoreOnMap>> =
        apiHandler(
            execute = { api.getStores(v.lng1, v.lat1, v.lng2, v.lat2, v.lng3, v.lat3, v.lng4, v.lat4) },
            mapper  = { list -> list.map { it.toModel() } }
        )


    override suspend fun searchStores(keyword: String): RetrofitResult<List<LocationUserSearchResultItem>> {
        return try {
            apiHandler(
                { api.searchStores(keyword) },
                { dtos -> toLocationUserSearchResult(dtos) }
            )
        } catch (e: Exception) {
            RetrofitResult.Error(e)
        }
    }

    override suspend fun searchPartners(keyword: String)
            : RetrofitResult<List<LocationAdminPartnerSearchResultItem>>{
        return try{
            apiHandler(
                { api.searchPartners(keyword)},{
                        dtos -> toLocationSearchPartnerResult(dtos)
                }
            )
        } catch (e : Exception){
            RetrofitResult.Error(e)
        }
    }

    override suspend fun searchAdmins(keyword: String)
            : RetrofitResult<List<LocationAdminPartnerSearchResultItem>>{
        return try{
            apiHandler(
                { api.searchAdmins(keyword)},{
                        dtos -> toLocationSearchAdminResult(dtos)
                }
            )
        } catch (e : Exception){
            RetrofitResult.Error(e)
        }
    }

    private fun toLocationUserSearchResult(
        dtos: List<StoreMapResponseDto>)
            : List<LocationUserSearchResultItem> {
        return dtos.map { storeDto ->
            LocationUserSearchResultItem(
                storeId = storeDto.storeId,
                shopName = storeDto.name,
                organization = storeDto.name,
                content = toContent(storeDto)
            )
        }
    }

    private fun toLocationSearchAdminResult(
        dtos: List<AdminMapResponseDto>
    ) : List<LocationAdminPartnerSearchResultItem>{
        dtos.forEach {
            Log.d("mapper-admin",   "in adminId=${it.adminId}, partnered=${it.partnered}, partnershipId=${it.partnershipId}")
        }
        return dtos.map{ adminDto ->
            val temp : String?
            if(adminDto.partnershipStartDate==null && adminDto.partnershipEndDate==null){
                temp = null
            } else {
                temp = convertTerm(adminDto.partnershipStartDate!!, adminDto.partnershipEndDate!!)
            }
            LocationAdminPartnerSearchResultItem(
                id= adminDto.adminId,
                shopName = adminDto.name,
                address = adminDto.address.toString(),
                paperId = adminDto.partnershipId,
                partnered = adminDto.partnered,
                term = temp,
                partnershipId = adminDto.partnershipId,
                partnershipStartDate = adminDto.partnershipStartDate,
                partnershipEndDate = adminDto.partnershipEndDate,
                latitude = adminDto.latitude,
                longitude = adminDto.longitude,
                profileUrl = adminDto.profileUrl,
                phoneNumber = adminDto.phoneNumber
            )
        }
    }

    private fun toLocationSearchPartnerResult(
        dtos: List<PartnerMapResponseDto>
    ) : List<LocationAdminPartnerSearchResultItem>{
        dtos.forEach {
            Log.d("mapper-partner", "in partnerId=${it.partnerId}, partnered=${it.partnered}, partnershipId=${it.partnershipId}")

        }

        return dtos.map{ partnerDto ->
            val temp : String?
            if(partnerDto.partnershipStartDate==null && partnerDto.partnershipEndDate==null){
                temp = null
            } else {
                temp = convertTerm(partnerDto.partnershipStartDate!!, partnerDto.partnershipEndDate!!)
            }
            LocationAdminPartnerSearchResultItem(
                id= partnerDto.partnerId,
                shopName = partnerDto.name,
                address = partnerDto.address.toString(),
                paperId = partnerDto.partnershipId,
                partnered = partnerDto.partnered,
                term = temp,
                partnershipId = partnerDto.partnershipId,
                partnershipStartDate = partnerDto.partnershipStartDate,
                partnershipEndDate = partnerDto.partnershipEndDate,
                latitude = partnerDto.latitude,
                longitude = partnerDto.longitude,
                profileUrl = partnerDto.profileUrl,
                phoneNumber = partnerDto.phoneNumber
            )
        }
    }

    private fun convertTerm(startDate: String, endDate: String)
            : String{
        return "${startDate} ~ ${endDate}"
    }

    private fun toContent(store : StoreMapResponseDto): String{
        return when {
            // 1. 인원수 기준 + 서비스 제공
            store.criterionType == "HEADCOUNT" && store.optionType == "SERVICE"->
                "${store.people}명 이상 식사 시 ${store.category} 제공"

            // 2. 인원수 기준 + 할인
            store.criterionType == "HEADCOUNT" && store.optionType =="DISCOUNT" ->
                "${store.people}명 이상 식사 시 ${store.discountRate}% 할인"

            // 3. 가격 기준 + 서비스 제공
            store.criterionType == "PRICE" && store.optionType == "SERVICE" ->
                "${store.cost}원 이상 주문 시 ${store.category} 제공"

            // 4. 가격 기준 + 할인
            store.criterionType == "PRICE" && store.optionType == "DISCOUNT" ->
                "${store.cost}원 이상 주문 시 ${store.discountRate}% 할인"

            // 위 조건에 해당하지 않는 경우 빈 문자열 반환
            else -> ""
        }
    }


}
