package com.example.assu_fe_app.presentation.partner

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
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
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            val extraPaddingTop = 3
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                0
            )

            // 바텀 네비게이션 높이 퍼센트 동적 계산
            val screenHeight = resources.displayMetrics.heightPixels / resources.displayMetrics.density
            val baseBottomNavHeight = 69f // 기본 높이
            val systemNavHeightDp = navigationBars.bottom / resources.displayMetrics.density
            val totalBottomNavHeight = baseBottomNavHeight + systemNavHeightDp
            val newHeightPercent = totalBottomNavHeight / screenHeight

            // 바텀 네비게이션 높이 퍼센트 적용
            val layoutParams = binding.bottomNavigationView.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.matchConstraintPercentHeight = newHeightPercent
            binding.bottomNavigationView.layoutParams = layoutParams

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

    fun hideBottomNavigation(){
        binding.bottomNavigationView.visibility = View.GONE
    }

    fun showBottomNavigation(){
        binding.bottomNavigationView.visibility= View.VISIBLE
    }
}