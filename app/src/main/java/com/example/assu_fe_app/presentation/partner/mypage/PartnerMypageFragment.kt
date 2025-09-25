package com.example.assu_fe_app.presentation.partner.mypage

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
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentPartnerMypageBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.presentation.user.mypage.UserMypagePrivacyDialogFragment
import com.example.assu_fe_app.ui.common.mypage.MypageViewModel
import com.example.assu_fe_app.ui.profileImage.ProfileImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PartnerMypageFragment
    : BaseFragment<FragmentPartnerMypageBinding>(R.layout.fragment_partner_mypage) {

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
        binding.tvPartnerAccountName.setText(authTokenLocalStore.getUserName())

        binding.tvPartnerAccountImageEdit.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                pickContent.launch("image/*")
            }
        }
        profileViewModel.fetchProfileImage()
        binding.tvPartnerAccountName.setText(authTokenLocalStore.getUserName())
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
                    Glide.with(this@PartnerMypageFragment)
                        .load(url)
                        .placeholder(R.drawable.img_partner) // 선택
                        .error(R.drawable.img_partner)        // 선택
                        .into(binding.ivPartnerAccountProfileImg)
                }

                // 2) 방금 업로드한 로컬 미리보기 우선 표시 (있으면)
                s.lastLocalPreview?.let { uri ->
                    Glide.with(this@PartnerMypageFragment)
                        .load(uri)
                        .into(binding.ivPartnerAccountProfileImg)
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
        binding.tvPartnerAccountName.setText(authTokenLocalStore.getUserName())
        initClickListeners()
    }

    private fun initClickListeners() {
        // 알림 설정으로 이동
        binding.clPartnerAccountComponent1.setOnClickListener {
            PartnerMypageAlarmDialogFragment()
                .show(childFragmentManager, "AlarmDialog")
        }

        // 계정관리로 이동
        binding.clPartnerAccountComponent2.setOnClickListener {
            findNavController().navigate(
                R.id.action_partner_mypage_to_mypage_account
            )
        }

        // 개인정보 처리방침으로 이동
        binding.clPartnerAccountComponent4.setOnClickListener {
            UserMypagePrivacyDialogFragment()
                .show(childFragmentManager, "PrivacyDialog")
        }

        // 고객센터로 이동
        binding.clPartnerAccountComponent5.setOnClickListener {
            findNavController().navigate(
                R.id.action_partner_mypage_to_inquiry
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