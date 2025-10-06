package com.ssu.assu.presentation.common.report


interface OnItemClickListener{
    fun onClick(position: Int)
}
interface OnReportTargetSelectedListener {
    fun onReportTargetSelected(target: String, isStudentReport: Boolean)
}

interface OnReviewReportConfirmedListener {
    fun onReviewReportConfirmed(position: Int, reportReason: String)
}

interface OnReviewReportCompleteListener{
    fun onReviewReportComplete(position: Int)
}