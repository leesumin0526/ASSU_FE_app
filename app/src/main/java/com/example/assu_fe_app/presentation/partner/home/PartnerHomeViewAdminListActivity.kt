package com.example.assu_fe_app.presentation.partner.home

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.ActivityPartnerHomeViewAdminListBinding
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.partner.home.adapter.PartnerAdminListAdapter
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PartnerHomeViewAdminListActivity : BaseActivity<ActivityPartnerHomeViewAdminListBinding>(R.layout.activity_partner_home_view_admin_list) {

    private val partnershipViewModel: PartnershipViewModel by viewModels()
    private lateinit var adapter: PartnerAdminListAdapter
    @Inject lateinit var authTokenLocalStore: AuthTokenLocalStore

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

        val storeName = authTokenLocalStore.getUserName()
        binding.tvAdminListName
        binding.tvPartnershipInfo.setText("$storeName 와(과) 제휴를 체결한 단체 목록이에요.")

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // 어댑터는 한 번만 생성
        adapter = PartnerAdminListAdapter(
            items = mutableListOf(),
            fragmentManager = supportFragmentManager,
            authTokenLocalStore = authTokenLocalStore
        )

        binding.rvPartnerList.layoutManager = LinearLayoutManager(this)
        binding.rvPartnerList.adapter = adapter

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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                partnershipViewModel.getPartnershipAdminListUiState.collect { state ->
                    when (state) {
                        is com.example.assu_fe_app.ui.partnership.PartnershipViewModel.PartnershipAdminListUiState.Success -> {
                            updateList(state.data)
                        }
                        is com.example.assu_fe_app.ui.partnership.PartnershipViewModel.PartnershipAdminListUiState.Fail -> {
                            // TODO: 에러 처리 (토스트 등)
                        }
                        is com.example.assu_fe_app.ui.partnership.PartnershipViewModel.PartnershipAdminListUiState.Error -> {
                            // TODO: 에러 처리
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 전체 조회 API 호출
        partnershipViewModel.getProposalAdminList(isAll = true)
    }

    private fun updateList(items: List<GetProposalAdminListModel>) {
        adapter.updateItems(items)
        binding.tvAdminCount.text = items.size.toString()
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}