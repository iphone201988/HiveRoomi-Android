package com.tech.hive.ui.for_room_mate.home.second

import androidx.activity.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.MatchProfileModel
import com.tech.hive.data.model.SecondMatchProfileModel
import com.tech.hive.databinding.ActivitySecondMatchBinding
import com.tech.hive.databinding.RvFieldItemBinding
import com.tech.hive.databinding.RvFieldSecondItemBinding
import com.tech.hive.ui.for_room_mate.home.HomeFragmentVM
import com.tech.hive.ui.for_room_mate.home.second.storiesprogressview.StoriesProgressView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SecondMatchActivity : BaseActivity<ActivitySecondMatchBinding>(), OnMapReadyCallback,
    StoriesProgressView.StoriesListener {
    private val viewModel: HomeFragmentVM by viewModels()
    private val progressCount = 6


    private var counter = 0
    private val resources = intArrayOf(
        R.drawable.dummy_person,
        R.drawable.ic_match_dummy,
        R.drawable.dummy_person,
        R.drawable.ic_match_dummy,
        R.drawable.dummy_person,
        R.drawable.ic_match_dummy,
    )


    // adapter
    private lateinit var matchAdapter: SimpleRecyclerViewAdapter<MatchProfileModel, RvFieldItemBinding>
    private lateinit var secondMatchAdapter: SimpleRecyclerViewAdapter<SecondMatchProfileModel, RvFieldSecondItemBinding>


    override fun getLayoutResource(): Int {
        return R.layout.activity_second_match
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

        binding.storiesProgressView.setStoriesCount(progressCount)
        binding.storiesProgressView.setStoryDuration(3000L)
        binding.storiesProgressView.setStoriesListener(this)
        binding.storiesProgressView.startStories(counter)
        binding.ivImage.setImageResource(resources[counter])
    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@SecondMatchActivity)
        BindingUtils.statusBarTextColor(this@SecondMatchActivity, false)
        // adapter
        initAdapter()
        // get data intent
        val intent = intent.getStringExtra("secondMatchType")
        if (intent != null) {
            if (intent.isNotEmpty() && intent.contains("before")) {
                binding.secondMatchType = 1
            } else {
                binding.secondMatchType = 2
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

                R.id.reverse -> {
                    binding.storiesProgressView.reverse()
                }

                R.id.skip -> {
                    binding.storiesProgressView.skip()
                }
            }
        }
    }

    /** handle api response **/
    private fun initObserver() {

    }

    /** handle adapter **/
    private fun initAdapter() {
        matchAdapter = SimpleRecyclerViewAdapter(R.layout.rv_field_item, BR.bean) { v, m, pos ->

        }
        matchAdapter.list = getList()
        binding.rvField.adapter = matchAdapter
        // second match adapter
        secondMatchAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_field_second_item, BR.bean) { v, m, pos ->

            }
        secondMatchAdapter.list = getSecondList()
        binding.rvSecondField.adapter = secondMatchAdapter


    }


    // get List
    private fun getSecondList(): ArrayList<SecondMatchProfileModel> {
        val list = ArrayList<SecondMatchProfileModel>()
        list.add(SecondMatchProfileModel("", "", "Verification & Host Info", true))
        list.add(SecondMatchProfileModel("Verification Status", "Verified", "", false))
        list.add(SecondMatchProfileModel("Host ", "Anna R.", "", false))
        list.add(SecondMatchProfileModel("Ratings", "4.4 (24 reviews) ratings", "", false))
        list.add(SecondMatchProfileModel("", "", "Actions", true))


        return list
    }

    // get List
    private fun getList(): ArrayList<MatchProfileModel> {
        val list = ArrayList<MatchProfileModel>()
        list.add(MatchProfileModel("Price", "$600/month", "", false))
        list.add(MatchProfileModel("Utilities", "$50/month", "", false))
        list.add(MatchProfileModel("Deposit", "$900", "", false))
        list.add(MatchProfileModel("Contract", "6 months", "", false))
        list.add(MatchProfileModel("Available from", "May 10", "Room Details", false))
        list.add(MatchProfileModel("", "", "Room Details", true))
        list.add(MatchProfileModel("Type", "Double Room", "", false))
        list.add(MatchProfileModel("Size", "20 sqm", "", false))
        list.add(MatchProfileModel("Furnished", "Yes", "", false))
        list.add(MatchProfileModel("", "", "Apartment Features", true))
        list.add(MatchProfileModel("Floor", "2nd Floor", "", false))
        list.add(MatchProfileModel("Elevator", "Yes", "", false))
        list.add(MatchProfileModel("Heating", "Independent", "", false))
        list.add(MatchProfileModel("Amenities", "Balcony, Laundry, Parking", "", false))
        list.add(MatchProfileModel("", "", "Household & Lifestyle", true))
        list.add(MatchProfileModel("Current Roommates", "1, M(25)", "", false))
        list.add(MatchProfileModel("Looking for", "Student or Young Worker", "", false))
        list.add(MatchProfileModel("House Rules", " No Smoking| Pets Allowed", "", false))
        list.add(MatchProfileModel("Cleanliness", "4/5", "", false))
        list.add(MatchProfileModel("Compatibility Score", "87%", "", false))
        list.add(MatchProfileModel("", "", "Location & Connectivity", true))
        list.add(MatchProfileModel("Area", "Navigli, Milan", "", false))

        return list
    }

    override fun onMapReady(p0: GoogleMap) {

    }

    override fun onNext() {
        binding.ivImage.setImageResource(resources[++counter])
    }

    override fun onPrev() {
        if ((counter - 1) < 0) return
        binding.ivImage.setImageResource(resources[--counter])
    }

    override fun onComplete() {

    }

    override fun onDestroy() {
        // Very important !
        binding.storiesProgressView.destroy()
        super.onDestroy()
    }

}