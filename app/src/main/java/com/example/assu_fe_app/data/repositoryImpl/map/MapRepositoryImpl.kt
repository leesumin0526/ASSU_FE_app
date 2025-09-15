package com.example.assu_fe_app.data.repositoryImpl.map

import com.example.assu_fe_app.data.dto.location.LocationAdminPartnerSearchResultItem
import com.example.assu_fe_app.data.dto.location.LocationUserSearchResultItem
import com.example.assu_fe_app.data.dto.map.AdminMapResponseDto
import com.example.assu_fe_app.data.dto.map.PartnerMapResponseDto
import com.example.assu_fe_app.data.dto.map.StoreMapResponseDto
import com.example.assu_fe_app.data.repository.map.MapRepository
import com.example.assu_fe_app.data.service.map.MapService
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.apiHandler
import java.time.LocalDate
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val mapService: MapService
) : MapRepository {
    override suspend fun searchStores(keyword: String)
    : RetrofitResult<List<LocationUserSearchResultItem>>{
        return try{
            apiHandler(
                {mapService.searchStores(keyword)},{
                    dtos -> toLocationUserSearchResult(dtos)
                }
            )
        } catch (e : Exception){
            RetrofitResult.Error(e)
        }
    }
    override suspend fun searchPartners(keyword: String)
            : RetrofitResult<List<LocationAdminPartnerSearchResultItem>>{
        return try{
            apiHandler(
                {mapService.searchPartners(keyword)},{
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
                {mapService.searchAdmins(keyword)},{
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
                organization = storeDto.adminName,
                content = toContent(storeDto)
            )
        }
    }

    private fun toLocationSearchAdminResult(
        dtos: List<AdminMapResponseDto>
    )
    : List<LocationAdminPartnerSearchResultItem>{
        return dtos.map{ adminDto ->
            val temp : String?
            if(adminDto.partnershipStartDate==null && adminDto.partnershipEndDate==null){
                temp = null
            } else {
                temp = convertTerm(adminDto.partnershipStartDate!!, adminDto.partnershipEndDate!!)
            }
            LocationAdminPartnerSearchResultItem(
                id= adminDto.adminId,
                name = adminDto.name,
                address = adminDto.address,
                paperId = adminDto.partnershipId,
                isPartnered = adminDto.partnered,
                term = temp
            )
        }
    }

    private fun toLocationSearchPartnerResult(
        dtos: List<PartnerMapResponseDto>
    ) : List<LocationAdminPartnerSearchResultItem>{
        return dtos.map{ partnerDto ->
            val temp : String?
            if(partnerDto.partnershipStartDate==null && partnerDto.partnershipEndDate==null){
                temp = null
            } else {
                temp = convertTerm(partnerDto.partnershipStartDate!!, partnerDto.partnershipEndDate!!)
            }
            LocationAdminPartnerSearchResultItem(
                id= partnerDto.partnerId,
                name = partnerDto.name,
                address = partnerDto.address,
                paperId = partnerDto.partnerId,
                isPartnered = partnerDto.partnered,
                term = temp
            )
        }
    }

    private fun convertTerm(startDate: LocalDate, endDate: LocalDate)
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