// com/example/assu_fe_app/ui/profileImage/ProfileImageViewModel.kt
package com.example.assu_fe_app.ui.profileImage

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.domain.usecase.profileImage.UploadOrReplaceProfileImageUseCase
import com.example.assu_fe_app.util.RetrofitResult
import com.example.assu_fe_app.util.toMultipartPart
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileImageViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val uploadOrReplaceProfileImage: UploadOrReplaceProfileImageUseCase
) : ViewModel() {

    data class ProfileUiState(
        val loading: Boolean = false,
        val lastLocalPreview: Uri? = null,
        val uploadedKey: String? = null,
        val message: String? = null
    )
    private val _profileUi = MutableStateFlow(ProfileUiState())
    val profileUi: StateFlow<ProfileUiState> = _profileUi

    fun uploadProfileImage(uri: Uri) = viewModelScope.launch {
        _profileUi.update { it.copy(loading = true, lastLocalPreview = uri, message = null) }

        val part = uri.toMultipartPart(
            context = appContext,
            partName = "image",
            fallbackFileName = "profile.jpg"
        )

        when (val res = uploadOrReplaceProfileImage(part)) {
            is RetrofitResult.Success -> _profileUi.update {
                it.copy(loading = false, uploadedKey = res.data.key, message = "프로필 사진이 업데이트되었습니다.")
            }
            is RetrofitResult.Fail -> _profileUi.update {
                it.copy(loading = false, message = res.message ?: "업로드에 실패했습니다.")
            }
            is RetrofitResult.Error -> _profileUi.update {
                it.copy(loading = false, message = "네트워크 오류가 발생했습니다.")
            }
        }
    }
}