package com.tech.hive.ui.room_offering.discover

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.DiscoverAnswerModel
import com.tech.hive.data.model.DiscoverQuestionModel
import com.tech.hive.databinding.ActivityDiscoverySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverySettingsActivity : BaseActivity<ActivityDiscoverySettingsBinding>() {
    private val viewModel: DiscoverySettingsActivityVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_discovery_settings
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@DiscoverySettingsActivity)
        BindingUtils.statusBarTextColor(this@DiscoverySettingsActivity, false)
        // click
        initOnCLick()
        // adapter
        val adapter = DiscoverQuestion(getQuestionSecondList())
        binding.rvDiscover.layoutManager = LinearLayoutManager(this)
        binding.rvDiscover.adapter = adapter

    }

    /*** all click handel  **/
    private fun initOnCLick() {
        viewModel.onClick.observe(this@DiscoverySettingsActivity) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.btnApply -> {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }


    // question List
    private fun getQuestionSecondList(): ArrayList<DiscoverQuestionModel> {
        val list = ArrayList<DiscoverQuestionModel>()
        list.add(
            DiscoverQuestionModel(
                question = "Amenities", answer = listOf(
                    DiscoverAnswerModel("Private Bathroom"),
                    DiscoverAnswerModel("Fast Wi-Fi (smart working/gaming)"),
                    DiscoverAnswerModel("Equipped Kitchen"),
                    DiscoverAnswerModel("Independent Heating"),
                    DiscoverAnswerModel("Washing Machine"),
                    DiscoverAnswerModel("Air Conditioning"),
                    DiscoverAnswerModel("Private")
                )
            )
        )
        list.add(
            DiscoverQuestionModel(
                question = "Property Features", answer = listOf(
                    DiscoverAnswerModel("Balcony/terrace"),
                    DiscoverAnswerModel("Parking Space"),
                    DiscoverAnswerModel("Furnished")
                )
            )
        )
        return list
    }
}