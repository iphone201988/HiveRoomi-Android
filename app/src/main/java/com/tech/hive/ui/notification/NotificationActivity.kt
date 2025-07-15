package com.tech.hive.ui.notification

import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.GetNotificationResponse
import com.tech.hive.data.model.NotificationData
import com.tech.hive.databinding.ActivityNotificationBinding
import com.tech.hive.databinding.NotificationItemViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>() {
    private val viewModel: NotificationActivityVM by viewModels()

    // notification adapter
    private lateinit var notificationAdapter: SimpleRecyclerViewAdapter<NotificationData, NotificationItemViewBinding>
    private var notificationId = ""
    override fun getLayoutResource(): Int {
        return R.layout.activity_notification
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
        viewModel.getUserNotification(Constants.USER_NOTIFICATION)
    }

    /** handle view **/
    private fun initView() {
        // status bar styling
        // set status bar color
        BindingUtils.statusBarStyle(this@NotificationActivity)
        BindingUtils.statusBarTextColor(this@NotificationActivity, false)
        // adapter
        initAdapter()
    }

    /** handle clicks **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.notificationObserver.observe(this@NotificationActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getUserNotification" -> {
                            try {
                                val myDataModel: GetNotificationResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    notificationAdapter.list = myDataModel.data
                                    if (notificationAdapter.list.isEmpty()) {
                                        binding.tvEmpty.visibility = View.VISIBLE
                                    } else {
                                        binding.tvEmpty.visibility = View.GONE
                                    }
                                }
                            } catch (e: Exception) {

                                Log.e("error", "getHomeApi: $e")
                            } finally {

                                hideLoading()
                            }
                        }

                        "acceptRejectAPi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    // api call
                                    viewModel.getUserNotification(Constants.USER_NOTIFICATION)
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
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
        notificationAdapter =
            SimpleRecyclerViewAdapter(R.layout.notification_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    // btn Accept
                    R.id.btnAccept -> {
                        val data = HashMap<String, Any>()
                        data["action"] = "accept"    //accept,reject
                        if (m.likeId?.type?.contains("user") == true) {
                            data["id"] = m.likeId._id.toString()
                        } else {
                            data["id"] = m.likeId?._id.toString()
                        }
                        viewModel.acceptRejectAPi(Constants.MATCH_ACCEPT_REJECT, data)
                    }
                    // reject
                    R.id.btnReject -> {

                        val data = HashMap<String, Any>()
                        data["action"] = "reject"   //accept,reject
                        if (m.likeId?.type?.contains("user") == true) {
                            data["id"] = m.likeId._id.toString()
                        } else {
                            data["id"] = m.likeId?._id.toString()
                        }
                        viewModel.acceptRejectAPi(Constants.MATCH_ACCEPT_REJECT, data)
                    }

                }
            }

        binding.rvNotification.adapter = notificationAdapter
    }

}