package com.tech.hive.ui

import android.content.Intent
import androidx.activity.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.ActivityMySplashBinding
import com.tech.hive.ui.auth.AuthActivity
import com.tech.hive.ui.dashboard.DashboardActivity
import com.tech.hive.ui.quiz.QuizActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MySplashActivity : BaseActivity<ActivityMySplashBinding>() {
    private val viewModel: MySplashActivityVM by viewModels()
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