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

    // ===== 네비게이션 이벤트 =====
    sealed interface NavEvent {
        data class ToChatRoom(val roomId: Long) : NavEvent
        data class ToPartnerSuggestionDetail(val suggestionId: Long) : NavEvent
        data class ToPartnerProposalDetail(val proposalId: Long) : NavEvent
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
                // BUGFIX: 마지막 페이지면 더 증가하지 않도록
                val nextPage = if (p.page < p.totalPages) p.page + 1 else p.page
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

    fun onItemClickSmart(item: NotificationModel, activeTab: String) = viewModelScope.launch {
        val id = item.id
        val needMark = !item.isRead

        // 스냅샷 (롤백 대비)
        val beforeAll = _all.value
        val beforeUnread = _unread.value

        // 1) 낙관적 반영 (미읽음일 때만)
        if (needMark) {
            when (activeTab) {
                "all" -> _all.value = beforeAll.copy(
                    items = beforeAll.items.map { if (it.id == id) it.copy(isRead = true) else it }
                )
                "unread" -> _unread.value = beforeUnread.copy(
                    items = beforeUnread.items.filterNot { it.id == id }
                )
            }
        }

        // 2) 네비게이션 이벤트 (읽음 여부와 무관하게 emit)
        when (item.type) {
            "ORDER" -> { /* 이동 없음 */ }
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
            else -> { /* 확장 포인트 */ }
        }

        // 3) 서버 반영 (미읽음이었던 경우에만)
        if (!needMark) return@launch

        when (markNotificationRead(id)) {
            is RetrofitResult.Success -> {
                // 반대 탭만 조용히 동기화
                if (activeTab == "all") refresh("unread", silent = true) else refresh("all", silent = true)
            }
            else -> {
                // 실패 시 롤백
                _all.value = beforeAll
                _unread.value = beforeUnread
            }
        }
    }

    // (참고) 기존 메서드는 더 이상 필요 없으면 삭제해도 됨
    fun emitNavEvent(item: NotificationModel) {
        when (item.type) {
            "ORDER" -> { /* 이동 없음 */ }
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