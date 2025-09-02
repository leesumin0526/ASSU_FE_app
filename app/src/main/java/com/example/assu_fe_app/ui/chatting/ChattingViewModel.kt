package com.example.assu_fe_app.ui.chatting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.domain.model.chatting.CreateChatRoomModel
import com.example.assu_fe_app.domain.usecase.chatting.CreateChatRoomUseCase
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.onError
import com.example.assu_fe_app.util.onFail
import com.example.assu_fe_app.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChattingViewModel @Inject constructor(
    private val createChatRoomUseCase: CreateChatRoomUseCase
) : ViewModel() {

    sealed interface CreateRoomUiState {
        data object Idle : CreateRoomUiState
        data object Loading : CreateRoomUiState
        data class Success(val data: CreateChatRoomModel) : CreateRoomUiState
        data class Fail(val code: Int, val message: String?) : CreateRoomUiState
        data class Error(val message: String) : CreateRoomUiState
    }

    private val _createRoomState = MutableStateFlow<CreateRoomUiState>(CreateRoomUiState.Idle)
    val createRoomState: StateFlow<CreateRoomUiState> = _createRoomState

    fun createRoom(req: CreateChatRoomRequestDto) {
        viewModelScope.launch {
            _createRoomState.value = CreateRoomUiState.Loading
            createChatRoomUseCase(req)
                .onSuccess { _createRoomState.value = CreateRoomUiState.Success(it) }
                .onFail    { code -> _createRoomState.value = CreateRoomUiState.Fail(code, "서버 처리 실패") }
                .onError   { e -> _createRoomState.value = CreateRoomUiState.Error(e.message ?: "Unknown Error") }
        }
    }

    fun resetCreateState() { _createRoomState.value = CreateRoomUiState.Idle }
}