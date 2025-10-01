package com.example.assu_fe_app.presentation.common.report


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