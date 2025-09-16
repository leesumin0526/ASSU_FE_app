package com.example.assu_fe_app.presentation.admin.mypage


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
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.databinding.FragmentAdminMypageBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.presentation.common.mypage.MypageViewModel
import com.example.assu_fe_app.ui.profileImage.ProfileImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AdminMypageFragment : BaseFragment<FragmentAdminMypageBinding>(R.layout.fragment_admin_mypage) {

    @Inject
    lateinit var tokenManager: TokenManager
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
        binding.tvAdmAccountName.setText(tokenManager.getUserName())

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

                s.lastLocalPreview?.let { uri ->
                    Glide.with(this@AdminMypageFragment)
                        .load(uri)
                        .into(binding.ivAdmAccountProfileImg) // 프로필 이미지뷰 id 가정: ivAdmProfile
                }

                s.message?.let { msg ->
                    // Snackbar/Toast 중 택1
                    // Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAdmAccountName.setText(tokenManager.getUserName())
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

        binding.tvAdmAccountName.setText(tokenManager.getUserName())

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

