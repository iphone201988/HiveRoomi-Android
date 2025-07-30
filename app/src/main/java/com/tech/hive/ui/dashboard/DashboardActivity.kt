package com.tech.hive.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.event.SingleRequestEvent
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.databinding.ActivityDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>() {
    private val viewModel: DashboardVM by viewModels()
    private var PERMISSION_REQUEST_CODE = 16
    companion object {
        var bottomNavigationItemCountChange = SingleRequestEvent<Boolean>()
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_dashboard
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        //        if (Build.VERSION.SDK_INT > 32) {
//            if (!shouldShowRequestPermissionRationale("112")) {
//                getNotificationPermission()
//            }
//        }
        // set status bar color
        BindingUtils.statusBarStyle(this@DashboardActivity)
        BindingUtils.statusBarTextColor(this@DashboardActivity, false)
        // view
        initView()
        // click
        initOnClick()
        // observer


    }

    override fun onResume() {
        super.onResume()
        Log.d("fdgdffgg", "onCreateView: ")
    }


    /** handle view **/
    private fun initView() {
        // Disable icon tint
        val navController = findNavController(R.id.nav_host_fragment)
        val userRole = sharedPrefManager.getRole()
        binding.bottomNavigation.itemIconTintList = null

        val menuRes = if (userRole == 3) {
            R.menu.third_menu_item
        } else {
            R.menu.bottom_menu
        }

        binding.bottomNavigation.menu.clear()
        binding.bottomNavigation.inflateMenu(menuRes)


        binding.bottomNavigation.setupWithNavController(navController)

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
        bottomNavigationItemCountChange.observe(this@DashboardActivity) {
            when (it?.status) {
                Status.SUCCESS -> {
                    // view
                    initView()
                }

                Status.ERROR -> {

                }

                Status.LOADING -> {

                }

                null -> {

                }
            }
        }

    }

    /*** permission  function ***/
    private fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this@DashboardActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
        }
    }
    /*** on request permission function ***/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    showErrorToast("Notification Permission Denied")

                }
            }
        }
    }


}