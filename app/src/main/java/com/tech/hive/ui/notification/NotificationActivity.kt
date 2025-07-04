package com.tech.hive.ui.notification

import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.NotificationModel
import com.tech.hive.databinding.ActivityNotificationBinding
import com.tech.hive.databinding.NotificationItemViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>() {
    private val viewModel: NotificationActivityVM by viewModels()

    // notification adapter
    private lateinit var notificationAdapter: SimpleRecyclerViewAdapter<NotificationModel, NotificationItemViewBinding>
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
    }

    /** handle view **/
    private fun initView() {
        // status bar styling
        // set status bar color
        BindingUtils.statusBarStyle(this@NotificationActivity)
        BindingUtils.statusBarTextColor(this@NotificationActivity,false)
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
        viewModel.notificationObserver.observe(this) {

        }
    }

    /** handle adapter **/
    private fun initAdapter() {
        notificationAdapter =
            SimpleRecyclerViewAdapter(R.layout.notification_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    // btn Accept
                    R.id.btnAccept -> {
                        showToast(getString(R.string.accept))
                    }
                    // reject
                    R.id.btnReject -> {
                        showToast(getString(R.string.reject))
                    }

                }
            }
        notificationAdapter.list = getList()
        binding.rvNotification.adapter=notificationAdapter
    }

    // list
    private fun getList(): ArrayList<NotificationModel> {
        val list = ArrayList<NotificationModel>()
        list.add(NotificationModel(1, "Bonnie started following you", null))
        list.add(NotificationModel(1, "Bonnie started following you", null))
        list.add(NotificationModel(2, "Congratulations! You and Bonnie matched!", "80%"))
        list.add(NotificationModel(1, "Bonnie started following you", null))
        list.add(NotificationModel(1, "Bonnie started following you", null))
        list.add(NotificationModel(2, "Congratulations! You and Bonnie matched!", "60%"))
        list.add(NotificationModel(2, "Congratulations! You and Bonnie matched!", "50%"))
        list.add(NotificationModel(1, "Bonnie started following you", null))
        return list
    }

}