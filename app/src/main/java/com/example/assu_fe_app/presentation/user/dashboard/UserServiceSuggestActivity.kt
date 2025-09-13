package com.example.assu_fe_app.presentation.user.dashboard

import android.R.attr.elevation
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityUserServiceSuggestBinding
import com.example.assu_fe_app.databinding.FragmentServiceSuggestDropDownBinding
import com.example.assu_fe_app.domain.model.suggestion.SuggestionTargetModel
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.common.chatting.proposal.ServiceProposalDropDownFragment
import com.example.assu_fe_app.presentation.user.dashboard.adapter.SuggestTargetAdapter
import com.example.assu_fe_app.ui.suggestion.SuggestionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserServiceSuggestActivity : BaseActivity<ActivityUserServiceSuggestBinding>(R.layout.activity_user_service_suggest){

    private val viewModel: SuggestionViewModel by viewModels()
    private var suggestionTargets: List<SuggestionTargetModel> = emptyList()

    private var dropdownWindow: PopupWindow? = null

    override fun initView() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

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

        activateCompleteButton()

        binding.spinnerTarget.setOnClickListener {
            if (dropdownWindow?.isShowing == true) {
                dropdownWindow?.dismiss()
            } else {
                showDropdownMenu(it, suggestionTargets)
            }
        }

        // 뒤로가기 버튼
        binding.btnSuggestBack.setOnClickListener {
            finish()
        }

        // 그냥 애초에 이 액티비티를 닫아서 UserSugesstCompleteActivity의 backStack을 UserMainActivity로 만듦.
        binding.btnSuggestComplete.setOnClickListener {
            viewModel.writeSuggestion()
            val intent = Intent(this, UserSuggestCompleteActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getAdminsState.collect { state ->
                        Log.d("SuggestActivity", "getAdminsState changed: $state")

                        when (state) {
                            is SuggestionViewModel.GetAdminsUiState.Success -> {
                                suggestionTargets = state.data
                            }
                            is SuggestionViewModel.GetAdminsUiState.Fail -> {
                                Toast.makeText(this@UserServiceSuggestActivity, state.message, Toast.LENGTH_SHORT).show()
                            }
                            is SuggestionViewModel.GetAdminsUiState.Error -> {
                                Toast.makeText(this@UserServiceSuggestActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun activateCompleteButton() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input1 = binding.etSuggestMarket.text.toString().trim()
                val input2 = binding.etSuggestWantBenefit.text.toString().trim()

                val isFilled = input1.isNotEmpty() && input2.isNotEmpty()

                binding.btnSuggestComplete.isEnabled = isFilled

                if (isFilled) {
                    binding.btnSuggestComplete.setBackgroundResource(R.drawable.btn_basic_selected)
                } else {
                    binding.btnSuggestComplete.setBackgroundResource(R.drawable.btn_basic_unselected)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etSuggestMarket.addTextChangedListener(textWatcher)
        binding.etSuggestWantBenefit.addTextChangedListener(textWatcher)

        // 초기 상태
        binding.btnSuggestComplete.isEnabled = false
        binding.btnSuggestComplete.setBackgroundResource(R.drawable.btn_basic_unselected)
    }


    private fun showDropdownMenu(anchor : View, targets: List<SuggestionTargetModel>) {

        val popupBinding = FragmentServiceSuggestDropDownBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            popupBinding.root,
            anchor.width,
            WRAP_CONTENT,
            true
        ).apply {
            elevation = 10f
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        popupWindow.setOnDismissListener {
            dropdownWindow = null
        }

        val textViews = listOf(
            popupBinding.tvSuggestDropTarget1,
            popupBinding.tvSuggestDropTarget2,
            popupBinding.tvSuggestDropTarget3
        )
        val dividers = listOf(popupBinding.lineDivide1, popupBinding.lineDivide2)

        textViews.forEach { it.visibility = View.GONE }
        dividers.forEach { it.visibility = View.GONE }

        targets.forEachIndexed { index, target ->
            if (index < textViews.size) {
                val textView = textViews[index]
                textView.visibility = View.VISIBLE
                textView.text = target.name

                textView.setOnClickListener {
                    viewModel.selectTarget(target)
                    popupWindow.dismiss()
                }
                if (index < targets.size - 1 && index < dividers.size) {
                    dividers[index].visibility = View.VISIBLE
                }
            }
        }

        popupWindow.showAsDropDown(anchor, -5, -155)

        this.dropdownWindow = popupWindow
    }
}