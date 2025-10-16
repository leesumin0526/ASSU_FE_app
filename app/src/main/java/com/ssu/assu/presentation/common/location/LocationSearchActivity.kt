package com.ssu.assu.presentation.common.location

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ssu.assu.R
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.databinding.ActivityLocationSearchBinding
import com.ssu.assu.presentation.base.BaseActivity
import com.ssu.assu.ui.map.AdminPartnerKeyWordSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class LocationSearchActivity :
    BaseActivity<ActivityLocationSearchBinding>(R.layout.activity_location_search) {

    @Inject
    lateinit var tokenLocalStore : AuthTokenLocalStore
    companion object {
        const val EXTRA_SELECTED_ADDRESS = "selected_address"
        const val RESULT_CODE_ADDRESS_SELECTED = 1001
    }

    private val searchViewModel : AdminPartnerKeyWordSearchViewModel by viewModels()
    override fun initView() {
        if(tokenLocalStore.getUserRole().equals("PARTNER", ignoreCase = true)){
            binding.etLocationSearch.hint = "찾으시는 관리자가 없나요?"
        } else{
            binding.etLocationSearch.hint="찾으시는 제휴 가게가 없나요?"
        }

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
                    if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 3. enter 입력 시 view 전환
        binding.etLocationSearch.setOnEditorActionListener { keyword: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {

                val keywordText = keyword.text.toString().trim()
                triggerSearch(keywordText)
                true
            } else {
                false
            }
        }

        binding.ivLocationSearchIc.setOnClickListener {
            val keywordText = binding.etLocationSearch.text.toString().trim()
            triggerSearch(keywordText)
        }

        // 4. 검색 취소 버튼 누르면 입력 초기화
        binding.ivLocationSearchCancle.setOnClickListener {
            binding.etLocationSearch.text.clear()
        }
    }


    override fun initObserver() {
        // state만 관찰하여 UI 상태 관리
        searchViewModel.state.observe(this) { state ->
            when (state) {
                "loading" -> {
                    showLoading()
                }
                "success" -> {
                    hideLoading()
                    showSuccessState()
                }
                "fail" -> {
                    hideLoading()
                    showFailState()
                }
            }
        }
    }

    private fun showLoading() {

        binding.tvLoadingText.text = "검색 중..."

        // 로딩 오버레이만 표시, 나머지 숨김
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.fvLocationSearchRank.visibility = View.INVISIBLE
        binding.fvLocationSearchSuccess.visibility = View.INVISIBLE
        binding.clLocationSearchFail.visibility = View.INVISIBLE
    }

    private fun hideLoading() {
        // 로딩 오버레이 숨김
        binding.loadingOverlay.visibility = View.GONE
    }

    private fun showSuccessState() {
        // 검색 성공 시 success fragment 표시
        binding.fvLocationSearchSuccess.visibility = View.VISIBLE
        binding.fvLocationSearchRank.visibility = View.INVISIBLE
        binding.clLocationSearchFail.visibility = View.INVISIBLE
    }

    private fun showFailState() {
        // 사용자 역할에 따른 실패 메시지 설정
        val userRole = tokenLocalStore.getUserRole()
        if (userRole.equals("PARTNER", ignoreCase = true)) {
            binding.tvLocationSearchFailTitle.text = "관리자를 찾지 못했어요!"
            binding.tvLocationSearchFailSubtitle.text = "관리자를 찾지 못해 페이지를 표시할 수 없어요.\n이용에 불편을 드려 죄송합니다."
        } else {
            binding.tvLocationSearchFailTitle.text = "검색결과를 찾지 못했어요!"
            binding.tvLocationSearchFailSubtitle.text = "매장을 찾지 못해 페이지를 표시할 수 없어요.\n이용에 불편을 드려 죄송합니다."
        }

        // 실패 상태 UI 표시
        binding.clLocationSearchFail.visibility = View.VISIBLE
        binding.fvLocationSearchRank.visibility = View.INVISIBLE
        binding.fvLocationSearchSuccess.visibility = View.INVISIBLE
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

    /**
     * 주소 선택 시 결과를 반환하는 메서드
     * 회원가입에서 주소를 선택할 때 사용
     */
    fun returnSelectedAddress(address: String) {
        val resultIntent = Intent().apply {
            putExtra(EXTRA_SELECTED_ADDRESS, address)
        }
        setResult(RESULT_CODE_ADDRESS_SELECTED, resultIntent)
        finish()
    }

    private fun triggerSearch(keywordText: String) {
        if (keywordText.isNotEmpty()) {
            searchViewModel.search(keywordText)
            hideKeyboard()
            binding.fvLocationSearchRank.visibility = View.INVISIBLE
            binding.fvLocationSearchSuccess.visibility = View.VISIBLE
        }
    }
}