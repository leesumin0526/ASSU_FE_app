package com.example.assu_fe_app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import com.kakao.vectormap.KakaoMapSdk;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        if (isArmDevice() && BuildConfig.KAKAO_MAP_KEY != null && !BuildConfig.KAKAO_MAP_KEY.isEmpty()) {
            KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY);
        }
    }
    private boolean isArmDevice() {
        String abi = Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0
                ? Build.SUPPORTED_ABIS[0]
                : "";
        return abi.contains("arm");
    }

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            if (caps == null) {
                return false;
            }

            boolean hasWifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            boolean hasCellular = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            boolean hasEthernet = caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            boolean hasVpn = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);

            boolean isOnline = hasWifi || hasCellular || hasEthernet || hasVpn;

            return isOnline;
        } else {
            // deprecated이지만 하위 호환용
            // noinspection deprecation
            boolean isConnected = cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isConnectedOrConnecting();
            return isConnected;
        }
    }
}
