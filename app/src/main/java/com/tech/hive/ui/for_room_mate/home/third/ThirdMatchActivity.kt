package com.tech.hive.ui.for_room_mate.home.third

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import androidx.activity.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.GetListingData
import com.tech.hive.data.model.MatchProfileModel
import com.tech.hive.data.model.SecondMatchProfileModel
import com.tech.hive.databinding.ActivityThirdMatchBinding
import com.tech.hive.databinding.RvFieldItemBinding
import com.tech.hive.databinding.RvThirdFieldDataItemBinding
import com.tech.hive.ui.for_room_mate.home.HomeFragmentVM
import com.tech.hive.ui.for_room_mate.home.second.SecondMatchActivity
import com.tech.hive.ui.for_room_mate.home.second.storiesprogressview.StoriesProgressView
import com.tech.hive.ui.room_offering.basic_details.BasicDetailsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThirdMatchActivity : BaseActivity<ActivityThirdMatchBinding>(), OnMapReadyCallback,
    StoriesProgressView.StoriesListener {
    private val viewModel: HomeFragmentVM by viewModels()
    private var listingData: GetListingData? = null
    // adapter
    private lateinit var matchAdapter: SimpleRecyclerViewAdapter<MatchProfileModel, RvThirdFieldDataItemBinding>

    override fun getLayoutResource(): Int {
        return R.layout.activity_third_match
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@ThirdMatchActivity)
        BindingUtils.statusBarTextColor(this@ThirdMatchActivity, false)
        // click
        initOnClick()
        // adapter
        initAdapter()
        // get intent data
        listingData = intent.getParcelableExtra<GetListingData>("basicDetail")
        listingData?.let {
            binding.bean = listingData
            val imageCount =listingData?.images?.size ?: 0
            binding.storiesProgressView.setStoriesCount(imageCount)
            binding.storiesProgressView.setStoryDuration(3000L)
            binding.storiesProgressView.setStoriesListener(this)
            if (imageCount > 0) {
                binding.storiesProgressView.startStories()
            }

            val availableRoomDate = BindingUtils.formatDateToMonthDay(listingData?.availableFrom)

            val smoke = if (listingData?.smoke?.contains("yes") == true) "\uD83D\uDEAD Yes Smoking" else "\uD83D\uDEAD No Smoking"
            val pets =
                if (listingData?.pets == true) "\uD83D\uDC36 Pets Allowed" else "\uD83D\uDC36 No Pets"
            var houseRule = "$smoke $pets"
            var wifi = if (listingData?.wifi == true) "Wifi," else "-"
            var kitchen = if (listingData?.kitchen == true) "Kitchen," else ""
            var washingMachine = if (listingData?.washingMachine == true) "Washing Machine," else ""
            var airConditioner = if (listingData?.airConditioner == true) "Air Conditioner," else ""
            var balcony = if (listingData?.balcony == true) "Balcony" else ""
            var parking = if (listingData?.parking == true) "Parking" else ""
            var amenities = "$wifi $kitchen $washingMachine $airConditioner $balcony $parking"
            val roommatesDisplay =
                listingData?.roommates?.joinToString(", ") { roommate ->
                    val gender =
                        roommate?.gender.orEmpty().take(1).lowercase()
                    val age = roommate?.age?.toString().orEmpty()
                    "$gender($age)"
                }.orEmpty()


            val detailsList = listOf(
                MatchProfileModel(
                    "Price",
                    "$" + listingData?.price?.toString()
                        .orEmpty() + "/month",
                    "",
                    false
                ),
                MatchProfileModel(
                    "Utilities",
                    "$" + listingData?.utilitiesPrice?.toString()
                        .orEmpty() + "/month",
                    "",
                    false
                ),
                MatchProfileModel(
                    "Deposit",
                    "$" + listingData?.deposit?.toString().orEmpty(),
                    "",
                    false
                ),
                MatchProfileModel(
                    "Contract",
                    listingData?.contractLength.orEmpty(),
                    "",
                    false
                ),
                MatchProfileModel(
                    "Available Room",
                    availableRoomDate,
                    "Room Details",
                    false
                ),
                MatchProfileModel("", "", "Room Details", true),
                MatchProfileModel(
                    "Type",
                    listingData?.roomType.orEmpty(),
                    "",
                    false
                ),
                MatchProfileModel(
                    "Size",
                    listingData?.size.orEmpty() + " sqm",
                    "",
                    false
                ),
                MatchProfileModel(
                    "Furnished",
                    listingData?.furnishingStatus.orEmpty(),
                    "",
                    false
                ),
                MatchProfileModel("", "", "Apartment Features", true),
                MatchProfileModel(
                    "Floor",
                    listingData?.floor.orEmpty() + "Floor",
                    "",
                    false
                ),
                MatchProfileModel(
                    "Elevator",
                    if (listingData?.elevator == true) "Yes" else "No",
                    "",
                    false
                ),
                MatchProfileModel(
                    "Heating",
                    listingData?.heating.orEmpty(),
                    "",
                    false
                ),
                MatchProfileModel("Amenities", amenities, "", false),
                MatchProfileModel("", "", "Household & Lifestyle", true),
                MatchProfileModel(
                    "Current Roommates",
                    roommatesDisplay,
                    "",
                    false
                ),
                MatchProfileModel(
                    "Looking For",
                    listingData?.lookingFor?.joinToString(", ")
                        .orEmpty(),
                    "",
                    false
                ),
                MatchProfileModel("House Rules", houseRule, "", false),
                MatchProfileModel(
                    "Cleanliness",
                    listingData?.cleanliness?.toString()
                        .orEmpty() + "/5",
                    "",
                    false
                ),
                MatchProfileModel("", "", "Location & Connectivity", true),
                MatchProfileModel(
                    "Area",
                    listingData?.address.toString(),
                    "",
                    false
                ),
            )

            matchAdapter.list = detailsList

        }


        // observer
        initObserver()
    }

    /** handle adapter **/
    private fun initAdapter() {
        matchAdapter = SimpleRecyclerViewAdapter(R.layout.rv_third_field_data_item, BR.bean) { v, m, pos ->

        }

        binding.rvField.adapter = matchAdapter
    }

    /** handle click **/
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(this@ThirdMatchActivity) {
            when (it?.id) {
                R.id.ivBack -> {
                    finish()
                }
            }
        }
            binding.ivLikeBefore.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val width = v.width
                    val clickedX = event.x
                    if (clickedX < width / 2) {
                        val intent =
                            Intent(this@ThirdMatchActivity, BasicDetailsActivity::class.java)
                        if (listingData != null) {
                            intent.putExtra("basicDetail", listingData)
                        }
                        startActivity(intent)
                    } else {
                        listingData?._id?.let { id ->
                            viewModel.deleteListingList("listing/$id")
                        }
                    }
                }
                true
            }


    }


    /** handle api response **/
    private fun initObserver() {
        viewModel.observeCommon.observe(this@ThirdMatchActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "deleteListingList" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
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

    override fun onMapReady(p0: GoogleMap) {

    }

    override fun onNext() {

    }

    override fun onPrev() {

    }

    override fun onComplete() {

    }
}