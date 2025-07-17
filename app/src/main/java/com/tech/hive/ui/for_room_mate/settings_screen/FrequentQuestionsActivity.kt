package com.tech.hive.ui.for_room_mate.settings_screen

import android.util.Log
import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.base.utils.showSuccessToast
import com.tech.hive.data.model.CommonQuestionsData
import com.tech.hive.data.model.CommonQuestionsResponse
import com.tech.hive.databinding.ActivityFrequentQuestionsBinding
import com.tech.hive.databinding.FrequentItemViewBinding
import com.tech.hive.ui.for_room_mate.settings.SettingsVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FrequentQuestionsActivity : BaseActivity<ActivityFrequentQuestionsBinding>() {
    private val viewModel: SettingsVM by viewModels()

    // adapter
    private lateinit var frequentAdapter: SimpleRecyclerViewAdapter<CommonQuestionsData, FrequentItemViewBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_frequent_questions
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
        // api call
        viewModel.askedQuestionsApiCall(com.tech.hive.data.api.Constants.USER_FAX)
    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@FrequentQuestionsActivity)
        BindingUtils.statusBarTextColor(this@FrequentQuestionsActivity, false)
        // adapter
        initAdapter()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                  finish()
                }
            }
        }

    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.settingObserver.observe(this@FrequentQuestionsActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "askedQuestionsApiCall" -> {
                            try {
                                val myDataModel: CommonQuestionsResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    frequentAdapter.list = myDataModel.data
                                    showSuccessToast(myDataModel.message.toString())

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
        frequentAdapter =
            SimpleRecyclerViewAdapter(R.layout.frequent_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clQn -> {
                        showToast("Clicked ${pos + 1}")
                    }
                }
            }

        binding.rvFrequentQn.adapter = frequentAdapter
    }


}