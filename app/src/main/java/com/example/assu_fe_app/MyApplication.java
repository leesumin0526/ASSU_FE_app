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
//        KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY);
        if (isArmDevice()) {
            KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY);
        }
    }
    private boolean isArmDevice() {
        String abi = Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0
                ? Build.SUPPORTED_ABIS[0] : "";
        return abi.contains("arm");
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            if (caps == null) return false;

            return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        } else {
            // deprecated이지만 하위 호환용
            //noinspection deprecation
            return cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
    }
}
