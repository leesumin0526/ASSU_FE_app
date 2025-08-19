package com.example.assu_fe_app.ui.chatting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.domain.model.chatting.CreateChatRoomModel
import com.example.assu_fe_app.domain.usecase.chatting.CreateChatRoomUseCase
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
        data class Error(val message: String) : CreateRoomUiState
    }

    private val _createRoomState = MutableStateFlow<CreateRoomUiState>(CreateRoomUiState.Idle)
    val createRoomState: StateFlow<CreateRoomUiState> = _createRoomState

    fun createRoom(createChatRoomRequestDto: CreateChatRoomRequestDto) {
        viewModelScope.launch { _createRoomState.value = CreateRoomUiState.Loading
        runCatching { createChatRoomUseCase(createChatRoomRequestDto) }
            .onSuccess { _createRoomState.value = CreateRoomUiState.Success(it) }
            .onFailure { _createRoomState.value = CreateRoomUiState.Error(it.message ?: "Unknown Error") }}
    }
    fun resetCreateState() { _createRoomState.value = CreateRoomUiState.Idle }
}