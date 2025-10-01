package com.example.assu_fe_app.presentation.user.location

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityUserLocationSearchBinding
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.ui.map.UserLocationSearchViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserLocationSearchActivity :
    BaseActivity<ActivityUserLocationSearchBinding>(R.layout.activity_user_location_search) {

    private val searchViewModel: UserLocationSearchViewModel by viewModels()

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

        // 1. 뒤로가기 버튼
        binding.fragmentLocationSearchLeftArrow.setOnClickListener {
            finish()
        }

        // 2. editText 입력 시 삭제 아이콘 보이게
        binding.etLocationSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.ivLocationSearchCancle.visibility =
                    if (s.isNullOrEmpty()) android.view.View.GONE else android.view.View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 3. enter 입력 시 view 전환
        binding.etLocationSearch.setOnEditorActionListener { keyword: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val keyword = keyword.text.toString().trim()
                Log.d("UserLocationSearchActivity", "검색어: $keyword")

                searchViewModel.getStoreListByKeyword(keyword)
                hideKeyboard()
                true
            } else {
                false
            }
        }

        // 4. 검색 취소 버튼 누르면 입력 초기화
        binding.ivLocationSearchCancle.setOnClickListener {
            binding.etLocationSearch.text.clear()
        }
    }

    override fun initObserver() {
        searchViewModel.state.observe(this){ state ->
            when (state) {
                "loading" -> {
                    showLoading()
                }
                "success" -> {
                    hideLoading()
                    showSuccessState()
                }
                else -> {
                    hideLoading()
                }
            }

        }
    }

    private fun showLoading() {
        binding.tvLoadingText.text = "검색 중..."

        // 로딩 오버레이만 표시, 나머지 숨김
        binding.loadingOverlay.visibility = android.view.View.VISIBLE
        binding.fvLocationSearchRank.visibility = android.view.View.INVISIBLE
        binding.fvLocationSearchSuccess.visibility = android.view.View.INVISIBLE
        binding.clLocationSearchFail.visibility = android.view.View.INVISIBLE
    }

    private fun hideLoading() {
        // 로딩 오버레이 숨김
        binding.loadingOverlay.visibility = android.view.View.GONE
    }

    private fun showSuccessState() {
        // 검색 성공 시 success fragment 표시
        // Fragment에서 데이터 유무에 따라 리스트/빈 상태 처리
        binding.fvLocationSearchSuccess.visibility = android.view.View.VISIBLE
        binding.fvLocationSearchRank.visibility = android.view.View.INVISIBLE
        binding.clLocationSearchFail.visibility = android.view.View.INVISIBLE
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}