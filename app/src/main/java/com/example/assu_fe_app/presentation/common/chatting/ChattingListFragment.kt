package com.example.assu_fe_app.presentation.common.chatting

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.chatting.ChattingListItem
import com.example.assu_fe_app.databinding.FragmentChattingListBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.adapter.ChattingChatListAdapter


class ChattingListFragment : BaseFragment<FragmentChattingListBinding> (R.layout.fragment_chatting_list){

    private lateinit var adapter: ChattingChatListAdapter

    override fun initObserver() {
        val dummyList = List(15) {
            ChattingListItem(
                "roomId1",
                "제휴 가능할까요?",
                "12:00",
                3,
                        R.drawable.ic_restaurant_ex,
                "인쌩맥주 숭실대점",
            )
        }

        adapter = ChattingChatListAdapter(dummyList) { item ->
            val intent = Intent(requireContext(), ChattingActivity::class.java).apply {
                putExtra("CHAT_NAME", item.opponentName)
                putExtra("LAST_CHAT", item.lastMessage)
                putExtra("CHAT", item.profileImage)
            }
            startActivity(intent)
        }
        binding.rvChattingChatList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChattingChatList.adapter = adapter
    }

    override fun initView() {
    }


}