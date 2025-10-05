package com.assu.app

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.assu.app.data.service.TokenManagementService
import com.assu.app.data.service.PeriodicLoginPromptService
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var tokenManagementService: TokenManagementService

    @Inject
    lateinit var periodicLoginPromptService: PeriodicLoginPromptService

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private var appContext: Context? = null

        fun getApplicationContext(): Context? {
            return appContext
        }

        fun isOnline(): Boolean {
            val cm = appContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = cm.activeNetwork ?: return false
                val caps = cm.getNetworkCapabilities(network) ?: return false

                val hasWifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                val hasCellular = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                val hasEthernet = caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                val hasVpn = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

                hasWifi || hasCellular || hasEthernet || hasVpn
            } else {
                // deprecated이지만 하위 호환용
                @Suppress("DEPRECATION")
                val activeNetwork = cm.activeNetworkInfo
                activeNetwork?.isConnectedOrConnecting == true
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        // 카카오맵 초기화
        if (isArmDevice() && BuildConfig.KAKAO_MAP_KEY != null && BuildConfig.KAKAO_MAP_KEY.isNotEmpty()) {
            KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY)
        }

        // 앱 시작 시 토큰 상태 확인 및 필요시 갱신
        applicationScope.launch {
            try {
                tokenManagementService.checkAndRefreshTokenOnAppStart(applicationScope)
                Log.d("MyApplication", "Token check completed on app start")
                
                // 학생 사용자의 경우 주기적 로그인 유도 확인
                periodicLoginPromptService.checkAndPromptForReLogin(this@MyApplication)
                Log.d("MyApplication", "Periodic login check completed on app start")
            } catch (e: Exception) {
                Log.e("MyApplication", "Error during token check on app start: ${e.message}")
            }
        }
    }

    private fun isArmDevice(): Boolean {
        val abi = if (Build.SUPPORTED_ABIS.isNotEmpty()) {
            Build.SUPPORTED_ABIS[0]
        } else {
            ""
        }
        return abi.contains("arm")
    }
}
