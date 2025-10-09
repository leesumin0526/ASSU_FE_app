package com.ssu.assu.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.domain.model.suggestion.SuggestionModel
import com.ssu.assu.domain.usecase.suggestion.GetSuggestionsUseCase
import com.ssu.assu.util.onFail
import com.ssu.assu.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminSuggestionsViewModel @Inject constructor(
    private val getSuggestionsUseCase: GetSuggestionsUseCase
) : ViewModel() {

    sealed interface SuggestionsUiState {
        data object Loading : SuggestionsUiState
        data class Success(val data: List<SuggestionModel>) : SuggestionsUiState
        data class Error(val message: String) : SuggestionsUiState
    }

    private val _suggestionsState = MutableStateFlow<SuggestionsUiState>(SuggestionsUiState.Loading)
    val suggestionsState: StateFlow<SuggestionsUiState> = _suggestionsState.asStateFlow()

    init {
        fetchSuggestions()
    }

    private fun fetchSuggestions() {
        viewModelScope.launch {
            _suggestionsState.value = SuggestionsUiState.Loading
            getSuggestionsUseCase()
                .onSuccess { suggestions ->
                    _suggestionsState.value = SuggestionsUiState.Success(suggestions)
                }
                .onFail { code ->
                    _suggestionsState.value = SuggestionsUiState.Error("오류가 발생했습니다.")
                }
        }
    }
}