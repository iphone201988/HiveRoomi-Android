package com.tech.hive.ui.for_room_mate.settings_screen

import android.content.Intent
import android.util.Log
import android.util.Patterns
import androidx.activity.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.base.utils.showSuccessToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.databinding.ActivityHelpBinding
import com.tech.hive.ui.auth.AuthActivity
import com.tech.hive.ui.for_room_mate.settings.SettingsActivity
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
                    if ((validate())){
                        val name = binding.etFullName.text.toString().trim()
                        val email = binding.etEmail.text.toString().trim()
                        val desc = binding.etShortBio.text.toString().trim()
                        var userId = sharedPrefManager.getLoginData()?._id
                        val data = HashMap<String, Any>()
                        data["userId"] = userId.toString()
                        data["name"] = name
                        data["email"] = email
                        data["message"] = desc
                        viewModel.feedBackApiCall(Constants.USER_FEEDBACK, data)
                    }
                }
                R.id.ivAskQn -> {
                    startActivity(Intent(this, FrequentQuestionsActivity::class.java))
                }
            }
        }

    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.settingObserver.observe(this@HelpActivity)
        {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "feedBackApiCall" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                        finish()
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {

                }
            }
        }
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val name = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val desc = binding.etShortBio.text.toString().trim()

        if (name.isEmpty()) {
            showInfoToast("Please enter  name")
            return false
        }else if (email.isEmpty()) {
            showInfoToast("Please enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showInfoToast("Please enter a valid email")
            return false
        } else if (desc.isEmpty()) {
            showInfoToast("Please enter description")
            return false
        }
        return true
    }



}