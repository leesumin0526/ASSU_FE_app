package com.example.assu_fe_app.ui.suggestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.suggestion.request.WriteSuggestionRequestDto
import com.example.assu_fe_app.data.dto.suggestion.response.SuggestionTargetInfoDto
import com.example.assu_fe_app.data.dto.suggestion.response.WriteSuggestionResponseDto
import com.example.assu_fe_app.domain.model.suggestion.SuggestionTargetModel
import com.example.assu_fe_app.domain.model.suggestion.WriteSuggestionModel
import com.example.assu_fe_app.domain.usecase.suggestion.GetSuggestionAdminsUseCase
import com.example.assu_fe_app.domain.usecase.suggestion.WriteSuggestionUseCase
import com.example.assu_fe_app.util.onError
import com.example.assu_fe_app.util.onFail
import com.example.assu_fe_app.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuggestionViewModel @Inject constructor(
    private val writeSuggestionUseCase: WriteSuggestionUseCase,
    private val getSuggestionAdminsUseCase: GetSuggestionAdminsUseCase
) : ViewModel() {
    sealed interface GetAdminsUiState {
        data object Idle: GetAdminsUiState
        data object Loading: GetAdminsUiState
        data class Success(val data: List<SuggestionTargetModel>): GetAdminsUiState
        data class Fail(val code: Int, val message: String?): GetAdminsUiState
        data class Error(val message: String): GetAdminsUiState
    }

    sealed interface WriteSuggestionUiState {
        data object Idle: WriteSuggestionUiState
        data object Loading: WriteSuggestionUiState
        data class Success(val data: WriteSuggestionModel): WriteSuggestionUiState
        data class Fail(val code: Int, val message: String?): WriteSuggestionUiState
        data class Error(val message: String): WriteSuggestionUiState
    }

    private val _getAdminsState = MutableStateFlow<GetAdminsUiState>(GetAdminsUiState.Idle)
    val getAdminsState: StateFlow<GetAdminsUiState> = _getAdminsState.asStateFlow()

    private val _selectedTarget = MutableStateFlow<SuggestionTargetModel?>(null)
    val selectedTarget: StateFlow<SuggestionTargetModel?> = _selectedTarget.asStateFlow()

    private val _writeSuggestionState = MutableStateFlow<WriteSuggestionUiState>(
        WriteSuggestionUiState.Idle)
    val writeSuggestionState: StateFlow<WriteSuggestionUiState> = _writeSuggestionState.asStateFlow()

    val storeName = MutableStateFlow("")
    val benefit = MutableStateFlow("")

    init {
        fetchAdmins()
    }

    fun fetchAdmins() {
        viewModelScope.launch {
            _getAdminsState.value = GetAdminsUiState.Loading
            getSuggestionAdminsUseCase()
                .onSuccess { modelList -> _getAdminsState.value = GetAdminsUiState.Success(modelList) }
                .onFail { code -> _getAdminsState.value = GetAdminsUiState.Fail(code, "건의 대상 목록 조회 실패") }
                .onError { e -> _getAdminsState.value = GetAdminsUiState.Error(e.message ?: "Unknown Error") }
        }
    }

    fun writeSuggestion() {
        val currentTarget = _selectedTarget.value
        if (storeName.value.isBlank() || benefit.value.isBlank() || currentTarget == null) {
            return
        }

        val request = WriteSuggestionRequestDto(
            adminId = currentTarget.id,
            storeName = storeName.value,
            benefit = benefit.value
        )

        viewModelScope.launch {
            _writeSuggestionState.value = WriteSuggestionUiState.Loading
            writeSuggestionUseCase(request)
                .onSuccess { model -> _writeSuggestionState.value = WriteSuggestionUiState.Success(model) }
                .onFail { code -> _writeSuggestionState.value = WriteSuggestionUiState.Fail(code, "건의 등록 실패") }
                .onError { e -> _writeSuggestionState.value = WriteSuggestionUiState.Error(e.message ?: "Unknown Error") }
        }
    }

    fun selectTarget(target: SuggestionTargetModel) {
        _selectedTarget.value = target
    }

    fun resetWriteSuggestionState() {
        _writeSuggestionState.value = WriteSuggestionUiState.Idle
    }

}