package com.example.assu_fe_app.presentation.user.dashboard

import android.R.attr.elevation
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.PopupWindow
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityUserServiceSuggestBinding
import com.example.assu_fe_app.databinding.FragmentServiceSuggestDropDownBinding
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.common.chatting.proposal.ServiceProposalDropDownFragment
import com.example.assu_fe_app.presentation.user.dashboard.adapter.SuggestTargetAdapter

class UserServiceSuggestActivity : BaseActivity<ActivityUserServiceSuggestBinding>(R.layout.activity_user_service_suggest){

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
//        val targetList = resources.getStringArray(R.array.suggest_target).toList()

        binding.btnServiceDropDown.setOnClickListener {
            showDropdownMenu(binding.spinnerTarget)
        }


        // 뒤로가기 버튼
        binding.btnSuggestBack.setOnClickListener {
            finish()
        }

        // 그냥 애초에 이 액티비티를 닫아서 UserSugesstCompleteActivity의 backStack을 UserMainActivity로 만듦.
        binding.btnSuggestComplete.setOnClickListener {
            val intent = Intent(this, UserSuggestCompleteActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun initObserver() {

    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun showDropdownMenu(anchor : View) {
//        val popupBinding = FragmentServiceSuggestDropDownBinding.inflate(layoutInflater, null, false)
//
//        val popupWidth = anchor.width
//
//        val popupWindow = PopupWindow(
//            popupBinding.root,
//            popupWidth,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            true
//        ).apply {
//            elevation = 8f
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            isOutsideTouchable = true
//        }

        val popupBinding = FragmentServiceSuggestDropDownBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(popupBinding.root, WRAP_CONTENT, WRAP_CONTENT, true)

        // 그림자 및 radius 배경 처리
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.GRAY))
        popupWindow.elevation = 10f

        // 드롭다운 항목 클릭 이벤트 처리
        popupBinding.tvSuggestDropTarget1.setOnClickListener {
            binding.tvServiceSelectTarget.text = popupBinding.tvSuggestDropTarget1.text
            popupWindow.dismiss()
        }

        popupBinding.tvSuggestDropTarget2.setOnClickListener {
            binding.tvServiceSelectTarget.text = popupBinding.tvSuggestDropTarget2.text
            popupWindow.dismiss()
        }

        popupBinding.tvSuggestDropTarget3.setOnClickListener {
            binding.tvServiceSelectTarget.text = popupBinding.tvSuggestDropTarget3.text
            popupWindow.dismiss()
        }

        // 버튼 바로 아래에 띄우기
        popupWindow.showAsDropDown(anchor, 0,-160)
    }
}