package com.example.assu_fe_app.presentation.user.location

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityLocationSearchBinding
import com.example.assu_fe_app.presentation.base.BaseActivity

class UserLocationSearchActivity :
    BaseActivity<ActivityLocationSearchBinding>(R.layout.activity_location_search) {
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
        binding.etLocationSearch.setOnEditorActionListener { _: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {

                binding.fvLocationSearchRank.visibility = android.view.View.INVISIBLE
                binding.fvLocationSearchSuccess.visibility = android.view.View.VISIBLE
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

    override fun initObserver() {}

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}