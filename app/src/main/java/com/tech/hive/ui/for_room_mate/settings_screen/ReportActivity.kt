package com.tech.hive.ui.for_room_mate.settings_screen

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
import com.tech.hive.data.model.MatchProfileUserResponse
import com.tech.hive.databinding.ActivityReportBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import com.tech.hive.ui.for_room_mate.settings.SettingsVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportActivity : BaseActivity<ActivityReportBinding>() {

    private val viewModel: SettingsVM by viewModels()
    private var userReportId = ""
    private var reportType = ""
    var reason = ""
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var commonAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_report
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // intent
        var userId = intent.getStringExtra("reportId")
        var reportType1 = intent.getStringExtra("reportType")
        if (userId?.isNotEmpty() == true) {
            userReportId = userId
        }
        reportType = if (reportType1?.isNotEmpty()==true && reportType1.contains("user")){
            "user"
        }else{
            "listing"

        }
        // click
        initClick()
        // observer
        initObserver()
    }


    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(this@ReportActivity) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.tvTitle1, R.id.ivArrow -> {
                    personalDialog()
                }

                R.id.btnContinue -> {
                    if (validate()){
                        val data = HashMap<String, Any>()
                        data["id"] = userReportId
                        data["reason"] = reason
                        data["type"] = reportType
                        data["description"] = binding.etShortBio.text.toString()
                        viewModel.reportAPiCall(Constants.USER_REPORT, data)
                    }

                }

            }
        }


    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.settingObserver.observe(this@ReportActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {

                        "reportAPiCall" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast("user report submitted")
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


    /** personal dialog  handel ***/
    private fun personalDialog() {
        personal = BaseCustomDialog(this@ReportActivity, R.layout.personal_dialog_item) {

        }
        personal!!.create()
        personal!!.show()
        // adapter
        initAdapter()
    }

    /** handle adapter **/
    private fun initAdapter() {
        commonAdapter = SimpleRecyclerViewAdapter(R.layout.un_pin_layout, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.consMainUnPin -> {
                    binding.tvTitle1.text = m.toString()
                    reason = m.toString()
                    personal?.dismiss()
                }
            }
        }

        commonAdapter.list = campusList()
        personal?.binding?.rvPersonal?.adapter = commonAdapter
    }


    private fun campusList(): ArrayList<String> {
        return arrayListOf(
            "Inappropriate behavior", "Spam or scam", "Harassment", "Fake profile", "Other"

        )
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val fullName = binding.etShortBio.text.toString().trim()

        if (reason.isEmpty()) {
            showInfoToast("Please select any reason")
            return false
        }
        if (fullName.isEmpty()) {
            showInfoToast("Please enter description")
            return false
        }
        return true
    }

}