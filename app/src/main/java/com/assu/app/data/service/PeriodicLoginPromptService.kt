package com.assu.app.data.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.assu.app.data.local.AuthTokenLocalStore
import com.assu.app.presentation.common.login.LoginActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeriodicLoginPromptService @Inject constructor(
    private val authTokenLocalStore: AuthTokenLocalStore
) {
    
    /**
     * 앱 시작 시 주기적 로그인 유도가 필요한지 확인 (학생 사용자만)
     */
    fun checkAndPromptForReLogin(context: Context): Boolean {
        // 로그인되어 있지 않으면 체크하지 않음
        if (!authTokenLocalStore.isLoggedIn()) {
            Log.d("PeriodicLoginPromptService", "User not logged in, skipping re-login check")
            return false
        }
        
        // 사용자 역할별 주기적 로그인 유도 확인 (학생만)
        if (authTokenLocalStore.shouldPromptForReLoginByUserRole()) {
            Log.d("PeriodicLoginPromptService", "Periodic re-login required for student, prompting user")
            showReLoginDialog(context)
            return true
        }
        
        Log.d("PeriodicLoginPromptService", "No need for periodic re-login yet")
        return false
    }
    
    /**
     * 사용자에게 재로그인 다이얼로그 표시
     */
    private fun showReLoginDialog(context: Context) {
        // 커스텀 다이얼로그나 알림을 표시할 수 있지만,
        // 여기서는 간단히 로그인 화면으로 이동
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("reason", "periodic_relogin")
            putExtra("message", "학적상태 업데이트를 위해 다시 로그인해주세요.")
        }
        
        context.startActivity(intent)
        Log.d("PeriodicLoginPromptService", "Redirected to login for periodic re-login")
    }
    
    /**
     * 수동으로 재로그인 유도 (관리자나 특정 조건에서 호출)
     */
    fun promptForReLoginManually(context: Context, reason: String = "학적상태 업데이트") {
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("reason", "manual_relogin")
            putExtra("message", "$reason 을 위해 다시 로그인해주세요.")
        }
        
        context.startActivity(intent)
        Log.d("PeriodicLoginPromptService", "Manual re-login prompted: $reason")
    }
    
    /**
     * 학생 사용자 재로그인 간격 설정 (일 단위)
     * 학생만 7일마다 재로그인 유도
     */
    fun getStudentReLoginIntervalDays(): Int {
        return 7 // 학생: 7일마다 재로그인 유도
    }
    
    /**
     * 마지막 로그인으로부터 경과된 일수
     */
    fun getDaysSinceLastLogin(): Int {
        val lastLoginTime = authTokenLocalStore.getLastLoginTime()
        if (lastLoginTime == 0L) return 0
        
        val currentTime = System.currentTimeMillis()
        val timeSinceLastLogin = currentTime - lastLoginTime
        return (timeSinceLastLogin / (1000 * 60 * 60 * 24)).toInt()
    }
}
