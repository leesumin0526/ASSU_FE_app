package com.example.assu_fe_app.ui.partnership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.partnership.request.ContractImageParam
import com.example.assu_fe_app.data.dto.partnership.request.ManualPartnershipRequestDto
import com.example.assu_fe_app.domain.model.partnership.ManualPartnershipModel
import com.example.assu_fe_app.domain.usecase.partnership.CreateManualPartnershipUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PassiveProposalViewModel @Inject constructor(
    private val createManualPartnership: CreateManualPartnershipUseCase
) : ViewModel() {

    val loading = MutableStateFlow(false)
    val result  = MutableSharedFlow<Result<ManualPartnershipModel>>()

    fun submit(
        req: ManualPartnershipRequestDto,
        image: ContractImageParam?
    ) = viewModelScope.launch {
        loading.value = true
        val res = createManualPartnership(req, image)
        loading.value = false

        when (res) {
            is RetrofitResult.Success -> result.emit(Result.success(res.data))
            is RetrofitResult.Fail -> result.emit(Result.failure(Exception(res.message)))
            is RetrofitResult.Error -> result.emit(Result.failure(Exception("네트워크 오류")))
        }
    }
}