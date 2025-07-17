package com.tech.hive.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.location.LocationHandler
import com.tech.hive.base.location.LocationResultListener
import com.tech.hive.base.permission.PermissionHandler
import com.tech.hive.base.permission.Permissions
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.api.Constants
import com.tech.hive.databinding.ActivityMySplashBinding
import com.tech.hive.ui.auth.AuthActivity
import com.tech.hive.ui.dashboard.DashboardActivity
import com.tech.hive.ui.quiz.QuizActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MySplashActivity : BaseActivity<ActivityMySplashBinding>(), LocationResultListener {
    private val viewModel: MySplashActivityVM by viewModels()
    private var locationHandler: LocationHandler? = null
    override fun getLayoutResource(): Int {
        return R.layout.activity_my_splash
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        var userdata = sharedPrefManager.getLoginData()
        if (userdata!=null){
            when {
                userdata.profileRole==1 -> {
                   sharedPrefManager.saveRole(1)
                }
                userdata.profileRole==2 -> {
                    sharedPrefManager.saveRole(2)

                }
                userdata.profileRole==3 -> {
                 sharedPrefManager.saveRole(3)

                }
            }
            val intent = Intent(this@MySplashActivity, AuthActivity::class.java)
            startActivity(intent)
        }
        // set status bar color
        BindingUtils.statusBarStyle(this@MySplashActivity)
        BindingUtils.statusBarTextColor(this@MySplashActivity, false)

        // click
        initClick()
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            // check location
            checkLocation()
        }


    }

    /* check location Function */
    private fun checkLocation() {
        Permissions.check(
            this@MySplashActivity,
            Manifest.permission.ACCESS_FINE_LOCATION,
            0,
            object : PermissionHandler() {
                override fun onGranted() {
                    createLocationHandler()
                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                    super.onDenied(context, deniedPermissions)

                }
            })
    }


    /**** location handler ***/
    private fun createLocationHandler() {
        locationHandler = LocationHandler(this@MySplashActivity, this)
        locationHandler?.getUserLocation()
        locationHandler?.removeLocationUpdates()
    }

    override fun getLocation(location: Location) {
        BindingUtils.latitude = location.latitude
        BindingUtils.longitude = location.longitude
    }


    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(this@MySplashActivity) {
            when (it?.id) {
                //  button click
                R.id.clEmail, R.id.clGoogle, R.id.clFaceBook -> {
                    val intent = Intent(this@MySplashActivity, AuthActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }

}