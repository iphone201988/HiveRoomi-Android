package com.tech.hive.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.ActivityAuthBinding
import com.tech.hive.ui.MySplashActivity
import com.tech.hive.ui.dashboard.DashboardActivity
import com.tech.hive.ui.quiz.QuizActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>() {
    private val viewModel: AuthActivityVM by viewModels()
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.authNavigationHost) as NavHostFragment).navController
    }




    override fun getLayoutResource(): Int {
        return R.layout.activity_auth
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {

        var userdata = sharedPrefManager.getLoginData()
        if (userdata!=null){
            val intent = Intent(this@AuthActivity, DashboardActivity::class.java)
            startActivity(intent)
            finishAffinity()
//            if (userdata.sendOtp == false) {
//                val bundle = Bundle().apply {
//                    putString("userEmail", userdata.email)
//                }
//                BindingUtils.navigateWithSlide(
//                    navController,
//                    R.id.navigateToVerifyFragment,
//                    bundle
//                )
//            }
//            else {
//                if (userdata.isProfileComplete == false) {
//                    if (userdata.profileRole == 3) {
//                        BindingUtils.navigateWithSlide(
//                            navController,
//                            R.id.navigateToProviderTypeFragment,
//                            null
//                        )
//                    } else {
//                        BindingUtils.navigateWithSlide(
//                            navController,
//                            R.id.navigateToPersonalFragment,
//                            null
//                        )
//                    }
//
//                } else if (userdata.isQuizComplete == false|| userdata.totalQuizDone == 0) {
//                    val intent = Intent(
//                        this@AuthActivity,
//                        QuizActivity::class.java
//                    )
//                    startActivity(intent)
//                    finishAffinity()
//                } else {
//                    val intent = Intent(this@AuthActivity, DashboardActivity::class.java)
//                    startActivity(intent)
//                    finishAffinity()
//
//                }
//            }
        }else{
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    navController.graph =
                        navController.navInflater.inflate(R.navigation.auth_navigation).apply {
                            setStartDestination(R.id.fragmentLanguage)
                        }
                }
            }
        }


    }
}