package com.example.assu_fe_app.util

import android.content.Context
import android.widget.Toast

object ToastUtil {
    
    /**
     * 기본 토스트를 표시
     * @param context Context
     * @param message 표시할 메시지
     * @param duration Toast.LENGTH_SHORT 또는 Toast.LENGTH_LONG
     */
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
    
    /**
     * 서버 에러를 사용자 친화적으로 변환하여 표시
     * @param context Context
     * @param fail RetrofitResult.Fail 객체
     * @param duration Toast.LENGTH_SHORT 또는 Toast.LENGTH_LONG
     */
    fun showErrorToast(context: Context, fail: RetrofitResult.Fail, duration: Int = Toast.LENGTH_SHORT) {
        // 기본 에러 메시지 표시
        val userFriendlyMessage = "오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        showToast(context, userFriendlyMessage, duration)
    }
}

/**
 * Context Extension 함수로 더 쉽게 사용
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.showToast(this, message, duration)
}

/**
 * 에러를 사용자 친화적으로 변환하여 표시
 */
fun Context.showErrorToast(fail: RetrofitResult.Fail, duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.showErrorToast(this, fail, duration)
}
