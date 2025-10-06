//package com.assu.app.presentation.user.home
//
//import android.content.Context
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.assu_fe_app.R
//import com.example.assu_fe_app.presentation.base.BaseActivity
//import com.example.assu_fe_app.databinding.ActivityUserPartnershipVerifyBinding
//
//class UserPartnershipVerifyActivity : BaseActivity<ActivityUserPartnershipVerifyBinding>(R.layout.activity_user_partnership_verify){
//    override fun initView() {
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            val extraPaddingTop = 3
//            v.setPadding(
//                systemBars.left,
//                systemBars.top + extraPaddingTop.dpToPx(v.context),
//                systemBars.right,
//                systemBars.bottom
//            )
//            insets
//        }
//
//        val fragment = UserGroupVerifyFragment()
//
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.user_verify_fragment_container, fragment)
//            .commit()
//
//    }
//
//    private fun Int.dpToPx(context: Context): Int {
//        return (this * context.resources.displayMetrics.density).toInt()
//    }
//
//    override fun initObserver() {
//
//    }
//}