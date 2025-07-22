package com.tech.hive.ui.for_room_mate.home.second

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
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
import com.tech.hive.base.utils.showSuccessToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.MatchProfileModel
import com.tech.hive.data.model.SecondMatchProfileModel
import com.tech.hive.data.model.SecondMatchProfileResponse
import com.tech.hive.databinding.ActivitySecondMatchBinding
import com.tech.hive.databinding.RvFieldItemBinding
import com.tech.hive.databinding.RvFieldSecondItemBinding
import com.tech.hive.ui.for_room_mate.home.HomeFragmentVM
import com.tech.hive.ui.for_room_mate.home.second.storiesprogressview.StoriesProgressView
import com.tech.hive.ui.for_room_mate.home.third.ThirdMatchActivity
import com.tech.hive.ui.for_room_mate.messages.chat.ChatActivity
import com.tech.hive.ui.for_room_mate.settings_screen.ReportActivity
import com.tech.hive.ui.video.ShowVideoPlayerActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SecondMatchActivity : BaseActivity<ActivitySecondMatchBinding>(), OnMapReadyCallback,
    StoriesProgressView.StoriesListener {
    private val viewModel: HomeFragmentVM by viewModels()
    var profileIdSecond = ""
    var commonId = ""
    var videosUrl = ""
    var chatClick = false
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

        // intent data
        profileIdSecond = intent.getStringExtra("profileIdSecond").toString()
        if (profileIdSecond.isNotEmpty()) {
            val data = HashMap<String, String>()
            viewModel.getMatchedProfileSecond(data, Constants.MATCH_LISTING + "$profileIdSecond")
        }


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

    @SuppressLint("ClickableViewAccessibility")
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


                R.id.ivLikeBefore, R.id.tvLikeBefore -> {
                    val data = HashMap<String, Any>()
                    data["id"] = commonId
                    data["action"] = "like"
                    data["type"] = "listing"
                    viewModel.matchLikeApi(Constants.MATCH_LIKE, data)
                }


                R.id.ivReportBefore, R.id.tvReportBefore -> {
                    val intent = Intent(this@SecondMatchActivity, ReportActivity::class.java)
                    intent.putExtra("reportId", commonId)
                    intent.putExtra("reportType", "listing")
                    startActivity(intent)
                }

                R.id.ivChat, R.id.tvChat -> {
                    if (chatClick) {
                        val intent = Intent(this@SecondMatchActivity, ChatActivity::class.java)
                        intent.putExtra("socketId", commonId)
                        intent.putExtra("matchId", commonId)
                        startActivity(intent)
                    }

                }

                R.id.ivReport, R.id.tvReport -> {
                    val intent = Intent(this@SecondMatchActivity, ReportActivity::class.java)
                    intent.putExtra("reportId", commonId)
                    intent.putExtra("reportType", "listing")
                    startActivity(intent)
                }

                R.id.clVideo->{
                    BindingUtils.preventMultipleClick(it)
                    val intent = Intent(this@SecondMatchActivity, ShowVideoPlayerActivity::class.java)
                    intent.putExtra("videoUrl", videosUrl)
                    startActivity(intent)
                }
            }
        }


        binding.clAfterMatch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val totalWidth = v.width
                val sectionWidth = totalWidth / 3
                val x = event.x.toInt()

                when {
                    x < sectionWidth -> {

                    }
                    x < sectionWidth * 2 -> {
                        if (chatClick) {
                            val intent = Intent(this@SecondMatchActivity, ChatActivity::class.java)
                            intent.putExtra("socketId", commonId)
                            intent.putExtra("matchId", commonId)
                            startActivity(intent)
                        }
                    }
                    else -> {
                        val intent = Intent(this@SecondMatchActivity, ReportActivity::class.java)
                        intent.putExtra("reportId", commonId)
                        intent.putExtra("reportType", "listing")
                        startActivity(intent)
                    }
                }
            }
            true
        }




        binding.clBeforeMatch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val width = v.width
                val clickedX = event.x
                if (clickedX < width / 2) {
                    val data = HashMap<String, Any>()
                    data["id"] = commonId
                    data["action"] = "like"
                    data["type"] = "listing"
                    viewModel.matchLikeApi(Constants.MATCH_LIKE, data)
                } else {
                    val intent = Intent(this@SecondMatchActivity, ReportActivity::class.java)
                    intent.putExtra("reportId", commonId)
                    intent.putExtra("reportType", "listing")
                    startActivity(intent)
                }
            }
            true
        }

    }


    /** handle api response **/
    private fun initObserver() {
        viewModel.observeCommon.observe(this@SecondMatchActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getMatchedProfile" -> {
                            try {
                                val myDataModel: SecondMatchProfileResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    binding.bean = myDataModel.data
                                    commonId = myDataModel.data._id.toString()
                                    videosUrl = myDataModel.data.videos ?: ""
                                    when (myDataModel.likeStatus) {
                                        1 -> {
                                            binding.tvLike.text = getString(R.string.pending)
                                            binding.secondMatchType = 2
                                        }

                                        2 -> {
                                            binding.tvLike.text = getString(R.string.matched)
                                            binding.secondMatchType = 2
                                            chatClick = true
                                        }

                                        3 -> {
                                            binding.tvLike.text = getString(R.string.rejected)
                                            binding.secondMatchType = 2
                                        }

                                        else -> {
                                            binding.secondMatchType = 1
                                        }
                                    }
                                    val imageCount = myDataModel.data.images?.size ?: 0
                                    binding.storiesProgressView.setStoriesCount(imageCount)
                                    binding.storiesProgressView.setStoryDuration(3000L)
                                    binding.storiesProgressView.setStoriesListener(this)
                                    if (imageCount > 0) {
                                        binding.storiesProgressView.startStories()
                                    }

                                    val availableRoomDate =
                                        BindingUtils.formatDateToMonthDay(myDataModel.data.availableFrom)
                                    val smoke =
                                        if (myDataModel.data.smoke?.contains("yes") == true) "\uD83D\uDEAD Yes Smoking" else "\uD83D\uDEAD No Smoking"
                                    val pets =
                                        if (myDataModel.data.pets == true) "\uD83D\uDC36 Pets Allowed" else "\uD83D\uDC36 No Pets"
                                    var houseRule = "$smoke $pets"
                                    var wifi = if (myDataModel.data.wifi == true) "Wifi," else "-"
                                    var kitchen =
                                        if (myDataModel.data.wifi == true) "Kitchen," else ""
                                    var washingMachine =
                                        if (myDataModel.data.wifi == true) "Washing Machine," else ""
                                    var airConditioner =
                                        if (myDataModel.data.wifi == true) "Air Conditioner," else ""
                                    var balcony =
                                        if (myDataModel.data.wifi == true) "Balcony" else ""
                                    var parking =
                                        if (myDataModel.data.wifi == true) "Parking" else ""
                                    var amenities =
                                        "$wifi $kitchen $washingMachine $airConditioner $balcony $parking"
                                    val roommatesDisplay =
                                        myDataModel.data.roommates?.joinToString(", ") { roommate ->
                                            val gender =
                                                roommate?.gender.orEmpty().take(1).lowercase()
                                            val age = roommate?.age?.toString().orEmpty()
                                            "$gender($age)"
                                        }.orEmpty()


                                    val detailsList = listOf(
                                        MatchProfileModel(
                                            "Price",
                                            "$" + myDataModel.data.price?.toString()
                                                .orEmpty() + "/month",
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            "Utilities",
                                            "$" + myDataModel.data.utilitiesPrice?.toString()
                                                .orEmpty() + "/month",
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            "Deposit",
                                            "$" + myDataModel.data.deposit?.toString().orEmpty(),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            "Contract",
                                            myDataModel.data.contractLength.orEmpty(),
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
                                            myDataModel.data.roomType.orEmpty(),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            "Size",
                                            myDataModel.data.size.orEmpty() + "sqm",
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            "Furnished",
                                            myDataModel.data.furnishingStatus.orEmpty(),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel("", "", "Apartment Features", true),
                                        MatchProfileModel(
                                            "Floor",
                                            myDataModel.data.floor.orEmpty() + "Floor",
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            "Elevator",
                                            if (myDataModel.data.elevator == true) "Yes" else "No",
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            "Heating",
                                            myDataModel.data.address.orEmpty(),
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
                                            myDataModel.data.lookingFor?.joinToString(", ")
                                                .orEmpty(),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel("House Rules", houseRule, "", false),
                                        MatchProfileModel(
                                            "Cleanliness",
                                            myDataModel.data.cleanliness?.toString()
                                                .orEmpty() + "/5",
                                            "",
                                            false
                                        ),
                                        MatchProfileModel("", "", "Location & Connectivity", true),
                                        MatchProfileModel(
                                            "Area",
                                            myDataModel.data.address.orEmpty(),
                                            "",
                                            false
                                        ),
                                    )

                                    val hostList = listOf(
                                        SecondMatchProfileModel(
                                            "",
                                            "",
                                            "Verification & Host Info",
                                            true
                                        ),
                                        SecondMatchProfileModel(
                                            "Verification Status",
                                            "Verified",
                                            "",
                                            false
                                        ),
                                        SecondMatchProfileModel("Host ", "Anna R.", "", false),
                                        SecondMatchProfileModel("", "", "Actions", true),
                                    )
                                    secondMatchAdapter.list = hostList
                                    matchAdapter.list = detailsList
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                binding.clNested.visibility= View.VISIBLE
                                binding.tvEmpty.visibility= View.GONE
                                hideLoading()
                            }
                        }


                        "matchLikeApi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    chatClick = false
                                    binding.tvLike.text = getString(R.string.pending)
                                    binding.secondMatchType = 2
                                    showSuccessToast("user liked")
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
                    binding.clNested.visibility= View.GONE
                    binding.tvEmpty.visibility= View.VISIBLE
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
        matchAdapter = SimpleRecyclerViewAdapter(R.layout.rv_field_item, BR.bean) { v, m, pos ->

        }

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
        //list.add(SecondMatchProfileModel("Ratings", "4.4 (24 reviews) ratings", "", false))
        list.add(SecondMatchProfileModel("", "", "Actions", true))


        return list
    }


    override fun onMapReady(p0: GoogleMap) {

    }

    override fun onNext() {
        //   binding.ivImage.setImageResource(resources[++counter])
    }

    override fun onPrev() {
        //   if ((counter - 1) < 0) return
        //   binding.ivImage.setImageResource(resources[--counter])
    }

    override fun onComplete() {

    }

    override fun onDestroy() {
        // Very important !
        binding.storiesProgressView.destroy()
        super.onDestroy()
    }

}