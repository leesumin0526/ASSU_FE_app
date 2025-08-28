package com.example.assu_fe_app.presentation.admin.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentAdminMypagePendingPartnershipBinding

class AdminMypagePendingPartnershipDialogFragment : DialogFragment() {

    private var _binding: FragmentAdminMypagePendingPartnershipBinding? = null
    private val binding get() = _binding!!

    private lateinit var pendingContractAdapter: AdminPendingContractAdapter

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
        
        // 뒤로가기 버튼 클릭
        binding.btnPendingBack.setOnClickListener {
            dismiss()
        }

        // RecyclerView 설정
        setupRecyclerView()

        // 샘플 데이터 로드
        loadSampleData()
    }

    private fun setupRecyclerView() {
        pendingContractAdapter = AdminPendingContractAdapter { contract ->
            showDeleteDialog(contract)
        }

        // 삭제 확인 리스너 설정
        pendingContractAdapter.setOnDeleteConfirmedListener { contract ->
            // 삭제 완료 후 처리 (예: 카운트 업데이트)
            binding.tvPendingCount.text = pendingContractAdapter.currentList.size.toString()
        }

        binding.rvPendingContracts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pendingContractAdapter
        }
    }

    private fun loadSampleData() {
        // 테스트를 위해 4개의 샘플 데이터로 설정 (실제로는 서버에서 데이터를 가져옴)
        val sampleContracts = listOf(
            PendingContract("역전할머니맥주 숭실대점", "2025-03-25"),
            PendingContract("스타벅스 숭실대점", "2025-03-24"),
            PendingContract("맥도날드 숭실대점", "2025-03-23"),
            PendingContract("롯데리아 숭실대점", "2025-03-22")
        )
        
        // 데이터가 없을 때 테스트 (주석 해제하여 테스트 가능)
        // val sampleContracts = listOf<PendingContract>()

        pendingContractAdapter.submitList(sampleContracts)
        binding.tvPendingCount.text = sampleContracts.size.toString()
        
        // 데이터가 없을 때 UI 업데이트
        updateUIForEmptyState(sampleContracts.isEmpty())
    }
    
    private fun updateUIForEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            // 데이터가 없을 때: 빈 상태 UI 표시
            binding.infoContainer.visibility = View.GONE
            binding.countContainer.visibility = View.GONE
            binding.rvPendingContracts.visibility = View.GONE
            binding.emptyStateContainer.visibility = View.VISIBLE
        } else {
            // 데이터가 있을 때: 기존 UI 표시
            binding.infoContainer.visibility = View.VISIBLE
            binding.countContainer.visibility = View.VISIBLE
            binding.rvPendingContracts.visibility = View.VISIBLE
            binding.emptyStateContainer.visibility = View.GONE
        }
    }

    private fun showDeleteDialog(contract: PendingContract) {
        val deleteDialogFragment = AdminDeleteContractDialogFragment.newInstance(
            contract = contract,
            onDeleteConfirmed = { deletedContract ->
                pendingContractAdapter.removeContract(deletedContract)
            }
        )
        deleteDialogFragment.show(childFragmentManager, "DeleteDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
