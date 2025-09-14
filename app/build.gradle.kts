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

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.example.assu_fe_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.assu_fe_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "KAKAO_MAP_KEY", "\"${properties.getProperty("KAKAO_MAP_KEY")}\"")    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http:/10.0.2.2:8080/\"") // 에뮬레이터 → PC 로컬
            buildConfigField("String", "DEV_BEARER", "\"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdXRoUmVhbG0iOiJDT01NT04iLCJyb2xlIjoiUEFSVE5FUiIsInVzZXJJZCI6MzAsInVzZXJuYW1lIjoicGFydG5lcjMyQGdtYWlsLmNvbSIsImp0aSI6IjdkMWFhZWRjLWQzYjctNDhkZS1hZmVhLTY2YWNhZjEzN2Y1OSIsImlhdCI6MTc1Njc3ODcwNCwiZXhwIjoxNzU2NzgyMzA0fQ.04DAYtdoyusmIR7uDbU2RgtyVs_CFltWF0T7g_ERNXU\"") // 🔴 임시

        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"") // 운영 주소로 교체
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


    // 네트워크
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1") // ← 중요 (Kotlin adapter)

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

    // QR 생성
    implementation("com.google.zxing:core:3.5.3")

}