package com.tech.hive.ui.for_room_mate.home.third

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.GetListingData
import com.tech.hive.data.model.MatchProfileModel
import com.tech.hive.databinding.ActivityThirdMatchBinding
import com.tech.hive.databinding.DialogDeleteLogoutBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.RvThirdFieldDataItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import com.tech.hive.ui.for_room_mate.home.HomeFragmentVM
import com.tech.hive.ui.for_room_mate.home.second.SecondMatchActivity
import com.tech.hive.ui.for_room_mate.home.second.storiesprogressview.StoriesProgressView
import com.tech.hive.ui.for_room_mate.settings.SettingsActivity
import com.tech.hive.ui.for_room_mate.settings_screen.ReportActivity
import com.tech.hive.ui.room_offering.basic_details.BasicDetailsActivity
import com.tech.hive.ui.video.ShowVideoPlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class ThirdMatchActivity : BaseActivity<ActivityThirdMatchBinding>(){
    private val viewModel: HomeFragmentVM by viewModels()
    private var listingData: GetListingData? = null
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var ageAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    private var listingId = ""
    private var listingDeleteDialog: BaseCustomDialog<DialogDeleteLogoutBinding>? = null
     var images = listOf<String>()
    var currentImageIndex = 0
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
        binding.mapView.setOnTouchListener({ v, event ->
            v.getParent().requestDisallowInterceptTouchEvent(true)
            false
        })
        // get intent data
        listingData = intent.getParcelableExtra<GetListingData>("basicDetail")
        listingData?.let {
            listingId = it._id.toString()
            binding.bean = listingData

            val likeCount = listingData?.likeCount ?: 0
            val viewCount = listingData?.viewCount ?: 0
            binding.tvFav.text = likeCount.toString() + " " + getString(R.string.view)
            binding.tvViews.text = viewCount.toString() + " " + getString(R.string.matches)

            when (listingData?.status) {
                1 -> {
                    binding.tvRoommate.text = getString(R.string.draft)
                }

                2 -> {
                    binding.tvRoommate.text = getString(R.string.active)
                }

                3 -> {
                    binding.tvRoommate.text = getString(R.string.paused)
                }

                4 -> {
                    binding.tvRoommate.text = getString(R.string.rented)
                }

            }

            val availableRoomDate = BindingUtils.formatDateToMonthDay(listingData?.availableFrom)

            val smoke =
                if (listingData?.smoke?.contains("yes") == true) getString(R.string.yes_smoking) else getString(
                    R.string.no_smoking
                )
            val pets =
                if (listingData?.pets == true) getString(R.string.pets_allowed) else getString(R.string.no_pets)
            var houseRule = "$smoke $pets"
            var wifi = if (listingData?.wifi == true) getString(R.string.wifi) else "-"
            var kitchen = if (listingData?.kitchen == true) getString(R.string.kitchen) else ""
            var washingMachine = if (listingData?.washingMachine == true) getString(R.string.washing_machine) else ""
            var airConditioner = if (listingData?.airConditioner == true) getString(R.string.air_conditioner) else ""
            var balcony = if (listingData?.balcony == true) getString(R.string.balcony) else ""
            var parking = if (listingData?.parking == true) getString(R.string.parking) else ""
            var amenities = "$wifi $kitchen $washingMachine $airConditioner $balcony $parking"
            val roommatesDisplay = listingData?.roommates?.joinToString(", ") { roommate ->
                val gender = roommate?.gender.orEmpty().take(1).lowercase()
                val age = roommate?.age?.toString().orEmpty()
                "$gender($age)"
            }.orEmpty()

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
                    val latitude = listingData!!.latitude ?: 0.0
                    val longitude = listingData!!.longitude ?: 0.0

                    Log.d("dgdfggf", "onCreateView: $latitude ,$longitude")

                    val latLng = LatLng(latitude, longitude)
                    mapboxMap.cameraPosition =
                        CameraPosition.Builder().target(latLng).zoom(10.0)
                            .build()
                    mapboxMap.addMarker(
                        com.mapbox.mapboxsdk.annotations.MarkerOptions()
                            .position(latLng)
                            .title(getString(R.string.map_name))
                    )
                }
            }


            val detailsList = listOf(
                MatchProfileModel(
                    getString(R.string.price), "$" + listingData?.price?.toString().orEmpty() + getString(R.string.per_month), "", false
                ),
                MatchProfileModel(
                    getString(R.string.utilities),
                    "$" + listingData?.utilitiesPrice?.toString().orEmpty() + getString(R.string.per_month),
                    "",
                    false
                ),
                MatchProfileModel(
                    getString(R.string.deposit), "$" + listingData?.deposit?.toString().orEmpty(), "", false
                ),
                MatchProfileModel(
                    getString(R.string.contract), listingData?.contractLength.orEmpty(), "", false
                ),
                MatchProfileModel(
                    getString(R.string.available_room), availableRoomDate, getString(R.string.room_details), false
                ),
                MatchProfileModel("", "", getString(R.string.room_details), true),
                MatchProfileModel(
                    getString(R.string.type), listingData?.roomType.orEmpty(), "", false
                ),
                MatchProfileModel(
                    getString(R.string.size), listingData?.size.orEmpty() + getString(R.string.sqm), "", false
                ),
                MatchProfileModel(
                    getString(R.string.furnished), listingData?.furnishingStatus.orEmpty(), "", false
                ),
                MatchProfileModel("", "", getString(R.string.apartment_features), true),
                MatchProfileModel(
                    getString(R.string.floor), listingData?.floor.orEmpty() + getString(R.string.floor), "", false
                ),
                MatchProfileModel(
                    getString(R.string.elevator), if (listingData?.elevator == true) getString(R.string.yes) else getString(R.string.no), "", false
                ),
                MatchProfileModel(
                    getString(R.string.heating), listingData?.heating.orEmpty(), "", false
                ),
                MatchProfileModel(getString(R.string.amenities), amenities, "", false),
                MatchProfileModel("", "", getString(R.string.household_lifestyle), true),
                MatchProfileModel(
                    getString(R.string.current_roommates), roommatesDisplay, "", false
                ),
                MatchProfileModel(
                    getString(R.string.looking_for), listingData?.lookingFor?.joinToString(", ").orEmpty(), "", false
                ),
                MatchProfileModel(getString(R.string.house_rules), houseRule, "", false),
                MatchProfileModel(
                    getString(R.string.cleanliness), listingData?.cleanliness?.toString().orEmpty() + "/5", "", false
                ),
                MatchProfileModel("", "", getString(R.string.location_connectivity), true),
                MatchProfileModel(
                    getString(R.string.area), listingData?.address.toString(), "", false
                ),
            )

            matchAdapter.list = detailsList

            // status
             images = (listingData!!.images ?: emptyList()) as List<String>

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


        // observer
        initObserver()
    }


    /** load image **/
    fun loadImage(index: Int) {
        if (index in images.indices) {
            Glide.with(binding.ivImage.context)
                .load(Constants.BASE_URL_IMAGE + images[index])
                .placeholder(R.drawable.progress_animation_small)
                .error(R.drawable.home_dummy_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivImage)
        }
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



    /** handle adapter **/
    private fun initAdapter() {
        matchAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_third_field_data_item, BR.bean) { v, m, pos ->

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
                R.id.clRoommate -> {
                    BindingUtils.preventMultipleClick(it)
                    personalDialog()
                }

                R.id.skip -> {
                    BindingUtils.preventMultipleClick(it)
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

                R.id.reverse -> {
                    BindingUtils.preventMultipleClick(it)
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


                R.id.tvInterestedPeoples -> {
                    BindingUtils.preventMultipleClick(it)
                    val intent = Intent(this@ThirdMatchActivity, ViewMatchPeopleActivity::class.java)
                    intent.putExtra("sendTitle", listingData?.title)
                    intent.putExtra("matchId", listingData?._id)
                    startActivity(intent)
                }
                R.id.clVideo->{
                    BindingUtils.preventMultipleClick(it)
                    val intent = Intent(this@ThirdMatchActivity, ShowVideoPlayerActivity::class.java)
                    intent.putExtra("videoUrl", listingData?.videos)
                    startActivity(intent)
                }
            }
        }


        binding.clLikeBefore.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val width = v.width
                val clickedX = event.x
                if (clickedX < width / 2) {
                    val intent = Intent(this@ThirdMatchActivity, BasicDetailsActivity::class.java)
                    if (listingData != null) {
                        intent.putExtra("basicDetail", listingData)
                    }
                    startActivity(intent)
                } else {
                    initDialog()

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
                                    listingDeleteDialog?.dismiss()
                                    finish()
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }
                        "basicDetailEditAPiCall" -> {
                            hideLoading()
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

    /** dialog **/
    private fun initDialog() {
        listingDeleteDialog =
            BaseCustomDialog(this@ThirdMatchActivity, R.layout.dialog_delete_logout) {
                when (it?.id) {
                    R.id.tvCancel -> {
                        listingDeleteDialog?.dismiss()
                    }
                    R.id.tvLogout -> {
                        listingData?._id?.let { id ->
                            viewModel.deleteListingList("listing/$id")
                        }
                    }
                }
            }
        listingDeleteDialog?.setCancelable(false)
        listingDeleteDialog?.show()
        listingDeleteDialog?.binding?.apply {
            tvTitle.text = getString(R.string.delete_listing)
            tvSubHeading.text = getString(R.string.are_you_sure_to_delete_listing)
            tvLogout.text = getString(R.string.delete)
        }
    }


    /** personal dialog  handel ***/
    private fun personalDialog() {
        personal = BaseCustomDialog(this@ThirdMatchActivity, R.layout.personal_dialog_item) {

        }
        personal!!.create()
        personal!!.show()
        // adapter
        initAdapterRoom()
    }

    /** handle adapter **/
    private fun initAdapterRoom() {
        ageAdapter = SimpleRecyclerViewAdapter(R.layout.un_pin_layout, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.consMainUnPin -> {
                    val data = HashMap<String, RequestBody>()
                    when(m){
                        "Draft"->{
                            data["status"] = "1".toRequestBody()
                            viewModel. basicDetailEditAPiCall("listing/$listingId",data)
                        }
                        "Active"->{
                            data["status"] = "2".toRequestBody()
                            viewModel. basicDetailEditAPiCall("listing/$listingId",data)
                        }
                        "Paused"-> {
                            data["status"] = "3".toRequestBody()
                            viewModel.basicDetailEditAPiCall("listing/$listingId", data)
                        }
                        "Rented"->{
                            data["status"] = "4".toRequestBody()
                            viewModel. basicDetailEditAPiCall("listing/$listingId",data)
                        }
                    }

                    binding.tvRoommate.text = m.toString()
                    personal?.dismiss()
                }
            }
        }

        ageAdapter.list = firstTypeList()
        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    private fun firstTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.draft),
            getString(R.string.active),
            getString(R.string.paused),
            getString(R.string.rented),
            )
    }

}