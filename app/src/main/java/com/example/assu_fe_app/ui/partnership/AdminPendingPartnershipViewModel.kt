package com.example.assu_fe_app.ui.partnership

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.domain.model.partnership.SuspendedPaperModel
import com.example.assu_fe_app.domain.usecase.partnership.DeletePartnershipUseCase
import com.example.assu_fe_app.domain.usecase.partnership.GetSuspendedPapersUseCase
import com.example.assu_fe_app.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AdminPendingPartnershipViewModel @Inject constructor(
    private val getSuspended: GetSuspendedPapersUseCase,
    private val deletePaper : DeletePartnershipUseCase
) : ViewModel() {

    private val _items   = MutableStateFlow<List<SuspendedPaperModel>>(emptyList())
    val items: StateFlow<List<SuspendedPaperModel>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _toast   = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast

    fun load() = viewModelScope.launch {
        _loading.value = true
        when (val res = getSuspended()) {
            is RetrofitResult.Success     -> _items.value = res.data
            is RetrofitResult.Fail     -> _toast.value = res.message ?: "불러오기 실패"
            is RetrofitResult.Error-> _toast.value = "네트워크 오류"
        }
        _loading.value = false
    }

    fun delete(paperId: Long) = viewModelScope.launch {
        when (val res = deletePaper(paperId)) {
            is RetrofitResult.Success     -> _items.value = _items.value.filterNot { it.paperId == paperId }
            is RetrofitResult.Fail     -> _toast.value = res.message ?: "삭제 실패"
            is RetrofitResult.Error-> _toast.value = "네트워크 오류"
        }
    }

    fun consumeToast() { _toast.value = null }
}