package com.assu.app.presentation.common.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.assu.app.databinding.ActivityNotificationBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    enum class Role { ADMIN, PARTNER }

    private lateinit var binding: ActivityNotificationBinding
    private val vm: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val role = intent.getSerializableExtra(EXTRA_ROLE) as? Role ?: Role.PARTNER

        binding.vpPartnerNoti.adapter = NotificationPagerAdapter(this, role)
        TabLayoutMediator(binding.tlPartnerNoti, binding.vpPartnerNoti) { tab, pos ->
            tab.text = if (pos == 0) "전체" else "안읽음"
        }.attach()

        binding.icPartnerNotiBack.setOnClickListener { finish() }

        // 프래그먼트가 올린 “안읽음으로 전환” 요청을 Activity가 받아서 탭 전환
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.switchToUnread.collect {
                    binding.vpPartnerNoti.currentItem = 1 // 0: 전체, 1: 안읽음
                }
            }
        }
    }

    companion object {
        private const val EXTRA_ROLE = "extra_role"
        fun start(context: Context, role: Role) {
            context.startActivity(Intent(context, NotificationActivity::class.java).apply {
                putExtra(EXTRA_ROLE, role) // ✅ 여기서 반드시 실어 보냄
            })
        }
    }
}