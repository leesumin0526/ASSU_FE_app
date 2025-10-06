package com.ssu.assu.presentation.common.mypage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssu.assu.databinding.FragmentUserInquiryDetailBinding
import com.ssu.assu.domain.model.inquiry.InquiryModel
import com.ssu.assu.ui.inquiry.InquiryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InquiryDetailDialogFragment : DialogFragment() {

    private var _binding: FragmentUserInquiryDetailBinding? = null
    private val binding get() = _binding!!

    //  리스트 화면과 같은 ViewModel 공유
    private val vm: InquiryViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserInquiryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCsDetailBack.setOnClickListener { dismiss() }

        bindLoading()

        val id = requireArguments().getLong(ARG_ID)
        vm.loadDetail(id)

        // 상세 관찰 → UI 바인딩
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.detail.collectLatest { d ->
                    d ?: return@collectLatest
                    bindDetail(d)
                }
            }
        }
    }

    private fun bindDetail(d: InquiryModel) = with(binding) {
        tvDetailTitle.text = d.title
        // createdAt "yyyy-MM-ddTHH:mm:ss[.SSS]" → 분리
        val (date, time) = splitDateTime(d.createdAt)
        tvDetailDate.text = date
        tvDetailTime.text = time
        tvDetailContent.text = d.content

        if (d.answer.isNullOrBlank()) {
            answerSectionContainer.visibility = View.GONE
        } else {
            answerSectionContainer.visibility = View.VISIBLE
            tvDetailAnswer.text = d.answer
        }
    }

    private fun splitDateTime(iso: String?): Pair<String,String> {
        if (iso.isNullOrBlank()) return "" to ""
        val p = iso.split('T')
        val date = p.getOrNull(0) ?: ""
        val time = p.getOrNull(1)?.take(5) ?: ""
        return date to time
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_ID = "inquiry_id"
        fun newInstance(id: Long) = InquiryDetailDialogFragment().apply {
            arguments = Bundle().apply { putLong(ARG_ID, id) }
        }
    }

    private fun bindLoading() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.detailLoading.collectLatest { loading ->
                    if (loading) showLoading("로딩 중...") else hideLoading()
                }
            }
        }
    }

    private fun showLoading(message: String = "로딩 중...") {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.tvLoadingText.text = message
    }
    private fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
    }
}