package com.example.assu_fe_app.ui.chatting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.chatting.ChatRoomUpdateDTO
import com.example.assu_fe_app.data.socket.ChatSocketClient
import com.example.assu_fe_app.domain.model.chatting.GetChattingRoomListModel
import com.example.assu_fe_app.domain.usecase.chatting.GetChattingRoomListUseCase
import com.example.assu_fe_app.util.onSuccess
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChattingListViewModel @Inject constructor(
    private val getChattingRoomListUseCase: GetChattingRoomListUseCase,
    private val chatSocket: ChatSocketClient, // ChatSocketClient는 Singleton으로 주입받아야 함
) : ViewModel(){
    // 1. 채팅방 리스트를 관리하는 StateFlow
    private val _chatRooms = MutableStateFlow<List<GetChattingRoomListModel>>(emptyList())
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val updateDtoAdapter = moshi.adapter(ChatRoomUpdateDTO::class.java)
    val chatRooms: StateFlow<List<GetChattingRoomListModel>> = _chatRooms.asStateFlow()

    // 2. 채팅방 리스트를 최초로 불러오는 API 호출 함수
    fun getChattingRoomList() {
        viewModelScope.launch {
            // ... (기존의 getChattingRoomListUseCase 호출 로직)
            getChattingRoomListUseCase()
                .onSuccess { initialList -> _chatRooms.value = initialList }
            // ... 에러 처리
        }
    }

    // 3. 실시간 업데이트 알림을 구독하는 함수
    fun subscribeToUserUpdates() {
        chatSocket.subscribeToUserQueue { jsonString ->
            // 워커 스레드에서 실행될 수 있으므로 runCatching으로 안전하게 처리
            val updateDto = runCatching { updateDtoAdapter.fromJson(jsonString) }.getOrNull()

            if (updateDto != null) {
                // 메인 스레드에서 UI 상태 업데이트
                viewModelScope.launch {
                    updateChatRoomList(updateDto)
                }
            }
        }
    }

    // 5. 구독을 해제하는 함수
    fun unsubscribeFromUserUpdates() {
        chatSocket.unsubscribeFromUserUpdates()
    }

    // 6. 수신된 정보로 리스트를 업데이트하는 핵심 로직
    private fun updateChatRoomList(updateDto: ChatRoomUpdateDTO) {
        val currentList = _chatRooms.value.toMutableList()
        val indexToUpdate = currentList.indexOfFirst { it.roomId == updateDto.roomId }

        if (indexToUpdate != -1) {
            // 리스트에 이미 해당 채팅방이 있는 경우
            val oldRoom = currentList[indexToUpdate]
            val updatedRoom = oldRoom.copy( // data class의 copy 활용
                lastMessage = updateDto.lastMessage,
                lastMessageTime = updateDto.lastMessageTime, // 타입 변환 필요할 수 있음
                unreadMessagesCount = updateDto.unreadCount
            )

            // 기존 아이템을 삭제하고 업데이트된 아이템을 맨 위에 추가
            currentList.removeAt(indexToUpdate)
            currentList.add(0, updatedRoom)
        } else {
            // 리스트에 없던 새로운 채팅방의 첫 메시지인 경우 (예: 상대방이 채팅을 처음 건 경우)
            // 전체 리스트를 새로고침하여 새 채팅방을 포함시킨다.
            getChattingRoomList()
            return
        }

        _chatRooms.value = currentList
    }
}