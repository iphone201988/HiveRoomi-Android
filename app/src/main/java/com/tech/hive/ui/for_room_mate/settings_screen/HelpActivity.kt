package com.tech.hive.ui.for_room_mate.settings_screen

import android.content.Intent
import androidx.activity.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.ActivityHelpBinding
import com.tech.hive.ui.for_room_mate.settings.SettingsVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HelpActivity : BaseActivity<ActivityHelpBinding>() {
    private val viewModel: SettingsVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_help
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // view
        initView()
        // clicks
        initOnClick()
        // observer
        initObserver()
    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@HelpActivity)
        BindingUtils.statusBarTextColor(this@HelpActivity, false)
        // check state
        binding.fullNameType = ""
        binding.emailType = ""
        binding.messageType = ""

    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // back button
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }
                // send message
                R.id.btnContinue -> {
                    onBackPressedDispatcher.onBackPressed()
                }
                // FAQ
                R.id.ivAskQn -> {
                    startActivity(Intent(this, FrequentQuestionsActivity::class.java))
                }
            }
        }

    }

    /** handle api response **/
    private fun initObserver() {

    }

}