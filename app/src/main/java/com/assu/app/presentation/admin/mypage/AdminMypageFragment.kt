package com.assu.app.presentation.admin.mypage


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.assu.app.R
import com.assu.app.data.local.AuthTokenLocalStore
import com.assu.app.databinding.FragmentAdminMypageBinding
import com.assu.app.presentation.base.BaseFragment
import com.assu.app.presentation.common.login.LoginActivity
import com.assu.app.presentation.user.mypage.UserMypagePrivacyDialogFragment
import com.assu.app.ui.common.mypage.MypageViewModel
import com.assu.app.ui.profileImage.ProfileImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AdminMypageFragment : BaseFragment<FragmentAdminMypageBinding>(R.layout.fragment_admin_mypage) {

    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore

    private val viewModel: MypageViewModel by viewModels()

    private val profileViewModel: ProfileImageViewModel by viewModels()

    // Android 13+ (Tiramisu) 권장 포토 피커
    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { profileViewModel.uploadProfileImage(it) }
    }

    // 하위 버전 폴백 (갤러리)
    private val pickContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileViewModel.uploadProfileImage(it) }
    }

    override fun initView(){
        // UI 초기화
        binding.tvAdmAccountName.setText(authTokenLocalStore.getUserName())

        binding.tvAdmAccountImageEdit.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                pickContent.launch("image/*")
            }
        }
    }

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logoutState.collectLatest { state ->
                when (state) {
                    is MypageViewModel.LogoutState.Done -> navigateToLoginAndClear()
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.profileUi.collectLatest { s ->
                // 1) 서버에서 받은 presigned URL 있으면 표시
                s.remoteUrl?.let { url ->
                    Glide.with(this@AdminMypageFragment)
                        .load(url)
                        .placeholder(R.drawable.img_student)
                        .error(R.drawable.img_student)
                        .into(binding.ivAdmAccountProfileImg)
                }

                // 2) 방금 업로드한 로컬 미리보기 우선 표시 (있으면)
                s.lastLocalPreview?.let { uri ->
                    Glide.with(this@AdminMypageFragment)
                        .load(uri)
                        .into(binding.ivAdmAccountProfileImg)
                }

                // 메시지는 필요 시 Snackbar/Toast
                s.message?.let { msg ->
                    // Log.e("Profile", msg) // 또는 Snackbar/Toast
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)
        val entry = findNavController().getBackStackEntry(R.id.adminMyPageFragment)
        val handle = entry.savedStateHandle

        handle.getLiveData<Boolean>("openPendingDialog")
            .observe(viewLifecycleOwner) { need ->
                if (need == true && childFragmentManager.findFragmentByTag("PendingDialog") == null) {
                    val targetId = handle.get<Long?>("openPendingTargetId")
                    AdminMypagePendingPartnershipDialogFragment
                        .newInstance(targetId)
                        .show(childFragmentManager, "PendingDialog")

                    // 소모
                    handle["openPendingDialog"] = false
                    handle["openPendingTargetId"] = null
                }
            }

        binding.tvAdmAccountName.setText(authTokenLocalStore.getUserName())
        profileViewModel.fetchProfileImage()
        initClick()
    }

    private fun initClick() {
        // 알림 설정
        binding.clAdmAccountComponent1.setOnClickListener {
            AdminMypageAlarmDialogFragment()
                .show(childFragmentManager, "AlarmDialog")
        }

        // 계정관리 페이지 이동
        binding.clAdmAccountComponent2.setOnClickListener {
            findNavController().navigate(
                R.id.action_admin_mypage_to_mypage_account
            )
        }

        // 대기중인 제휴계약서
        binding.clAdmAccountComponent3.setOnClickListener {
            AdminMypagePendingPartnershipDialogFragment()
                .show(childFragmentManager, "PendingPartnershipDialog")
        }

        binding.tvAdmAccountName.setText(authTokenLocalStore.getUserName())

        // 개인정보 처리방침
        binding.clAdmAccountComponent4.setOnClickListener {
            UserMypagePrivacyDialogFragment()
                .show(childFragmentManager, "PrivacyDialog")
        }

        // 고객센터
        binding.clAdmAccountComponent5.setOnClickListener {
            findNavController().navigate(
                R.id.action_admin_mypage_to_inquiry
            )
        }
    }

    private fun navigateToLoginAndClear() {
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}

