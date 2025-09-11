package com.example.assu_fe_app.presentation.admin.dashboard

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityAdminDashboardSuggestionsBinding
import com.example.assu_fe_app.presentation.admin.dashboard.adapter.AdminSuggestionItem
import com.example.assu_fe_app.presentation.admin.dashboard.adapter.AdminSuggestionListAdapter
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.ui.admin.AdminSuggestionsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminDashboardSuggestionsActivity : BaseActivity<ActivityAdminDashboardSuggestionsBinding>(R.layout.activity_admin_dashboard_suggestions) {

    private val viewModel: AdminSuggestionsViewModel by viewModels()
    private val adapter = AdminSuggestionListAdapter()

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3 // 8dp 추가
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        binding.ivSuggestionListBack.setOnClickListener {
            finish() // 현재 Activity 종료 → 이전 화면으로 돌아감
        }

        binding.rvSuggestionList.adapter = adapter
        binding.rvSuggestionList.layoutManager = LinearLayoutManager(this)

        // 아이템 간 여백 설정 (20dp)
        binding.rvSuggestionList.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                viewModel.suggestionsState.collect { state ->
                    when (state) {
                        is AdminSuggestionsViewModel.SuggestionsUiState.Loading -> {}
                        is AdminSuggestionsViewModel.SuggestionsUiState.Success -> {
                            adapter.submitList(state.data)
                            binding.tvSuggestionCount.text = state.data.size.toString()
                        }
                        is AdminSuggestionsViewModel.SuggestionsUiState.Error -> {
                            Toast.makeText(this@AdminDashboardSuggestionsActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}