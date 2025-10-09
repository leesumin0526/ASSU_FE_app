package com.ssu.assu.ui.inquiry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.domain.model.inquiry.InquiryModel
import com.ssu.assu.domain.usecase.inquiry.CreateInquiryUseCase
import com.ssu.assu.domain.usecase.inquiry.GetInquiriesUseCase
import com.ssu.assu.domain.usecase.inquiry.GetInquiryDetailUseCase
import com.ssu.assu.util.RetrofitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class InquiryViewModel @Inject constructor(
    private val createInquiry: CreateInquiryUseCase,
    private val getInquiries: GetInquiriesUseCase,
    private val getInquiryDetail: GetInquiryDetailUseCase
) : ViewModel() {

    data class ListState(
        val items: List<InquiryModel> = emptyList(),
        val page: Int = 1,
        val size: Int = 20,
        val totalPages: Int = 1,
        val loading: Boolean = false,
        val error: String? = null,
        val status: String = "all"
    )

    private val _list = MutableStateFlow(ListState())
    val list: StateFlow<ListState> = _list

    private val _createResult = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val createResult: SharedFlow<Long> = _createResult

    private val _detail = MutableStateFlow<InquiryModel?>(null)
    val detail: StateFlow<InquiryModel?> = _detail

    private val _creating = MutableStateFlow(false)
    val creating: StateFlow<Boolean> = _creating

    private val _detailLoading = MutableStateFlow(false)
    val detailLoading: StateFlow<Boolean> = _detailLoading

    fun refresh(status: String = _list.value.status, size: Int = _list.value.size) =
        load(status, page = 1, size = size, reset = true)

    fun loadMore() {
        val st = _list.value
        if (st.loading || st.page > st.totalPages) return
        load(st.status, st.page, st.size, reset = false)
    }

    private fun load(status: String, page: Int, size: Int, reset: Boolean) = viewModelScope.launch {
        val cur = _list.value
        _list.value = cur.copy(loading = true, error = null, status = status)

        when (val res = getInquiries(status, page, size)) {
            is RetrofitResult.Success -> {
                val p = res.data
                val merged = if (reset || page == 1) p.items else cur.items + p.items
                val nextPage = if (p.page < p.totalPages) p.page + 1 else p.page
                _list.value = cur.copy(
                    items = merged,
                    page = nextPage,
                    size = p.size,
                    totalPages = p.totalPages,
                    loading = false,
                    error = null,
                    status = status
                )
            }
            is RetrofitResult.Fail -> _list.value = cur.copy(loading = false, error = res.message)
            is RetrofitResult.Error -> _list.value = cur.copy(loading = false, error = "네트워크 오류")
        }
    }

    fun create(title: String, content: String, email: String) = viewModelScope.launch {
        when (val res = createInquiry(title, content, email)) {
            is RetrofitResult.Success -> {
                _createResult.tryEmit(res.data)  // 새로 생성된 id
                refresh(_list.value.status)      // 리스트 새로고침
            }
            is RetrofitResult.Fail -> { /* 에러 토스트/상태 처리 */ }
            is RetrofitResult.Error -> { /* 네트워크 오류 처리 */ }
        }
    }

    fun loadDetail(id: Long) = viewModelScope.launch {
        _detailLoading.value = true
        when (val res = getInquiryDetail(id)) {
            is RetrofitResult.Success -> _detail.value = res.data
            else -> _detail.value = null
        }
        _detailLoading.value = false
    }
}