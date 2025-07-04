package com.tech.hive.ui.dashboard

import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.ActivityDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>() {
    private val viewModel: DashboardVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_dashboard
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@DashboardActivity)
        BindingUtils.statusBarTextColor(this@DashboardActivity, false)
        // view
        initView()
        // click
        initOnClick()
        // observer
        initObserver()
    }

    /** handle view **/
    private fun initView() {
        // set bottom nav bar
        binding.bottomNavigation.itemIconTintList = null
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(
            navController
        )
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val currentDestinationId = navController.currentDestination?.id
            when (item.itemId) {
                R.id.homeFragment -> {
                    if (currentDestinationId != R.id.homeFragment) {
                        navController.popBackStack(R.id.homeFragment, true)
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }

                R.id.matchesFragment -> {
                    if (currentDestinationId != R.id.matchesFragment) {
                        navController.popBackStack(R.id.matchesFragment, true)
                        navController.navigate(R.id.matchesFragment)
                    }
                    true
                }

                R.id.messagesFragment -> {
                    if (currentDestinationId != R.id.messagesFragment) {
                        navController.popBackStack(R.id.messagesFragment, true)
                        navController.navigate(R.id.messagesFragment)
                    }
                    true
                }

                R.id.profileFragment -> {
                    if (currentDestinationId != R.id.profileFragment) {
                        navController.popBackStack(R.id.profileFragment, true)
                        navController.navigate(R.id.profileFragment)
                    }
                    true
                }

                else -> false
            }
        }
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {

        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.dashBoardObserver.observe(this) {

        }

    }

}