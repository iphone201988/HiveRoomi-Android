package com.tech.hive.ui.for_room_mate.home.second

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
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
import com.tech.hive.ui.for_room_mate.messages.chat.ChatActivity
import com.tech.hive.ui.for_room_mate.settings_screen.ReportActivity
import com.tech.hive.ui.video.ShowVideoPlayerActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SecondMatchActivity : BaseActivity<ActivitySecondMatchBinding>() {
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
        profileIdSecond = intent.getStringExtra("profileIdSecond") ?: ""
        if (profileIdSecond.isNotEmpty()) {
            val data = HashMap<String, String>()
            viewModel.getMatchedProfileSecond(data, Constants.MATCH_LISTING + profileIdSecond)
        }

        binding.mapView.setOnTouchListener({ v, event ->
            v.getParent().requestDisallowInterceptTouchEvent(true)
            false
        })
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
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
                    if (currentImageIndex > 0) {
                        currentImageIndex--
                        loadImage(currentImageIndex)

                        binding.storiesProgressView.destroy()
                        binding.storiesProgressView.setStoriesCount(images.size)
                        binding.storiesProgressView.setStoryDuration(3000L)
                        binding.storiesProgressView.startStories(
                            currentImageIndex
                        )
                    }
                }

                R.id.skip -> {
                    if (currentImageIndex < images.size - 1) {
                        currentImageIndex++
                        loadImage(currentImageIndex)

                        binding.storiesProgressView.destroy()
                        binding.storiesProgressView.setStoriesCount(images.size)
                        binding.storiesProgressView.setStoryDuration(3000L)
                        binding.storiesProgressView.startStories(
                            currentImageIndex
                        )
                    }
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

                R.id.clVideo -> {
                    BindingUtils.preventMultipleClick(it)
                    val intent =
                        Intent(this@SecondMatchActivity, ShowVideoPlayerActivity::class.java)
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

    var images = listOf<String>()
    var currentImageIndex = 0

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

                                    val styleUrl =
                                        "https://api.maptiler.com/maps/${Constants.MAP_ID}/style.json?key=${Constants.MAP_API_KEY}"

                                    binding.mapView.getMapAsync { mapboxMap ->
                                        mapboxMap.setStyle(styleUrl) { style ->
                                            mapboxMap.uiSettings.isScrollGesturesEnabled = false
                                            mapboxMap.uiSettings.isZoomGesturesEnabled =
                                                false // (optional)
                                            mapboxMap.uiSettings.isRotateGesturesEnabled =
                                                false // (optional)
                                            mapboxMap.uiSettings.isTiltGesturesEnabled = false
                                            mapboxMap.uiSettings.areAllGesturesEnabled()
                                            val latitude = myDataModel.data.latitude ?: 0.0
                                            val longitude = myDataModel.data.longitude ?: 0.0
                                            val latLng = LatLng(latitude, longitude)
                                            mapboxMap.cameraPosition =
                                                CameraPosition.Builder().target(latLng).zoom(14.0)
                                                    .build()
                                            mapboxMap.addMarker(
                                                com.mapbox.mapboxsdk.annotations.MarkerOptions()
                                                    .position(latLng)
                                                    .title(getString(R.string.map_name))
                                            )
                                        }
                                    }
                                    val availableRoomDate =
                                        BindingUtils.formatDateToMonthDay(myDataModel.data.availableFrom)
                                    val smoke =
                                        if (myDataModel.data.smoke?.contains("yes") == true) getString(
                                            R.string.yes_smoking
                                        ) else getString(
                                            R.string.no_smoking
                                        )
                                    val pets =
                                        if (myDataModel.data.pets == true) getString(
                                            R.string.pets_allowed
                                        ) else getString(
                                            R.string.no_pets
                                        )
                                    var houseRule = "$smoke $pets"
                                    var wifi = if (myDataModel.data.wifi == true) getString(
                                        R.string.wifi
                                    ) else "-"
                                    var kitchen =
                                        if (myDataModel.data.wifi == true) getString(
                                            R.string.kitchen
                                        ) else ""
                                    var washingMachine =
                                        if (myDataModel.data.wifi == true) getString(
                                            R.string.washing_machine
                                        ) else ""
                                    var airConditioner =
                                        if (myDataModel.data.wifi == true) getString(
                                            R.string.air_conditioner
                                        ) else ""
                                    var balcony =
                                        if (myDataModel.data.wifi == true) getString(
                                            R.string.balcony
                                        ) else ""
                                    var parking =
                                        if (myDataModel.data.wifi == true) getString(
                                            R.string.parking
                                        ) else ""
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
                                            getString(
                                                R.string.price
                                            ),
                                            "$" + myDataModel.data.price?.toString()
                                                .orEmpty() + getString(
                                                R.string.per_month
                                            ),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            getString(
                                                R.string.utilities
                                            ),
                                            "$" + myDataModel.data.utilitiesPrice?.toString()
                                                .orEmpty() + getString(
                                                R.string.per_month
                                            ),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            getString(
                                                R.string.deposit
                                            ),
                                            "$" + myDataModel.data.deposit?.toString().orEmpty(),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            getString(
                                                R.string.contract
                                            ),
                                            myDataModel.data.contractLength.orEmpty(),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            getString(
                                                R.string.available_room
                                            ),
                                            availableRoomDate,
                                            getString(
                                                R.string.room_details
                                            ),
                                            false
                                        ),
                                        MatchProfileModel("", "", getString(
                                            R.string.room_details
                                        ), true),
                                        MatchProfileModel(
                                            getString(
                                                R.string.type
                                            ), myDataModel.data.roomType.orEmpty(), "", false
                                        ),
                                        MatchProfileModel(
                                            getString(
                                                R.string.size
                                            ),
                                            myDataModel.data.size.orEmpty() + getString(
                                                R.string.sqm
                                            ),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            getString(
                                                R.string.furnished
                                            ),
                                            myDataModel.data.furnishingStatus.orEmpty(),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel("", "", getString(
                                            R.string.apartment_features
                                        ), true),
                                        MatchProfileModel(
                                            getString(
                                                R.string.floor
                                            ),
                                            myDataModel.data.floor.orEmpty() + getString(
                                                R.string.floor
                                            ),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            getString(
                                                R.string.elevator
                                            ),
                                            if (myDataModel.data.elevator == true) getString(
                                                R.string.yes
                                            ) else getString(
                                                R.string.no
                                            ),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(
                                            getString(
                                                R.string.heating
                                            ), myDataModel.data.address.orEmpty(), "", false
                                        ),
                                        MatchProfileModel(getString(
                                            R.string.amenities
                                        ), amenities, "", false),
                                        MatchProfileModel("", "", getString(
                                            R.string.household_lifestyle
                                        ), true),
                                        MatchProfileModel(
                                            getString(
                                                R.string.current_roommates
                                            ), roommatesDisplay, "", false
                                        ),
                                        MatchProfileModel(
                                            getString(
                                                R.string.looking_for
                                            ),
                                            myDataModel.data.lookingFor?.joinToString(", ")
                                                .orEmpty(),
                                            "",
                                            false
                                        ),
                                        MatchProfileModel(getString(
                                            R.string.house_rules
                                        ), houseRule, "", false),
                                        MatchProfileModel(
                                            getString(
                                                R.string.cleanliness
                                            ),
                                            myDataModel.data.cleanliness?.toString()
                                                .orEmpty() + "/5",
                                            "",
                                            false
                                        ),
                                        MatchProfileModel("", "", getString(
                                            R.string.location_connectivity
                                        ), true),
                                        MatchProfileModel(
                                            getString(
                                                R.string.area
                                            ), myDataModel.data.address.orEmpty(), "", false
                                        ),
                                    )

                                    val hostList = listOf(
                                        SecondMatchProfileModel(
                                            "", "", getString(R.string.verification_host_info), true
                                        ),
                                        SecondMatchProfileModel(
                                            getString(R.string.verification_status),
                                            getString(R.string.verified), "", false
                                        ),
                                        SecondMatchProfileModel(getString(R.string.host), "Anna R.", "", false),
                                        SecondMatchProfileModel("", "",
                                            getString(R.string.actions), true),
                                    )
                                    secondMatchAdapter.list = hostList
                                    matchAdapter.list = detailsList


                                    // status
                                    images =
                                        (myDataModel.data.images ?: emptyList()) as List<String>
                                    if (images.isNotEmpty()) {
                                        loadImage(currentImageIndex)

                                        binding.storiesProgressView.setStoriesCount(images.size)
                                        binding.storiesProgressView.setStoryDuration(3000L)
                                        binding.storiesProgressView.setStoriesListener(object :
                                            StoriesProgressView.StoriesListener {
                                            override fun onNext() {
                                                currentImageIndex++
                                                if (currentImageIndex < images.size) {
                                                    loadImage(currentImageIndex)
                                                }
                                            }

                                            override fun onPrev() {
                                                currentImageIndex =
                                                    (currentImageIndex - 1).coerceAtLeast(0)
                                                loadImage(currentImageIndex)
                                            }

                                            override fun onComplete() {
                                                // Optional: loop or reset
                                            }
                                        })

                                        binding.storiesProgressView.startStories(currentImageIndex)
                                    }


                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                // binding.clNested.visibility = View.VISIBLE
                                binding.tvEmpty.visibility = View.GONE
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
                    // binding.clNested.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {

                }
            }
        }
    }

    fun loadImage(index: Int) {
        if (index in images.indices) {
            Glide.with(binding.ivImage.context).load(Constants.BASE_URL_IMAGE + images[index])
                .placeholder(R.drawable.progress_animation_small).error(R.drawable.home_dummy_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.ivImage)
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

        binding.rvSecondField.adapter = secondMatchAdapter


    }





    override fun onDestroy() {
        // Very important !
        binding.storiesProgressView.destroy()
        binding.mapView.onDestroy()
        super.onDestroy()
    }

}