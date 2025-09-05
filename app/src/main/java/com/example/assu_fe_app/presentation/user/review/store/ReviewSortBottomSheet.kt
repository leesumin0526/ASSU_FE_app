package com.example.assu_fe_app.presentation.user.review.store
import com.example.assu_fe_app.R
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.assu_fe_app.databinding.BottomsheetReviewSortBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReviewSortBottomSheet(
    private val onSortSelected: (SortType) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetReviewSortBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetReviewSortBinding.inflate(inflater, container, false)

        binding.tvLatest.setOnClickListener {
            onSortSelected(SortType.LATEST)
            dismiss()
        }
        binding.tvOldest.setOnClickListener {
            onSortSelected(SortType.OLDEST)
            dismiss()
        }
        binding.tvRating.setOnClickListener {
            onSortSelected(SortType.RATING)
            dismiss()
        }

        return binding.root
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
    }
}

enum class SortType(val label: String, val apiValue: String) {
    LATEST("최신순", "createdAt,desc"),
    OLDEST("오래된순", "createdAt,asc"),
    RATING("별점순", "rate,desc")
}