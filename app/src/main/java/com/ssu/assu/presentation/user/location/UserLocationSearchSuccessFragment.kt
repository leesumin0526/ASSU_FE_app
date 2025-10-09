package com.ssu.assu.presentation.user.location

import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentUserLocationSearchSuccessBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.user.location.adapter.UserLocationSearchSuccessAdapter
import com.ssu.assu.ui.map.UserLocationSearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserLocationSearchSuccessFragment :
    BaseFragment<FragmentUserLocationSearchSuccessBinding>(R.layout.fragment_user_location_search_success) {
    private val searchViewModel: UserLocationSearchViewModel by activityViewModels()
    private lateinit var adapter: UserLocationSearchSuccessAdapter

    override fun initObserver() {
        searchViewModel.storeList.observe(viewLifecycleOwner) { storeList ->
            adapter.submitList(storeList)
            Log.d("UserLocationSearchSuccessFragment", "관찰된 데이터: $storeList")
            binding.tvLocationSearchSuccessTitle.text = storeList.size.toString()
        }

        searchViewModel.isEmptyList.observe(viewLifecycleOwner){ isEmpty ->
            if(isEmpty){
                binding.tvLocationSearchSuccessTitle.visibility = View.GONE
                binding.tvLocationSearchSccessTitle.visibility= View.GONE
                binding.rvLocationSearchSuccess.visibility = View.GONE
                binding.llLocationItemEmpty.visibility=  View.VISIBLE
            }
            else {
                binding.tvLocationSearchSuccessTitle.visibility = View.VISIBLE
                binding.tvLocationSearchSccessTitle.visibility= View.VISIBLE
                binding.rvLocationSearchSuccess.visibility = View.VISIBLE
                binding.llLocationItemEmpty.visibility=  View.GONE
            }
        }
    }

    override fun initView() {
        // 1. 어댑터 초기화 (생성자에 리스트를 넣지 않음)
        adapter = UserLocationSearchSuccessAdapter()

        // 2. RecyclerView에 LayoutManager와 Adapter 설정
        binding.rvLocationSearchSuccess.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@UserLocationSearchSuccessFragment.adapter
        }


    }
}