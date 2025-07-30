package com.tech.hive.ui.auth

import android.content.res.Configuration
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

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


        // intent
        val rute = intent.getStringExtra("rute")

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                navController.graph =
                    navController.navInflater.inflate(R.navigation.auth_navigation).apply {
                        if (rute?.isNotEmpty() == true) {
                            when (rute) {
                                "otp" -> {
                                    setStartDestination(R.id.fragmentVerify)
                                }

                                "provider" -> {
                                    setStartDestination(R.id.fragmentProviderType)
                                }

                                "personal" -> {
                                    setStartDestination(R.id.fragmentPersonal)
                                }

                                else -> {
                                    setStartDestination(R.id.fragmentLanguage)
                                }
                            }
                        } else {
                            setStartDestination(R.id.fragmentLanguage)
                        }


                    }
            }
        }


    }


}