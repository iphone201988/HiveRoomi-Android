package com.tech.hive.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.activity.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.location.LocationHandler
import com.tech.hive.base.location.LocationResultListener
import com.tech.hive.base.permission.PermissionHandler
import com.tech.hive.base.permission.Permissions
import com.tech.hive.base.utils.BindingUtils
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
        // set status bar color
        BindingUtils.statusBarStyle(this@MySplashActivity)
        BindingUtils.statusBarTextColor(this@MySplashActivity, false)
        // check login or verification
        checkLogin()
        // click
        initClick()
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            // check location
            checkLocation()
        }
    }

    /** check login function **/
    private fun checkLogin() {
        var userdata = sharedPrefManager.getLoginData()
        var otpVerify = sharedPrefManager.getOtpToken()
        var profileVerify = sharedPrefManager.getProfile()
        var quizVerify = sharedPrefManager.getQuiz()
        if (userdata != null) {
            if (otpVerify == false) {
                val intent = Intent(this@MySplashActivity, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("rute", "otp")
                startActivity(intent)

            } else {
                if (profileVerify == false) {
                    if (userdata.profileRole == 3) {
                        val intent = Intent(this@MySplashActivity, AuthActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("rute", "provider")
                        startActivity(intent)
                        // navigate to provider fragment
                    } else {
                        val intent = Intent(this@MySplashActivity, AuthActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("rute", "personal")
                        startActivity(intent)
                        // navigate to personal fragment
                    }

                } else if (quizVerify == false) {
                    if (userdata.profileRole == 3) {
                        val intent = Intent(this@MySplashActivity, DashboardActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@MySplashActivity, QuizActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    }

                } else {
                    val intent = Intent(this@MySplashActivity, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)


                }
            }

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
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("rute", "language")
                    startActivity(intent)
                }

            }
        }
    }

}