package com.example.assu_fe_app;

import android.app.Application;

import com.kakao.vectormap.KakaoMapSdk;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY);
    }
}
