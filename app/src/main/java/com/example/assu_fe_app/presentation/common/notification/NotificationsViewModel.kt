package com.example.assu_fe_app.presentation.common.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.domain.model.notification.NotificationModel
import com.example.assu_fe_app.domain.usecase.notification.GetNotificationsUseCase
import com.example.assu_fe_app.domain.usecase.notification.MarkNotificationReadUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotifications: GetNotificationsUseCase,
    private val markNotificationRead: MarkNotificationReadUseCase
) : ViewModel() {

    // ===== 새로 추가: 네비게이션 이벤트 =====
    sealed interface NavEvent {
        data class ToChatRoom(val roomId: Long) : NavEvent
        data class ToPartnerSuggestionDetail(val suggestionId: Long) : NavEvent
        data class ToPartnerProposalDetail(val proposalId: Long) : NavEvent
        // 필요 시 더 추가
    }
    private val _navEvents = MutableSharedFlow<NavEvent>(extraBufferCapacity = 1)
    val navEvents: SharedFlow<NavEvent> = _navEvents
    // =======================================

    data class PageState(
        val items: List<NotificationModel> = emptyList(),
        val page: Int = 1,
        val size: Int = 20,
        val totalPages: Int = 1,
        val loading: Boolean = false,
        val refreshing: Boolean = false,
        val error: String? = null
    )

    private val _all    = MutableStateFlow(PageState())
    private val _unread = MutableStateFlow(PageState())

    private val _switchToUnread = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val switchToUnread: SharedFlow<Unit> = _switchToUnread

    val allState: StateFlow<PageState>    = _all
    val unreadState: StateFlow<PageState> = _unread

    fun refresh(status: String, size: Int = 20, silent: Boolean = false) =
        load(status = status, page = 1, size = size, reset = true, silent = silent)

    fun loadMore(status: String) {
        val st = state(status).value
        if (st.loading) return
        if (st.page > st.totalPages) return
        load(status = status, page = st.page, size = st.size, reset = false)
    }

    private fun load(
        status: String,
        page: Int,
        size: Int,
        reset: Boolean,
        silent: Boolean = false
    ) = viewModelScope.launch {
        val tgt = state(status)
        val cur = tgt.value
        if (!silent) tgt.value = cur.copy(loading = true, refreshing = reset, error = null)

        when (val res = getNotifications(status, page, size)) {
            is RetrofitResult.Success -> {
                val p = res.data
                val merged = if (reset || page == 1) p.items else cur.items + p.items
                val nextPage = if (p.page < p.totalPages) p.page + 1 else p.page + 1
                tgt.value = cur.copy(
                    items = merged,
                    page = nextPage,
                    size = p.size,
                    totalPages = p.totalPages,
                    loading = false,
                    refreshing = false,
                    error = null
                )
            }
            is RetrofitResult.Fail -> {
                if (!silent) tgt.value = cur.copy(loading = false, refreshing = false, error = res.message)
            }
            is RetrofitResult.Error -> {
                if (!silent) tgt.value = cur.copy(loading = false, refreshing = false, error = "네트워크 오류")
            }
        }
    }

    fun requestSwitchToUnread() {
        _switchToUnread.tryEmit(Unit)
    }

    private fun state(status: String) = if (status == "unread") _unread else _all

    /**
     * 아이템 클릭: 읽음 처리 + (ORDER 제외) 네비게이션 이벤트 emit
     */
    fun onItemClickAndReload(item: NotificationModel, activeTab: String) = viewModelScope.launch {
        val id = item.id

        // 스냅샷
        val beforeAll = _all.value
        val beforeUnread = _unread.value

        // 1) 낙관적 적용
        when (activeTab) {
            "all" -> _all.value = beforeAll.copy(
                items = beforeAll.items.map { if (it.id == id) it.copy(isRead = true) else it }
            )
            "unread" -> _unread.value = beforeUnread.copy(
                items = beforeUnread.items.filterNot { it.id == id }
            )
        }

        // 4) 네비게이션 이벤트 (ORDER 제외)
        when (item.type) {
            "ORDER" -> {
                // 주문 알림은 네비게이션 없음 (TTS/알림만)
            }
            "CHAT" -> {
                val roomId = item.refId ?: return@launch
                _navEvents.tryEmit(NavEvent.ToChatRoom(roomId))
            }
            "PARTNER_SUGGESTION" -> {
                val suggestionId = item.refId ?: return@launch
                _navEvents.tryEmit(NavEvent.ToPartnerSuggestionDetail(suggestionId))
            }
            "PARTNER_PROPOSAL" -> {
                val proposalId = item.refId ?: return@launch
                _navEvents.tryEmit(NavEvent.ToPartnerProposalDetail(proposalId))
            }
            else -> {
                // 미정 타입: 아직 없음
            }
        }

        // 2) 서버 반영
        val markResult = markNotificationRead(id)

        // 3) 반대 탭 조용히 동기화
        if (markResult is RetrofitResult.Success) {
            if (activeTab == "all") refresh("unread", silent = true) else refresh("all", silent = true)
        } else {
            // 실패 시 롤백
            _all.value = beforeAll
            _unread.value = beforeUnread
            return@launch
        }
    }

    fun emitNavEvent(item: NotificationModel) {
        when (item.type) {
            "ORDER" -> {
                // 주문은 네비게이션 없음
            }
            "CHAT" -> {
                val roomId = item.refId ?: return
                _navEvents.tryEmit(NavEvent.ToChatRoom(roomId))
            }
            "PARTNER_SUGGESTION" -> {
                val suggestionId = item.refId ?: return
                _navEvents.tryEmit(NavEvent.ToPartnerSuggestionDetail(suggestionId))
            }
            "PARTNER_PROPOSAL" -> {
                val proposalId = item.refId ?: return
                _navEvents.tryEmit(NavEvent.ToPartnerProposalDetail(proposalId))
            }
        }
    }
}