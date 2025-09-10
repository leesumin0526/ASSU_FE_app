package com.example.assu_fe_app.presentation.user.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.certification.UserSessionRequestDto
import com.example.assu_fe_app.data.dto.store.PaperContent
import com.example.assu_fe_app.data.dto.store.StorePartnershipResponseDto
import com.example.assu_fe_app.domain.usecase.certification.GetSessionIdUseCase
import com.example.assu_fe_app.domain.usecase.store.GetStorePartnershipUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserVerifyViewModel @Inject constructor(
    private val useCase : GetStorePartnershipUseCase,
    private val certificationUseCase: GetSessionIdUseCase
): ViewModel(){

    // 기본 정보
    var storeId : Long = 0
    var sessionId : Long =0

    var tableNumber : String = ""
    private val _storeName = MutableLiveData<String>()
    val storeName : LiveData<String> = _storeName

    var isPeopleType : Boolean = false
    var isGoodsList : Boolean = false
    var isPriceType : Boolean = false

    private val _contentList = MutableLiveData<List<PaperContent>>()
    val contentList: LiveData<List<PaperContent>> = _contentList

    private val _selectedContent = MutableLiveData<PaperContent?>()
    val selectedContent: LiveData<PaperContent?> = _selectedContent

    fun getStorePartnership(){
        viewModelScope.launch{
            when (val result = useCase(storeId)) {
                is RetrofitResult.Success -> {
                    _storeName.value = result.data.storeName
                    storeId = result.data.storeId
                    _contentList.value = result.data.contents
                    Log.d("조회된 storeName", "${_storeName.value}")
                    Log.d("조회된 contentList" , contentList.value.toString())
                }

                is RetrofitResult.Error -> {
                    // 에러 처리
                }
                is RetrofitResult.Fail -> {
                    // 실패 처리
                }
            }
        }
    }

    fun requestSessionId(
        request: UserSessionRequestDto
    ){
        viewModelScope.launch {
            when (val result = certificationUseCase(request)) {
                is RetrofitResult.Success -> {
                    sessionId = result.data.sessionId
                    Log.d("조회된 sessionId, adminId", "${sessionId}")
                    // 여기서 주소 구독 로직 실행
                }
                is RetrofitResult.Error -> {
                    // 에러 처리
                }
                is RetrofitResult.Fail -> {
                }
            }

        }

    }

    // 선택된 제휴사 정보를 저장하는 함수
    fun selectPartnership(content: PaperContent) {
        _selectedContent.value = content

        if(!content.goods.isNullOrEmpty() && content.goods.size > 1){
            isGoodsList = true
        }
        if(content.people != null && content.people > 1){
            isPeopleType = true
        }
        content.cost?.let {
            if(it > 0){
                isPriceType = true
            } // 추후에 로직 수정
        }
    }

    // selectedContent에서 필요한 정보를 가져오는 편의 함수들

    val selectedContentId: Long
        get() = selectedContent.value?.contentId ?: 0

    val selectedAdminName: String
        get() = selectedContent.value?.adminName ?: ""

    val selectedPeople: Int
        get() = selectedContent.value?.people ?: 0

    val selectedAdminId: Long
        get() = selectedContent.value?.adminId?: 0
    val selectedPaperContent: String
        get() = selectedContent.value?.paperContent ?: ""

    val selectedGoodsList: List<String>
        get() = selectedContent.value?.goods ?: emptyList()
}