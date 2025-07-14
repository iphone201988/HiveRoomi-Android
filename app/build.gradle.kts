plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin1kept)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.tech.hive"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tech.hive"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    buildTypes {

        debug {

            buildConfigField("String", "BASE_URL", "\"http://13.51.228.96:8111/api/v1/\"")
            buildConfigField("String", "SOCKET_URL", "\"http://13.51.228.96:8111\"")
            buildConfigField("String", "MEDIA_BASE_URL", "\"http://13.51.228.96:8111\"")

        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "BASE_URL", "\"http://13.51.228.96:8111/api/v1/\"")
            buildConfigField("String", "SOCKET_URL", "\"http://13.51.228.96:8111\"")
            buildConfigField("String", "MEDIA_BASE_URL", "\"http://13.51.228.96:8111\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation.fragment.ktx.v277)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.dagger)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.play.services.location)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.glide)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.gson)
    kapt(libs.dagger.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.lottie)
    implementation(libs.converter.gson)
    //  image picker
    implementation(libs.imagepicker)
    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-auth-ktx")

    //facebook login dependency
    implementation("com.facebook.android:facebook-login:17.0.2")
    implementation("com.facebook.android:facebook-android-sdk:17.0.2")

    // for location access
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // calender
    implementation(libs.material.calendar.view)

    //socket dependency
    implementation("io.socket:socket.io-client:2.0.1")
    implementation("com.tbuonomo:dotsindicator:5.0")
    implementation("com.github.wdsqjq:AndRatingBar:1.0.6")

}