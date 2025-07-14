package com.tech.hive.ui.role

import android.util.Log
import androidx.activity.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.RoleChangeResponse
import com.tech.hive.databinding.ActivityChangeRoleBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class ChangeRoleActivity : BaseActivity<ActivityChangeRoleBinding>() {
    private val viewModel: ChangeRoleActivityVM by viewModels()
    private var roleType = 0
    private var language = ""
    private var apiCallType = 0
    override fun getLayoutResource(): Int {
        return R.layout.activity_change_role
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // get data intent
        var roleType = intent.getStringExtra("roleType")
        val userRole = intent.getStringExtra("userRole")
        val userLanguage = intent.getStringExtra("userLanguage")
        if (roleType == "role") {
            apiCallType = 1
            binding.visibilityType = 1
        } else {
            apiCallType = 2
            binding.visibilityType = 2
        }
        if (userRole?.isNotEmpty() == true) {
            roleType = userRole
            binding.selectType = userRole.toInt()
        }
        if (userLanguage?.isNotEmpty() == true) {
            if (userLanguage == "en") {
                language = "en"
                binding.selectType = 1
            } else {
                language = "it"
                binding.selectType = 2
            }

        }
        // click
        initOnCLick()
        // observer
        initObserver()

    }

    /** api call **/
    private fun apiCall(type: Int) {
        val data = HashMap<String, RequestBody>()
        if (type == 1) {
            data["profileRole"] = roleType.toString().toRequestBody()
        } else if (type == 2) {
            data["language"] = language.toRequestBody()
        }

        viewModel.updateRoleAndLanguage(data, null)

    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.observeCommon.observe(this@ChangeRoleActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "updateRoleAndLanguage" -> {
                            try {
                                val myDataModel: RoleChangeResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    sharedPrefManager.saveRole(myDataModel.data.profileRole)
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


    /** all click handel function ***/
    private fun initOnCLick() {
        viewModel.onClick.observe(this@ChangeRoleActivity) {
            when (it?.id) {
                R.id.btnContinue -> {
                    if (apiCallType == 1) {
                        apiCall(1)
                    } else {
                        apiCall(2)
                    }

                }

                R.id.ivBack -> {
                    finish()
                }
                // english button click
                R.id.clEnglish -> {
                    language = "en"
                    binding.selectType = 1
                }
                // italian button click
                R.id.clItalian -> {
                    language = "it"
                    binding.selectType = 2
                }

                // Roommate button click
                R.id.clRoommate -> {
                    roleType = 1
                    binding.selectType = 1
                    Constants.userType = 1

                }
                // Home button click
                R.id.clHome -> {
                    roleType = 2
                    binding.selectType = 2
                    Constants.userType = 2

                }
                // Offering button click
                R.id.clOffering -> {
                    roleType = 3
                    binding.selectType = 3
                    Constants.userType = 3

                }
            }
        }
    }
}