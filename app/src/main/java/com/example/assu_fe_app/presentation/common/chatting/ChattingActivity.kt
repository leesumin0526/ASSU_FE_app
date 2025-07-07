package com.example.assu_fe_app.presentation.common.chatting

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityChattingBinding
import com.example.assu_fe_app.presentation.admin.AdminMainActivity
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.common.chatting.adapter.ChattingChatListAdapter
import com.example.assu_fe_app.presentation.common.chatting.adapter.ChattingMessageAdapter
import com.example.assu_fe_app.presentation.common.chatting.proposal.ServiceProposalWritingFragment
import com.example.assu_fe_app.presentation.user.UserMainActivity


class ChattingActivity : BaseActivity<ActivityChattingBinding>(R.layout.activity_chatting) {

    private lateinit var adapter: ChattingMessageAdapter

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

        val messages = listOf(
            ChattingMessageItem.OtherMessage(
                profileImageUrl = "https://example.com/profile.jpg",
                message = "안녕하세요, 제휴 문의 드리고 싶어요!",
                sentAt = "오후 3:50"
            ),
            ChattingMessageItem.MyMessage(
                message = "네! 어떤 내용이신가요?",
                sentAt = "오후 3:52",
                isRead = true
            ),
            ChattingMessageItem.OtherMessage(
                profileImageUrl = "https://example.com/profile.jpg",
                message = "특정 조건에 대한 혜택을 드리고 싶습니다.",
                sentAt = "오후 3:55"
            )
        )

        adapter = ChattingMessageAdapter(messages)
        binding.rvChattingMessageList.adapter = adapter
        binding.rvChattingMessageList.layoutManager = LinearLayoutManager(this)

        binding.ivChattingBack.setOnClickListener {
            navigateToChatting()
        }

        binding.llChattingSent.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.chatting_fragment_container, ServiceProposalWritingFragment())
                .addToBackStack(null)
                .commit()
        }

        supportFragmentManager.setFragmentResultListener("return_reason", this) { _, bundle ->
            val reason = bundle.getString("reason")
            if (reason != null) {
                Log.d("ChattingActivity", "되돌아온 이유: $reason")
                // reason 값: "ivCross", "btnText", "bgImage" 등
            }
        }
    }

    override fun initObserver() {
    }


    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun navigateToChatting() {
        val intent = Intent(this, AdminMainActivity::class.java).apply {
            // 기존 Task 스택 위로 올라가서 중복 생성 방지
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            // BottomNavigationView에 전달할 목적지 ID
            putExtra("nav_dest_id", R.id.adminChattingFragment)
        }
        startActivity(intent)
        finish() // FinishReviewActivity 종료
    }
}