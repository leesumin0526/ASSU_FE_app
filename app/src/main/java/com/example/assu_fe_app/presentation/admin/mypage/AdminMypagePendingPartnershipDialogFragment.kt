package com.example.assu_fe_app.presentation.admin.mypage

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentAdminMypagePendingPartnershipBinding
import com.example.assu_fe_app.domain.model.partnership.SuspendedPaperModel
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment
import com.example.assu_fe_app.presentation.common.contract.toContractData
import com.example.assu_fe_app.ui.partnership.AdminPendingPartnershipViewModel
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@AndroidEntryPoint
class AdminMypagePendingPartnershipDialogFragment : DialogFragment() {

    private var _binding: FragmentAdminMypagePendingPartnershipBinding? = null
    private val binding get() = _binding!!

    private lateinit var pendingContractAdapter: AdminPendingContractAdapter
    private val pendingVm: AdminPendingPartnershipViewModel by viewModels()
    private val partnershipVm: PartnershipViewModel by activityViewModels()

    private var pendingPartnershipId: Long? = null
    private var lastClickedItem: SuspendedPaperModel? = null

    private var autoOpenTargetId: Long? = null
    private var autoOpenConsumed = false

    @Inject lateinit var authTokenLocalStore: AuthTokenLocalStore

    companion object {
        private const val ARG_TARGET_ID = "arg_target_id"

        fun newInstance(targetId: Long?): AdminMypagePendingPartnershipDialogFragment {
            return AdminMypagePendingPartnershipDialogFragment().apply {
                arguments = Bundle().apply {
                    if (targetId != null) putLong(ARG_TARGET_ID, targetId)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminMypagePendingPartnershipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPendingBack.setOnClickListener { dismiss() }
        autoOpenTargetId = if (arguments?.containsKey(ARG_TARGET_ID) == true)
            arguments?.getLong(ARG_TARGET_ID)
        else null

        setupRecyclerView()
        bindViewModel()
        bindPartnershipDetail()
        bindLoading()

        pendingVm.load() // 서버 호출
    }

    private fun setupRecyclerView() {
        pendingContractAdapter = AdminPendingContractAdapter(
            onDeleteClick = { item -> showDeleteDialog(item) },
            onItemClick   = { item ->
                lastClickedItem = item
                pendingPartnershipId = item.paperId

                pendingContractAdapter.selectById(item.paperId)

                partnershipVm.getPartnershipDetail(item.paperId) // paperId == partnershipId
            }
        )
        binding.rvPendingContracts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPendingContracts.adapter = pendingContractAdapter
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            pendingVm.items.collect { list ->
                pendingContractAdapter.submitList(list)
                binding.tvPendingCount.text = list.size.toString()
                updateUIForEmptyState(list.isEmpty())
                maybeAutoOpen(list)
            }
        }
    }

    private fun showDeleteDialog(contract: SuspendedPaperModel) {
        AdminDeleteContractDialogFragment.newInstance(
            contract = contract,
            onDeleteConfirmed = { pendingVm.delete(it.paperId) } // 실제 삭제 API
        ).show(childFragmentManager, "DeleteDialog")
    }

    private fun updateUIForEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.infoContainer.visibility = View.GONE
            binding.countContainer.visibility = View.GONE
            binding.rvPendingContracts.visibility = View.GONE
            binding.emptyStateContainer.visibility = View.VISIBLE
        } else {
            binding.infoContainer.visibility = View.VISIBLE
            binding.countContainer.visibility = View.VISIBLE
            binding.rvPendingContracts.visibility = View.VISIBLE
            binding.emptyStateContainer.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }

    private fun bindPartnershipDetail() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            partnershipVm.getPartnershipDetailUiState.collect { state ->
                when (state) {
                    is PartnershipViewModel.PartnershipDetailUiState.Loading -> {
                        showLoading("로딩 중...")
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Success -> {
                        hideLoading()
                        val wanted = pendingPartnershipId
                        if (wanted == null || state.data.partnershipId != wanted) return@collect
                        pendingPartnershipId = null

                        val adminNameFb   = authTokenLocalStore.getUserName() ?: "관리자"
                        val partnerNameFb = lastClickedItem?.partnerName ?: "-"

                        val data = state.data.toContractData(
                            partnerNameFallback = partnerNameFb,
                            adminNameFallback   = adminNameFb,
                            fallbackStart = state.data.periodStart,
                            fallbackEnd   = state.data.periodEnd
                        )

                        PartnershipContractDialogFragment
                            .newInstance(data)
                            .apply {
                                onDismissListener = { pendingContractAdapter.clearSelection() }
                            }
                            .show(childFragmentManager, "PartnershipContractDialog")
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Fail,
                    is PartnershipViewModel.PartnershipDetailUiState.Error -> {
                        hideLoading()
                        pendingPartnershipId = null
                    }
                    PartnershipViewModel.PartnershipDetailUiState.Idle -> Unit
                }
            }
        }
    }


    private fun maybeAutoOpen(list: List<SuspendedPaperModel>) {
        if (autoOpenConsumed) return
        if (list.isEmpty()) return

        val targetId = autoOpenTargetId ?: return

        val target = list.firstOrNull { it.paperId == targetId } ?: return

        autoOpenConsumed = true
        lastClickedItem = target
        pendingPartnershipId = target.paperId

        pendingContractAdapter.selectById(target.paperId)
        partnershipVm.getPartnershipDetail(target.paperId)
    }

    private fun bindLoading() {
        // 기존의 pendingVm.loading / pendingVm.busy 각각 collect 하던 코드 삭제하세요.
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            kotlinx.coroutines.flow.combine(
                pendingVm.loading,
                pendingVm.busy,
                partnershipVm.getPartnershipDetailUiState
            ) { listLoading, busy, detailState ->
                val detailLoading = detailState is PartnershipViewModel.PartnershipDetailUiState.Loading

                // 어떤 로딩을 보여줄지 우선순위 결정
                val show = listLoading || busy || detailLoading
                val message = when {
                    busy -> "삭제 중..."
                    detailLoading -> "로딩 중..."
                    listLoading -> "로딩 중..."
                    else -> null
                }
                show to message
            }
                .distinctUntilChanged() // 같은 상태 연속 호출 방지
                .collect { (show, message) ->
                    if (show) {
                        showLoading(message ?: "로딩 중...")
                    } else {
                        hideLoading()
                    }
                }
        }
    }

    private fun showLoading(message: String) {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.tvLoadingText.text = message
    }
    private fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
    }

}
