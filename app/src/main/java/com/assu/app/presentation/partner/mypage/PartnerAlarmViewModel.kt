package com.assu.app.presentation.partner.mypage

import androidx.lifecycle.ViewModel
import com.assu.app.domain.model.notification.NotificationTypeModel
import com.assu.app.domain.usecase.notification.GetNotificationSettingsUseCase
import com.assu.app.domain.usecase.notification.ToggleNotificationUseCase
import com.assu.app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PartnerNotificationPrefs(
    val chat: Boolean = true,
    val order: Boolean = true
) {
    val anyOn: Boolean get() = chat || order
}

@HiltViewModel
class PartnerAlarmViewModel @Inject constructor(
    private val getSettings: GetNotificationSettingsUseCase,
    private val toggleNotification: ToggleNotificationUseCase
) : ViewModel() {

    private val _prefs = MutableStateFlow(PartnerNotificationPrefs())
    val prefs: StateFlow<PartnerNotificationPrefs> = _prefs.asStateFlow()

    // 마스터 = OR
    val master: StateFlow<Boolean> =
        prefs.map { it.anyOn }.stateIn(
            scope = kotlinx.coroutines.CoroutineScope(Dispatchers.Main.immediate),
            started = SharingStarted.Lazily,
            initialValue = true
        )

    val loading = MutableStateFlow(false)
    val error   = MutableStateFlow<String?>(null)

    private var serverSnapshot: PartnerNotificationPrefs? = null
    private var promotedOnce = false // (옵션) 처음 한 번만 마스터 ON 시 하위 모두 ON

    /** 최초 1회 서버 로드 */
    fun load() {
        if (loading.value) return
        loading.value = true
        error.value = null

        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            val res = getSettings()
            withContext(Dispatchers.Main) {
                when (res) {
                    is RetrofitResult.Success -> {
                        val snap = PartnerNotificationPrefs(
                            chat  = res.data.chat,
                            order = res.data.order
                        )
                        serverSnapshot = snap
                        _prefs.value = snap
                        promotedOnce = false
                    }
                    is RetrofitResult.Fail  -> error.value = res.message
                    is RetrofitResult.Error -> error.value = "네트워크 오류가 발생했어요. 잠시 후 다시 시도해주세요."
                }
                loading.value = false
            }
        }
    }

    /** 닫힐 때 1회 저장 (diff → 단일 타입 toggle 순차 호출) */
    suspend fun commit() {
        if (loading.value) return
        loading.value = true
        error.value = null

        val before = serverSnapshot ?: _prefs.value
        val after  = _prefs.value

        val changed = buildList {
            if (before.chat  != after.chat)  add(NotificationTypeModel.CHAT)
            if (before.order != after.order) add(NotificationTypeModel.ORDER)
        }

        try {
            withContext(Dispatchers.IO) {
                for (type in changed) toggleNotification(type)
            }
            serverSnapshot = after
        } catch (t: Throwable) {
            error.value = "설정 저장 중 문제가 발생했어요. 다시 시도해주세요."
        } finally {
            loading.value = false
        }
    }

    // ===== UI 이벤트 (서버 호출 없음) =====

    /** 마스터: OFF → 전부 OFF, ON → (처음 한 번만) 전부 ON */
    fun onMasterClick(checked: Boolean) {
        if (!checked) {
            _prefs.value = _prefs.value.copy(chat = false, order = false)
        } else {
            if (!promotedOnce && !_prefs.value.anyOn) {
                _prefs.value = _prefs.value.copy(chat = true, order = true)
                promotedOnce = true
            }
        }
    }

    fun onChatClick(checked: Boolean)  { _prefs.value = _prefs.value.copy(chat = checked) }
    fun onOrderClick(checked: Boolean) { _prefs.value = _prefs.value.copy(order = checked) }
}