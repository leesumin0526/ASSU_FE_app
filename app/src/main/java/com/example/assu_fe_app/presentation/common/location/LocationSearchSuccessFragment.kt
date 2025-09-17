package com.example.assu_fe_app.presentation.common.location

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.UserRole
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentLocationSearchSuccessBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.location.adapter.AdminPartnerLocationAdapter
import com.example.assu_fe_app.presentation.common.location.adapter.LocationSharedViewModel
import com.example.assu_fe_app.ui.map.AdminPartnerKeyWordSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LocationSearchSuccessFragment :
    BaseFragment<FragmentLocationSearchSuccessBinding>(R.layout.fragment_location_search_success) {
    @Inject lateinit var authTokenLocalStore: AuthTokenLocalStore

    private val sharedViewModel: LocationSharedViewModel by viewModels()
    private val searchViewModel : AdminPartnerKeyWordSearchViewModel by activityViewModels()

    private lateinit var adapter: AdminPartnerLocationAdapter
    private lateinit var role: UserRole


    override fun initObserver() {

        searchViewModel.contentList.observe(viewLifecycleOwner){ contentList ->
            adapter.submitList(contentList)
            Log.d("LocationSearchSuccessFragment", "관찰된 데이터: $contentList")
            binding.tvLocationSearchSuccessCount.text = contentList.size.toString()

        }
        searchViewModel.isEmptyList.observe(viewLifecycleOwner){ isEmpty ->
            if(isEmpty){
                binding.tvLocationSearchSuccessTitle.visibility = View.GONE
                binding.tvLocationSearchSuccessCount.visibility= View.GONE
                binding.rvLocationSearchSuccess.visibility = View.GONE
                binding.llLocationAdminPartnerItemEmpty.visibility=  View.VISIBLE
            }
            else {
                binding.tvLocationSearchSuccessTitle.visibility = View.VISIBLE
                binding.tvLocationSearchSuccessCount.visibility= View.VISIBLE
                binding.rvLocationSearchSuccess.visibility = View.VISIBLE
                binding.llLocationAdminPartnerItemEmpty.visibility=  View.GONE
            }
        }
    }

    override fun initView() {
        role = authTokenLocalStore.getUserRoleEnum() ?: UserRole.ADMIN
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initAdapter(){
        adapter = AdminPartnerLocationAdapter(role)
        binding.rvLocationSearchSuccess.apply{
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@LocationSearchSuccessFragment.adapter
        }

    }

}