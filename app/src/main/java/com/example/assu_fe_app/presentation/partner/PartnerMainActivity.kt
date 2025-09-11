package com.example.assu_fe_app.presentation.partner

import android.content.Context
import android.content.Intent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityPartnerMainBinding
import com.example.assu_fe_app.fcm.TtsManager
import com.example.assu_fe_app.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PartnerMainActivity : BaseActivity<ActivityPartnerMainBinding>(R.layout.activity_partner_main) {

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3 // 8dp 추가
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                0
            )
            insets
        }

        initBottomNavigation()

        handleNavIntent(intent)
        TtsManager.init(this)
    }

    override fun initObserver() {
        // 옵저버 필요한 경우 작성
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNavIntent(intent)
    }

    private fun handleNavIntent(intent: Intent) {
        val destId = intent.getIntExtra("nav_dest_id", -1)
        if (destId != -1) {
            // BottomNavigationView 에서 해당 메뉴 아이템을 선택하면
            // NavigationUI 가 알아서 navController.navigate(destId) 해 줍니다.
            binding.bottomNavigationView.selectedItemId = destId
        }
    }

    private fun initBottomNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}