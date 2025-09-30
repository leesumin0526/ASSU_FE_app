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

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy

    fun load() = viewModelScope.launch {
        _loading.value = true
        when (val res = getSuspended()) {
            is RetrofitResult.Success -> {
                _items.value = res.data
                android.util.Log.d("AdminPendingVM", "목록 불러오기 성공: ${res.data.size}건")
            }
            is RetrofitResult.Fail -> {
                android.util.Log.d("AdminPendingVM", "불러오기 실패: ${res.message}")
            }
            is RetrofitResult.Error -> {
                android.util.Log.d("AdminPendingVM", "네트워크 오류 발생")
            }
        }
        _loading.value = false
    }

    fun delete(paperId: Long) = viewModelScope.launch {
        _busy.value = true
        when (val res = deletePaper(paperId)) {
            is RetrofitResult.Success -> {
                _items.value = _items.value.filterNot { it.paperId == paperId }
                android.util.Log.d("AdminPendingVM", "삭제 성공: paperId=$paperId")
            }
            is RetrofitResult.Fail -> {
                android.util.Log.d("AdminPendingVM", "삭제 실패: ${res.message}")
            }
            is RetrofitResult.Error -> {
                android.util.Log.d("AdminPendingVM", "네트워크 오류 발생 (삭제)")
            }
        }
        _busy.value = false
    }
}