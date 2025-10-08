package com.ssu.assu.presentation.admin.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssu.assu.R
import com.ssu.assu.data.dto.partnership.request.OptionDto
import com.ssu.assu.data.dto.partnership.request.SelectedPlaceDto
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.databinding.FragmentServicePassiveProposalWritingBindingImpl
import com.ssu.assu.presentation.admin.home.adapter.ServicePassiveProposalAdapter
import com.ssu.assu.presentation.admin.home.adapter.toOptionDtoOrThrow
import com.ssu.assu.presentation.base.BaseFragment
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ServicePassivityProposalWritingFragment
    : BaseFragment<FragmentServicePassiveProposalWritingBindingImpl>(R.layout.fragment_service_passive_proposal_writing) {

    private val moshi by lazy { Moshi.Builder().build() }
    private val optionListType = Types.newParameterizedType(List::class.java, OptionDto::class.java)
    private val optionListAdapter = moshi.adapter<List<OptionDto>>(optionListType)

    private var selectedPlaceName: String? = null
    private var selectedPlaceId: String? = null
    private var selectedPlaceAddress: String? = null
    private var selectedPlaceRoadAddress: String? = null
    private var selectedPlaceLatitude: Double? = null
    private var selectedPlaceLongitude: Double? = null

    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore

    val adapter = ServicePassiveProposalAdapter {
        onItemOptionSelected()
        checkAllFieldsFilled()
    }

    override fun initView() {
        binding.etFragmentServiceProposalAdmin.setText(authTokenLocalStore.getUserName())

        parentFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
            val name = bundle.getString("selectedPlace")
            val address     = bundle.getString("selectedPlace_address")
            val roadAddress = bundle.getString("selectedPlace_roadAddress")
            val latitude    = bundle.getDouble("selectedPlace_latitude", Double.NaN)
            val longitude   = bundle.getDouble("selectedPlace_longitude", Double.NaN)
            val storeId = bundle.getString("selectedPlace_placeId")

            binding.tvFragmentServiceProposalPartner.text = name

            selectedPlaceName        = name
            selectedPlaceAddress     = address
            selectedPlaceRoadAddress = roadAddress
            selectedPlaceLatitude    = if (latitude.isNaN()) null else latitude
            selectedPlaceLongitude   = if (longitude.isNaN()) null else longitude
            selectedPlaceId = storeId
        }
        binding.rvFragmentServiceProposalItemSet.adapter = adapter
        binding.rvFragmentServiceProposalItemSet.layoutManager =
            LinearLayoutManager(requireContext())

        if (adapter.getItems().isEmpty()) {
            adapter.addItem()
        }

        binding.tvAddProposalItem.setOnClickListener {
            adapter.addItem()
            checkAllFieldsFilled()
        }

        binding.btnCompleted.setOnClickListener {
            // 1) 옵션 수집(실패 시 즉시 중단)
            val options = try {
                adapter.getItems().map { it.toOptionDtoOrThrow() }   // mapNotNull 금지
            } catch (e: IllegalArgumentException) {
                return@setOnClickListener
            } catch (e: IllegalStateException) {
                return@setOnClickListener
            }

            // 2) 부가 정보
            val adminName = authTokenLocalStore.getUserName()
            val storeName = binding.tvFragmentServiceProposalPartner.text?.toString().orEmpty()

            val selectedPlace = SelectedPlaceDto(
                roadAddress = selectedPlaceRoadAddress,
                address = selectedPlaceAddress,
                placeId = selectedPlaceId,          // 여기 타입이 Long/Int라면 아래 put도 Long/Int로!
                name = storeName,
                latitude = selectedPlaceLatitude,
                longitude = selectedPlaceLongitude
            )

            // 3) 옵션 직렬화
            val optionsJson =
                try { optionListAdapter.toJson(options) }
                catch (_: Throwable) { com.google.gson.Gson().toJson(options) }  // fallback

            // 4) 번들 구성 (storeId 타입 주의)
            val bundle = Bundle().apply {
                // 🔧 storeId: 수신부가 Long을 기대한다면 putLong 사용
                when (selectedPlace.placeId) {
                    is Long    -> putLong("arg_storeId", selectedPlace.placeId as Long)
                    is Int     -> putInt("arg_storeId", selectedPlace.placeId as Int)
                    is String  -> putString("arg_storeId", selectedPlace.placeId as String)
                    else       -> putString("arg_storeId", selectedPlace.placeId?.toString())
                }

                putString("arg_storeName", selectedPlace.name)
                putString("arg_adminName", adminName)

                selectedPlace.latitude?.let  { putDouble("arg_latitude",  it) }
                selectedPlace.longitude?.let { putDouble("arg_longitude", it) }

                putString("arg_selectedPlace_address", selectedPlace.address)
                putString("arg_selectedPlace_roadAddress", selectedPlace.roadAddress)
                putString("arg_options_json", optionsJson)
            }

            // 5) 디버깅 로그
            bundle.keySet().forEach { key ->
                Log.d("BundleDebug", "key=$key, value=${bundle.get(key)}")
            }

            // 6) 네비게이션
            findNavController().navigate(
                R.id.action_serviceProposalWritingFragment_to_registerFinishFragment,
                bundle
            )
        }

        binding.ivFragmentServiceProposalBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvFragmentServiceProposalPartner.setOnClickListener {
            val bundle = Bundle().apply{
                putString("type", "passive")
            }

            findNavController().navigate(
                R.id.action_serviceProposalWritingFragment_to_locationSearchFragment, bundle)
        }


        setUpFragmentEditTextWatchers()
        checkAllFieldsFilled()
    }

    private fun onItemOptionSelected() {
        binding.tvAddProposalItem.visibility = View.VISIBLE
    }

    override fun initObserver() {}

    private fun setUpFragmentEditTextWatchers() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = checkAllFieldsFilled()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }


        binding.etFragmentServiceProposalAdmin.addTextChangedListener(watcher)
    }

    private fun checkAllFieldsFilled() {
        val partnerFilled = binding.tvFragmentServiceProposalPartner.text?.isNotBlank() == true
        val adminFilled = binding.etFragmentServiceProposalAdmin.text?.isNotBlank() == true
        val itemFieldsFilled = adapter.getItems().all { item ->
            // `ProposalItem`의 `contents`가 비어있지 않은지 확인하는 로직 추가
            item.contents.all { it.isNotBlank() }
        }

        val allFilled = partnerFilled && adminFilled && itemFieldsFilled

        val colorRes = if (allFilled) R.color.assu_main else R.color.assu_sub
        binding.btnCompleted.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)

        binding.btnCompleted.isEnabled = allFilled
    }
}