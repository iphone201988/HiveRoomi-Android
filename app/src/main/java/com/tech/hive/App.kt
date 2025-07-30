package com.tech.hive

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.google.firebase.FirebaseApp
import com.mapbox.mapboxsdk.Mapbox
import com.tech.hive.base.AppLifecycleListener
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    private var isRestarting: Boolean = false

    fun onLogout() {
        restartApp()
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Init MapLibre
        Mapbox.getInstance(this)
//        val credentialsProvider = CognitoCachingCredentialsProvider(
//            applicationContext,
//            "IDENTITY_POOL_ID", // Replace with your pool ID
//            Regions.US_EAST_1 // Replace with your region
//        )

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener(this@App))
    }

    private fun restartApp() {
        if (!isRestarting) {
            isRestarting = true
            val intent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
