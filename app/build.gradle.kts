import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

val lp = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.ssu.assu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ssu.assu"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "KAKAO_MAP_KEY", "${lp.getProperty("KAKAO_MAP_KEY")}")    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://assu.shop/\"") // 에뮬레이터 → PC 로컬
            // PC 로컬 서버 (예: localhost:8080)
//            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
//            buildConfigField("String", "DEV_BEARER", "\"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJDT01NT04iLCJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjksInVzZXJuYW1lIjoiandhZG1pbnRlc3RAZXhhbXBsZS5jb20iLCJqdGkiOiJmY2UwYzA2Mi03NmE2LTQwMjItYjZjNy04ODRjYjRmYjAxNDUiLCJpYXQiOjE3NTc5NDk1OTMsImV4cCI6MTc1Nzk1MzE5M30.tXNKNztXn7PRyLrDSsgXbwK2czU0YBvirpWL9juJAH8\"") // 🔴 임시

        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://assu.shop/\"") // 운영 주소로 교체
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 바텀 네비게이션
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // grid layout
    implementation(libs.androidx.gridlayout)

    // camera
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // 자동 줄바꿈
    implementation (libs.flexbox)

    implementation (libs.androidx.recyclerview)

    // Kakao
    implementation("com.kakao.maps.open:android:2.12.8")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    // 네트워크
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1") // ← 중요 (Kotlin adapter)
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 코루틴
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.57")
    kapt("com.google.dagger:hilt-android-compiler:2.57")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // Paging (목록 API면)
    implementation("androidx.paging:paging-runtime:3.3.6")

    // 테스트 (서버 목)
    testImplementation("com.squareup.okhttp3:mockwebserver:5.1.0")

    //Gson
    implementation("com.google.code.gson:gson:2.10.1")


    // 프로필 사진 처리
    implementation("io.coil-kt:coil:2.4.0")

    // 채팅
    //    implementation("io.github.hannesa2:stomp-android:2.0.5") // 유지보수 포크
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    // RxJava 2 (+ RxAndroid 2)  ← 꼭 추가
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // 시간 포맷
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")
    // QR 생성
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")


    //Graph (dashboard)
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // 현재 위치 받아오기
    implementation("com.google.android.gms:play-services-location:21.3.0")

}