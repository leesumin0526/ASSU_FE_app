package com.ssu.assu.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.domain.model.dashboard.PopularStoreModel
import com.ssu.assu.domain.model.user.GetUsablePartnershipModel
import com.ssu.assu.domain.usecase.user.GetStampUseCase
import com.ssu.assu.domain.usecase.user.GetTodayBestStoresUseCase
import com.ssu.assu.domain.usecase.user.GetUsablePartnershipUseCase
import com.ssu.assu.util.RetrofitResult
import com.ssu.assu.util.onError
import com.ssu.assu.util.onFail
import com.ssu.assu.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val getStampCountUseCase: GetStampUseCase,
    private val getTodayBestStoresUseCase: GetTodayBestStoresUseCase,
    private val getUsablePartnershipUseCase: GetUsablePartnershipUseCase
) : ViewModel() {

    sealed interface StampUiState {
        data object Idle : StampUiState
        data object Loading : StampUiState
        data class Success(val stampCount: Int) : StampUiState
        data class Error(val message: String) : StampUiState
    }
    sealed interface PopularStoresUiState {
        data object Idle : PopularStoresUiState
        data object Loading : PopularStoresUiState
        data class Success(val stores: List<PopularStoreModel>) : PopularStoresUiState
        data class Error(val message: String) : PopularStoresUiState
    }

    private val _stampState = MutableStateFlow<StampUiState>(StampUiState.Idle)
    val stampState: StateFlow<StampUiState> = _stampState

    private val _popularStoresState = MutableStateFlow<PopularStoresUiState>(PopularStoresUiState.Idle)
    val popularStoresState: StateFlow<PopularStoresUiState> = _popularStoresState

    init {
        loadStampCount()
    }
    private fun loadHomeData() {
        viewModelScope.launch {
            coroutineScope {
                val stampDeferred = async { loadStampCount() }
                val popularStoresDeferred = async { loadPopularStores() }

                stampDeferred.await()
                popularStoresDeferred.await()
            }
        }
    }

    fun loadStampCount() {
        viewModelScope.launch {
            _stampState.value = StampUiState.Loading

            when (val result = getStampCountUseCase()) {
                is RetrofitResult.Success -> {
                    _stampState.value = StampUiState.Success(result.data)
                }

                is RetrofitResult.Fail -> {
                    _stampState.value = StampUiState.Error("서버 오류: ${result.message}")
                }

                is RetrofitResult.Error -> {
                    _stampState.value = StampUiState.Error("네트워크 오류가 발생했습니다")
                }
            }
        }
    }
    suspend fun loadPopularStores() {
        _popularStoresState.value = PopularStoresUiState.Loading

        when (val result = getTodayBestStoresUseCase()) {
            is RetrofitResult.Success -> {
                _popularStoresState.value = PopularStoresUiState.Success(result.data)
            }
            is RetrofitResult.Fail -> {
                _popularStoresState.value = PopularStoresUiState.Error("서버 오류: ${result.message}")
            }
            is RetrofitResult.Error -> {
                _popularStoresState.value = PopularStoresUiState.Error("네트워크 오류가 발생했습니다")
            }
        }
    }

    sealed interface GetUsableProposalUiState {
        data object Idle : GetUsableProposalUiState
        data object Loading : GetUsableProposalUiState
        data class Success(val data: List<GetUsablePartnershipModel>) : GetUsableProposalUiState
        data class Fail(val code: Int, val message: String?) : GetUsableProposalUiState
        data class Error(val message: String) : GetUsableProposalUiState
    }

    private val _getUsableProposalState = MutableStateFlow<GetUsableProposalUiState>(GetUsableProposalUiState.Idle)
    val getUsableProposalState: StateFlow<GetUsableProposalUiState> = _getUsableProposalState

    fun getUsableProposalList(all: Boolean) {
        viewModelScope.launch {
            _getUsableProposalState.value = GetUsableProposalUiState.Loading
            getUsablePartnershipUseCase(all)
                .onSuccess { _getUsableProposalState.value = GetUsableProposalUiState.Success(it) }
                .onFail { code -> _getUsableProposalState.value = GetUsableProposalUiState.Fail( code,"Unknown Error") }
                .onError { e -> _getUsableProposalState.value = GetUsableProposalUiState.Error(e.message ?: "Unknown Error") }
        }
    }


    fun refreshData() {
        loadHomeData()
    }
}