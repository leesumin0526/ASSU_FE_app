package com.ssu.assu.presentation.user.home

import android.content.Intent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssu.assu.R
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.databinding.FragmentUserHomeBinding
import com.ssu.assu.domain.model.dashboard.PopularStoreModel
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.ui.user.UserHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserHomeFragment :
    BaseFragment<FragmentUserHomeBinding>(R.layout.fragment_user_home){

    private val viewModel: UserHomeViewModel by viewModels()

    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stampState.collect { state ->
                when (state) {
                    is UserHomeViewModel.StampUiState.Idle -> {
                        // 초기 상태
                    }
                    is UserHomeViewModel.StampUiState.Loading -> {
                        // 로딩 상태 (필요시 로딩 UI 표시)
                    }
                    is UserHomeViewModel.StampUiState.Success -> {
                        updateStampDisplay(state.stampCount)
                    }
                    is UserHomeViewModel.StampUiState.Error -> {
                        // 에러 발생 시 기본값 표시 또는 에러 처리
                        updateStampDisplay(0)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.popularStoresState.collect { state ->
                when (state) {
                    is UserHomeViewModel.PopularStoresUiState.Idle -> {
                        // 초기 상태
                    }
                    is UserHomeViewModel.PopularStoresUiState.Loading -> {
                        showLoading()
                    }
                    is UserHomeViewModel.PopularStoresUiState.Success -> {
                        hideLoading()
                        setupRankingGrid(state.stores)
                    }
                    is UserHomeViewModel.PopularStoresUiState.Error -> {
                        hideLoading()
                        setupRankingGrid(emptyList())
                    }
                }
            }
        }
    }

    override fun initView() {
        val name = authTokenLocalStore.getUserName()
        binding.tvHome1.text = "안녕하세요, ${name}님!"

        // 제휴 QR 박스 클릭 시 인증 액티비티로 이동
        binding.clHomeQrBox.setOnClickListener {
            val intent = Intent(requireContext(), UserQRVerifyActivity::class.java)
            startActivity(intent)
        }

        binding.tvSeeMoreMyStamp.setOnClickListener {
            navigateToMyPartnershipDetails()
        }
        binding.ivSeeMoreMyStamp.setOnClickListener {
            navigateToMyPartnershipDetails()
        }

        // 오늘 날짜 업데이트
        binding.tvTodayUpdateDateAndTime.text = getCurrentDateString()

        // 인기매장 데이터 로드
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadPopularStores()
        }
    }

    private fun setupRankingGrid(popularStores: List<PopularStoreModel>) {
        val gridLayout = binding.gridRanking
        gridLayout.removeAllViews()

        if (popularStores.isEmpty()) {
            val context = requireContext()
            val noDataTextView = TextView(context).apply {
                text = "아직 인기매장 데이터가 없어요"
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.assu_font_sub))
                gravity = android.view.Gravity.CENTER
                layoutParams = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = (80 * resources.displayMetrics.density).toInt()
                    columnSpec = androidx.gridlayout.widget.GridLayout.spec(0, 2)
                    rowSpec = androidx.gridlayout.widget.GridLayout.spec(0, 4)
                }
            }
            gridLayout.addView(noDataTextView)
            return
        }

        // 순서대로 추가하면 GridLayout이 2열로 설정되어 있어서 자동으로 1-5, 2-6, 3-7, 4-8 배치
        popularStores.take(8).forEach { store ->
            val itemView = createRankingItem(store)
            gridLayout.addView(itemView)
        }
    }

    private fun createRankingItem(store: PopularStoreModel): LinearLayout {
        val context = requireContext()
        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            layoutParams = androidx.gridlayout.widget.GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = androidx.gridlayout.widget.GridLayout.spec(androidx.gridlayout.widget.GridLayout.UNDEFINED, 1f)
                setMargins(0, 0, 0, (12 * resources.displayMetrics.density).toInt())
            }
        }

        val rankTextView = TextView(context).apply {
            text = store.rank.toString()
            textSize = 14f
            setTextColor(
                if (store.isHighlight)
                    ContextCompat.getColor(context, R.color.assu_main)
                else
                    ContextCompat.getColor(context, R.color.assu_font_main)
            )
        }

        val storeTextView = TextView(context).apply {
            text = store.storeName
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = (8 * resources.displayMetrics.density).toInt()
            }
        }

        linearLayout.addView(rankTextView)
        linearLayout.addView(storeTextView)

        return linearLayout
    }

    private fun updateStampDisplay(stampCount: Int) {
        // 모든 스탬프 ImageView ID 리스트
        val stampIds = listOf(
            R.id.iv_home_stamp1, R.id.iv_home_stamp2, R.id.iv_home_stamp3,
            R.id.iv_home_stamp4, R.id.iv_home_stamp5, R.id.iv_home_stamp6,
            R.id.iv_home_stamp7, R.id.iv_home_stamp8, R.id.iv_home_stamp9,
            R.id.iv_home_stamp10
        )

        // 모든 스탬프를 먼저 빈 상태로 초기화
        stampIds.forEach { id ->
            val imageView = binding.root.findViewById<ImageView>(id)
            imageView?.setImageResource(R.drawable.ic_home_stamp) // 빈 스탬프 이미지
        }

        // stampCount만큼 채워진 스탬프로 변경
        val actualStampCount = minOf(stampCount, 10) // 최대 10개로 제한
        for (i in 0 until actualStampCount) {
            val imageView = binding.root.findViewById<ImageView>(stampIds[i])
            imageView?.setImageResource(R.drawable.ic_home_stamp_filled) // 채워진 스탬프 이미지
        }
    }

    private fun getCurrentDateString(): String {
        val formatter = java.text.SimpleDateFormat("yyyy년 MM월 dd일 HH:mm 기준", java.util.Locale.KOREAN)
        return formatter.format(java.util.Date())
    }

    private fun navigateToMyPartnershipDetails() {
        findNavController().navigate(R.id.myPartnershipDetailsFragment)
    }

    private fun showLoading() {
        // 로딩 UI 표시
    }

    private fun hideLoading() {
        // 로딩 UI 숨김
    }

}