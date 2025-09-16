package com.example.assu_fe_app.presentation.partner.home

import android.app.Fragment
import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityPartnerHomeViewAdminListBinding
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.partner.home.adapter.PartnerAdminListAdapter

class PartnerHomeViewAdminListActivity : BaseActivity<ActivityPartnerHomeViewAdminListBinding>(R.layout.activity_partner_home_view_admin_list) {

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

        binding.ivAdminListBack.setOnClickListener {
            finish() // 현재 Activity 종료 → 이전 화면으로 돌아감
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {


//        val adapter = PartnerAdminListAdapter(, supportFragmentManager)
        binding.rvPartnerList.layoutManager = LinearLayoutManager(this)
//        binding.rvPartnerList.adapter = adapter

        // 아이템 간 여백 설정 (20dp)
        binding.rvPartnerList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                if (position != 0) {
                    outRect.top = (20 * resources.displayMetrics.density).toInt()
                }
            }
        })
    }


    override fun initObserver() {
        // 옵저버 필요한 경우 작성
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}