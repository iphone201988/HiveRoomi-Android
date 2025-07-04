package com.tech.hive.ui.for_room_mate.home

import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.CompatibilityModel
import com.tech.hive.databinding.ActivityMatchedProfileBinding
import com.tech.hive.databinding.ApartmentImageItemViewBinding
import com.tech.hive.databinding.CompatibilityItemViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchedProfileActivity : BaseActivity<ActivityMatchedProfileBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()

    // adapter
    private lateinit var compatibilityAdapter: SimpleRecyclerViewAdapter<CompatibilityModel, CompatibilityItemViewBinding>
    private lateinit var apartmentImageAdapter: SimpleRecyclerViewAdapter<CompatibilityModel, ApartmentImageItemViewBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_matched_profile
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
        BindingUtils.statusBarStyle(this@MatchedProfileActivity)
        BindingUtils.statusBarTextColor(this@MatchedProfileActivity, false)
        // adapter
        initAdapter()
        // get data intent
        val intent = intent.getStringExtra("matchType")
        if (intent != null) {
            if (intent.isNotEmpty() && intent.contains("before")) {
                binding.match = 1
            } else {
                binding.match = 2
            }

        }
    }

    /** handle click **/
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

    }

    /** handle adapter **/
    private fun initAdapter() {
        compatibilityAdapter =
            SimpleRecyclerViewAdapter(R.layout.compatibility_item_view, BR.bean) { v, m, pos ->

            }
        compatibilityAdapter.list = getList()
        binding.rvCompatibility.adapter = compatibilityAdapter

        apartmentImageAdapter =
            SimpleRecyclerViewAdapter(R.layout.apartment_image_item_view, BR.bean) { v, m, pos ->

            }
        apartmentImageAdapter.list = getList()
        binding.rvApartmentImage.adapter = apartmentImageAdapter
    }

    // get List
    private fun getList(): ArrayList<CompatibilityModel> {
        val list = ArrayList<CompatibilityModel>()
        list.add(CompatibilityModel("You both prefer no parties"))
        list.add(CompatibilityModel("Similar cleanliness level"))
        list.add(CompatibilityModel("Both OK with pets"))
        list.add(CompatibilityModel("Sleep schedules align"))
        list.add(CompatibilityModel("Sleep schedules align"))
        list.add(CompatibilityModel("Sleep schedules align"))
        list.add(CompatibilityModel("Sleep schedules align"))
        return list
    }

}