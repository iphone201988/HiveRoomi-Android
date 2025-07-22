package com.tech.hive.ui.for_room_mate.settings

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.base.utils.showSuccessToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.SettingsModel
import com.tech.hive.databinding.ActivitySettingsBinding
import com.tech.hive.databinding.DialogDeleteLogoutBinding
import com.tech.hive.databinding.SettingsItemViewBinding
import com.tech.hive.ui.auth.AuthActivity
import com.tech.hive.ui.for_room_mate.settings_screen.CommunityActivity
import com.tech.hive.ui.for_room_mate.settings_screen.FrequentQuestionsActivity
import com.tech.hive.ui.for_room_mate.settings_screen.HelpActivity
import com.tech.hive.ui.for_room_mate.settings_screen.SafetyTipsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {
    private val viewModel: SettingsVM by viewModels()
    private lateinit var settingsAdapter: SimpleRecyclerViewAdapter<SettingsModel, SettingsItemViewBinding>
    private var logoutDeleteDialog: BaseCustomDialog<DialogDeleteLogoutBinding>? = null
    override fun getLayoutResource(): Int {
        return R.layout.activity_settings
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // view
        initView()
        // click
        initOnClick()
        // observer
        initObserver()

    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@SettingsActivity)
        BindingUtils.statusBarTextColor(this@SettingsActivity, false)
        // adapter
        initAdapter()
        // dialog
        initDialog()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // back button
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }
                // share button
                R.id.tvShare -> {

                }

            }
        }
    }


    /** dialog **/
    private fun initDialog() {
        logoutDeleteDialog =
            BaseCustomDialog(this@SettingsActivity, R.layout.dialog_delete_logout) {
                when (it?.id) {
                    R.id.tvCancel -> {
                        logoutDeleteDialog?.dismiss()
                    }

                    R.id.tvLogout -> {
                        val data = HashMap<String, Any>()
                        viewModel.logoutApiCall(Constants.LOGOUT, data)
                    }
                }

            }
        logoutDeleteDialog?.setCancelable(false)
    }


    /**dialog text change for logout and delete**/
    private fun showDialogItem(type: Int) {
        if (type == 2) {
            logoutDeleteDialog?.binding?.apply {
                tvTitle.text = getString(R.string.delete_account)
                tvSubHeading.text = getString(R.string.are_you_sure_to_delete_account)
                tvLogout.text = getString(R.string.delete)
            }

        } else {
            logoutDeleteDialog?.binding?.apply {
                tvTitle.text = getString(R.string.logout)
                tvSubHeading.text = getString(R.string.are_you_sure_to_logout)
                tvLogout.text = getString(R.string.logout)
            }

        }
        logoutDeleteDialog?.show()

    }


    /** handle api response **/
    private fun initObserver() {
        viewModel.settingObserver.observe(this@SettingsActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {

                        "logoutApiCall" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                    sharedPrefManager.clear()
                                    logoutDeleteDialog?.dismiss()
                                    val intent =
                                        Intent(this@SettingsActivity, AuthActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    startActivity(intent)
                                    finishAffinity()
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

    /** handle adapter **/
    private fun initAdapter() {
        settingsAdapter =
            SimpleRecyclerViewAdapter(R.layout.settings_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.tvTitle, R.id.ivArrow -> {
                        when (pos) {
                            0 -> {
                                startActivity(Intent(this, HelpActivity::class.java))

                            }

                            1 -> {
                                val intent =
                                    Intent(this@SettingsActivity, CommunityActivity::class.java)
                                intent.putExtra("typeCommunity", "community")
                                startActivity(intent)
                            }

                            2 -> {
                                startActivity(Intent(this, SafetyTipsActivity::class.java))
                            }

                            3 -> {
                                showToast(m.title)
                            }

                            4 -> {
                                val intent =
                                    Intent(this@SettingsActivity, CommunityActivity::class.java)
                                intent.putExtra("typeCommunity", "privacy")
                                startActivity(intent)
                            }

                            5 -> {
                                showToast(m.title)
                            }

                            6 -> {
                                startActivity(Intent(this, FrequentQuestionsActivity::class.java))
                            }

                            7 -> {
                                showToast(m.title)
                            }

                            8 -> {
                                showDialogItem(1)
                            }

                            9 -> {
                                showDialogItem(2)
                            }
                        }
                    }
                }
            }
        settingsAdapter.list = getList()
        binding.rvSettings.adapter = settingsAdapter
    }

    //// settings List
    private fun getList(): ArrayList<SettingsModel> {
        val list = ArrayList<SettingsModel>()
        list.add(
            SettingsModel(
                getString(R.string.contact_us), getString(R.string.help_and_assistance), true
            )
        )
        list.add(
            SettingsModel(
                getString(R.string.community), getString(R.string.community_guidelines), true
            )
        )
        list.add(
            SettingsModel(
                getString(R.string.community), getString(R.string.safety_tips), false
            )
        )
        list.add(
            SettingsModel(
                getString(R.string.privacy), getString(R.string.cookie_policy), true
            )
        )
        list.add(
            SettingsModel(
                getString(R.string.privacy), getString(R.string.privacy_policy), false
            )
        )
        list.add(
            SettingsModel(
                getString(R.string.privacy), getString(R.string.privacy_preferences), false
            )
        )
        list.add(SettingsModel(getString(R.string.legal_notes), getString(R.string.licenses), true))
        list.add(
            SettingsModel(
                getString(R.string.legal_notes), getString(R.string.terms_of_use), false
            )
        )
        list.add(SettingsModel(getString(R.string.account), getString(R.string.logout), true))
        list.add(
            SettingsModel(
                getString(R.string.account), getString(R.string.delete_account), false
            )
        )
        return list
    }


}