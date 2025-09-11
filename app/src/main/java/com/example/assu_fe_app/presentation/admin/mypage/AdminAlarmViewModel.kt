package com.example.assu_fe_app.presentation.admin.mypage

import androidx.lifecycle.ViewModel
import com.example.assu_fe_app.domain.model.notification.NotificationTypeModel
import com.example.assu_fe_app.domain.usecase.notification.GetNotificationSettingsUseCase
import com.example.assu_fe_app.domain.usecase.notification.ToggleNotificationUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

data class NotificationPrefs(
    val chat: Boolean = true,
    val suggestion: Boolean = true,
    val proposal: Boolean = true
) {
    val anyOn: Boolean get() = chat || suggestion || proposal
}

@HiltViewModel
class AdminAlarmViewModel @Inject constructor(
    private val getSettings: GetNotificationSettingsUseCase,
    private val toggle: ToggleNotificationUseCase
) : ViewModel() {

    private val _prefs = MutableStateFlow(NotificationPrefs())
    val prefs: StateFlow<NotificationPrefs> = _prefs.asStateFlow()

    val master: StateFlow<Boolean> =
        prefs.map { it.anyOn }.stateIn(CoroutineScope(Dispatchers.Main.immediate), SharingStarted.Lazily, true)

    val loading = MutableStateFlow(false)
    val error   = MutableStateFlow<String?>(null)

    private var serverSnapshot: NotificationPrefs? = null

    // “처음 한 번만 승격”용 플래그
    private var promotedOnce: Boolean = false

    /** 화면 진입 시 1회 서버 로드 */
    fun load() {
        if (loading.value) return
        loading.value = true
        error.value = null

        // viewModelScope 없이도 동작하지만, 로드 자체는 뷰모델 생명주기 내에서 안전
        CoroutineScope(Dispatchers.IO).launch {
            val res = getSettings()
            withContext(Dispatchers.Main) {
                when (res) {
                    is RetrofitResult.Success -> {
                        val snap = NotificationPrefs(
                            chat       = res.data.chat,
                            suggestion = res.data.suggestion,
                            proposal   = res.data.proposal
                        )
                        serverSnapshot = snap
                        _prefs.value = snap
                        // 초기 상태 기록: anyOn=false면 마스터 OFF로 시작
                        promotedOnce = false
                    }
                    is RetrofitResult.Fail -> error.value = res.message
                    is RetrofitResult.Error -> error.value = "네트워크 오류가 발생했어요. 잠시 후 다시 시도해주세요."
                }
                loading.value = false
            }
        }
    }

    /**
     * 화면 종료 시 1회 서버 반영 (suspend)
     * - 내부에서 launch 금지! 호출자가 await할 수 있어야 함
     */
    suspend fun commit() {
        if (loading.value) return
        loading.value = true
        error.value = null

        val before = serverSnapshot ?: _prefs.value
        val after  = _prefs.value

        val changedTypes = buildList {
            if (before.chat       != after.chat)       add(NotificationTypeModel.CHAT)
            if (before.suggestion != after.suggestion) add(NotificationTypeModel.PARTNER_SUGGESTION)
            if (before.proposal   != after.proposal)   add(NotificationTypeModel.PARTNER_PROPOSAL)
        }

        try {
            // 순차 호출(서버 토글 레이스 방지)
            withContext(Dispatchers.IO) {
                for (type in changedTypes) {
                    toggle(type)
                }
            }
            serverSnapshot = after
        } catch (t: Throwable) {
            error.value = "설정 저장 중 문제가 발생했어요. 다시 시도해주세요."
        } finally {
            loading.value = false
        }
    }

    // ========== UI 이벤트(서버 호출 없음) ==========

    /** 마스터 클릭: false → 전부 OFF, true → (처음 한 번만) 전부 ON 승격 */
    fun onMasterClick(checked: Boolean) {
        if (!checked) {
            _prefs.value = _prefs.value.copy(chat = false, suggestion = false, proposal = false)
            // OFF로 내릴 때는 승격 여부와 무관
        } else {
            // 처음 시작이 모두 OFF였고, 아직 승격하지 않았다면 한 번만 승격
            if (!promotedOnce && !_prefs.value.anyOn) {
                _prefs.value = _prefs.value.copy(chat = true, suggestion = true, proposal = true)
                promotedOnce = true
            }
            // 그 외에는 하위 유지(변경 없음)
        }
    }

    fun onChatClick(checked: Boolean)       { _prefs.value = _prefs.value.copy(chat = checked) }
    fun onSuggestionClick(checked: Boolean) { _prefs.value = _prefs.value.copy(suggestion = checked) }
    fun onProposalClick(checked: Boolean)   { _prefs.value = _prefs.value.copy(proposal = checked) }
}