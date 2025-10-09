package com.ssu.assu.ui.partnership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.data.dto.partnership.request.ContractImageParam
import com.ssu.assu.data.dto.partnership.request.ManualPartnershipRequestDto
import com.ssu.assu.domain.model.partnership.ManualPartnershipModel
import com.ssu.assu.domain.usecase.partnership.CreateManualPartnershipUseCase
import com.ssu.assu.util.RetrofitResult
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