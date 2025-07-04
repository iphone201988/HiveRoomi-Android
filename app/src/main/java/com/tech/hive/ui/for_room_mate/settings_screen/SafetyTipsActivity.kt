package com.tech.hive.ui.for_room_mate.settings_screen

import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.SafetyModel
import com.tech.hive.databinding.ActivitySafetyTipsBinding
import com.tech.hive.databinding.SafteyItemViewBinding
import com.tech.hive.ui.for_room_mate.settings.SettingsVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SafetyTipsActivity : BaseActivity<ActivitySafetyTipsBinding>() {
    private val viewModel: SettingsVM by viewModels()

    // adapter
    private lateinit var safetyAdapter: SimpleRecyclerViewAdapter<SafetyModel, SafteyItemViewBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_safety_tips
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
        BindingUtils.statusBarStyle(this@SafetyTipsActivity)
        BindingUtils.statusBarTextColor(this@SafetyTipsActivity, false)
        // adapter
        initAdapter()

    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // back button
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }
                // got it button
                R.id.btnContinue -> {
                    onBackPressedDispatcher.onBackPressed()
                }
            }

        }

    }

    /** handle api response **/
    private fun initObserver() {

    }

    /** handle adapter **/
    private fun initAdapter() {
        safetyAdapter = SimpleRecyclerViewAdapter(R.layout.saftey_item_view, BR.bean) { v, m, pos ->
            when (v.id) {

            }
        }
        safetyAdapter.list = getList()
        binding.rvSafety.adapter = safetyAdapter
    }

    // list
    private fun getList(): ArrayList<SafetyModel> {
        val list = ArrayList<SafetyModel>()
        list.add(
            SafetyModel(
                "Protect Your Info",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            )
        )
        list.add(
            SafetyModel(
                "Protect Your Info",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            )
        )
        list.add(
            SafetyModel(
                "Protect Your Info",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            )
        )
        list.add(
            SafetyModel(
                "Protect Your Info",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            )
        )
        list.add(
            SafetyModel(
                "Protect Your Info",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            )
        )
        list.add(
            SafetyModel(
                "Protect Your Info",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            )
        )
        list.add(
            SafetyModel(
                "Protect Your Info",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            )
        )
        list.add(
            SafetyModel(
                "Protect Your Info",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
            )
        )
        return list
    }

}