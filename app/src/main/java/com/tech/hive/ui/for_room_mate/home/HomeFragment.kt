package com.tech.hive.ui.for_room_mate.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showSuccessToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.GetListingData
import com.tech.hive.data.model.GetListingResponse
import com.tech.hive.data.model.HomeApiData
import com.tech.hive.data.model.HomeApiResponse
import com.tech.hive.data.model.HomeFilterList
import com.tech.hive.data.model.HomeModelClass
import com.tech.hive.data.model.HomeRoomTData
import com.tech.hive.data.model.HomeRoomType
import com.tech.hive.data.model.RoommateModelClass
import com.tech.hive.databinding.FragmentHomeBinding
import com.tech.hive.databinding.RvDiscoverItemBinding
import com.tech.hive.databinding.RvHomeFilterItemBinding
import com.tech.hive.databinding.ThirdUserRvItemBinding
import com.tech.hive.ui.for_room_mate.calenders.CalenderActivity
import com.tech.hive.ui.for_room_mate.filters.FilterActivity
import com.tech.hive.ui.for_room_mate.filters.filter_source.FilterSource
import com.tech.hive.ui.for_room_mate.home.adapter.HomeSwipeAdapter
import com.tech.hive.ui.for_room_mate.home.adapter.RoommateSwipeAdapter
import com.tech.hive.ui.for_room_mate.home.second.SecondMatchActivity
import com.tech.hive.ui.for_room_mate.home.third.ThirdMatchActivity
import com.tech.hive.ui.notification.NotificationActivity
import com.tech.hive.ui.room_offering.basic_details.BasicDetailsActivity
import com.tech.hive.ui.room_offering.discover.DiscoverySettingsActivity
import com.yalantis.library.KolodaListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    private var currentSource: FilterSource? = null
    private lateinit var commonLauncher: ActivityResultLauncher<Intent>
    private lateinit var homeAdapter: SimpleRecyclerViewAdapter<HomeRoomTData, RvDiscoverItemBinding>
    private lateinit var homeThirdAdapter: SimpleRecyclerViewAdapter<GetListingData, ThirdUserRvItemBinding>
    private lateinit var homeThirdFilterAdapter: SimpleRecyclerViewAdapter<HomeFilterList, RvHomeFilterItemBinding>
    private var userTypeLike = 1
    private var apiCallStop = false
    private var scrollType = ""
    private var searchHandler: Handler? = Handler()
    private var searchRunnable: Runnable? = null
    private var filterOpen = 0
    var progressType = false
    private lateinit var adapter: RoommateSwipeAdapter
    private lateinit var homeAdapterSwipe: HomeSwipeAdapter
    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(view: View) {
        // launcher
        initCommonLauncher()
        // click
        initOnClick()
        // observer
        initObserver()
        // adapter
        initAdapter()
        // search
        searchListing()

        binding.clRoot.setOnTouchListener { v, event ->
            val filter = binding.clFilter
            if (filter.isVisible) {
                val location = IntArray(2)
                filter.getLocationOnScreen(location)
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                val left = location[0]
                val top = location[1]
                val right = left + filter.width
                val bottom = top + filter.height
                if (x < left || x > right || y < top || y > bottom) {
                    filter.visibility = View.GONE
                }
            }
            false
        }

        apiCallFunction()
    }


    /** api call function **/
    private fun apiCallFunction() {
        var userData = sharedPrefManager.getRole()
        if (userData == 1) {
            // api call
            if (apiCallStop == false) {
                val data = HashMap<String, String>()
                viewModel.getHomeApi(Constants.MATCH_LOOKING_ROOMMATE, data)
            }
        } else if (userData == 2) {
            if (apiCallStop == false) {
                // api call
                val data = HashMap<String, String>()
                viewModel.getHomeApiListening(Constants.MATCH_LOOKING_LISTING, data)
            }
        }
    }

    override fun onResume() {
        // api call
        var userData = sharedPrefManager.getRole()
        binding.clFilter.visibility = View.GONE
        binding.userType = userData
        binding.buttonClick = 1
        binding.check = userData
        userTypeLike = userData
        binding.invalidateAll()

        if (userData == 1) {
            filterOpen = 1

        } else if (userData == 2) {
            filterOpen = 2

        } else {
            filterOpen = 0
            // api call
            val data = HashMap<String, String>()
            viewModel.getListing(Constants.LISTING, data)
        }

        if (BindingUtils.userRole == 2) {
            binding.check = 2
            binding.userType = 2
            binding.buttonClick = 1
            userTypeLike = 2
            filterOpen = 2
        } else if (BindingUtils.userRole == 1) {
            binding.check = 1
            binding.userType = 1
            userTypeLike = 1
            filterOpen = 1
        }
        super.onResume()
    }

    /** common launcher **/
    private fun initCommonLauncher() {
        commonLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                when (currentSource) {
                    FilterSource.FILTER -> {
                        val roommate =
                            data?.getParcelableExtra<RoommateModelClass>("filtered_roommate")
                        roommate?.let {
                            apiCallStop = true
                            val requestData = HashMap<String, String>()
                            if (it.gender.isNotBlank()) requestData["gender"] = it.gender
                            if (it.age.isNotBlank()) requestData["ageRange"] = it.age
                            if (it.professionRole.isNotBlank()) requestData["profession"] =
                                it.professionRole
                            if (it.campus.isNotBlank()) requestData["campus"] = it.campus
                       //     if (it.language.isNotBlank()) requestData["language"] = it.language
                            //  if (it.location.isNotBlank()) requestData["location"] = it.location
                            if (it.lat != null) requestData["latitude"] = it.lat.toString()
                            if (it.long != null) requestData["longitude"] = it.long.toString()
//                            requestData["latitude"] = BindingUtils.latitude.toString()
//                            requestData["longitude"] = BindingUtils.longitude.toString()

                            Log.d("gfdfggd", "initCommonLauncher: ")
                            viewModel.getHomeApi(Constants.MATCH_LOOKING_ROOMMATE, requestData)
                        }
                    }

                    FilterSource.DISCOVER -> {
                        val home = data?.getParcelableExtra<HomeModelClass>("filtered_home")
                        home?.let {
                            val requestData = HashMap<String, String>()
                            apiCallStop = true
                            if (it.amenities.contains("Private Bathroom", ignoreCase = true)) {
                                requestData["privateBathroom"] = "true"
                            } else if (it.amenities.contains(
                                    "Fast Wi-Fi (smart working/gaming)", ignoreCase = true
                                )
                            ) {
                                requestData["wifi"] = "true"
                            } else if (it.amenities.contains(
                                    "Equipped Kitchen", ignoreCase = true
                                )
                            ) {
                                requestData["kitchen"] = "true"
                            } else if (it.amenities.contains(
                                    "Washing Machine", ignoreCase = true
                                )
                            ) {
                                requestData["washingMachine"] = "true"
                            } else if (it.amenities.contains(
                                    "Air Conditioning", ignoreCase = true
                                )
                            ) {
                                requestData["airConditioner"] = "true"
                            }

                            if (it.propertyFeatures.contains(
                                    "Balcony / Terrace", ignoreCase = true
                                )
                            ) {
                                requestData["balcony"] = "true"
                            } else if (it.propertyFeatures.contains(
                                    "Parking Space", ignoreCase = true
                                )
                            ) {
                                requestData["parking"] = "true"
                            }


                            val formattedFurnishingStatus = when (it.furnishedStatus) {
                                "fully furnished" -> "fullyfurnished"
                                "semi furnished" -> "partiallyfurnished"
                                "unfurnished" -> "unfurnished"
                                else -> ""
                            }
                            if (formattedFurnishingStatus.isNotBlank()) {
                                requestData["furnishingStatus"] = formattedFurnishingStatus
                            }
//                            requestData["latitude"] = BindingUtils.latitude.toString()
//                            requestData["longitude"] = BindingUtils.longitude.toString()
                            viewModel.getHomeApiListening(
                                Constants.MATCH_LOOKING_LISTING, requestData
                            )
                        }
                    }


                    else -> Unit
                }
            }
        }
    }


    /** handle adapter **/
    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapter() {
        // home adapter
        homeAdapter = SimpleRecyclerViewAdapter(R.layout.rv_discover_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clImage -> {
                    val intent = Intent(context, SecondMatchActivity::class.java)
                    intent.putExtra("profileIdSecond", m._id)
                    startActivity(intent)
                }
            }
        }
        binding.rvHome.adapter = homeAdapter


        homeThirdAdapter =
            SimpleRecyclerViewAdapter(R.layout.third_user_rv_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.ivThreeDot -> {
                        binding.clFilter.visibility = View.GONE
                        val intent = Intent(requireContext(), BasicDetailsActivity::class.java)
                        intent.putExtra("basicDetail", m)
                        startActivity(intent)
                    }

                    R.id.clImage -> {
                        binding.clFilter.visibility = View.GONE
                        val intent = Intent(requireContext(), ThirdMatchActivity::class.java)
                        intent.putExtra("basicDetail", m)
                        startActivity(intent)
                    }
                }
            }

        binding.rvThirdHome.adapter = homeThirdAdapter

        homeThirdFilterAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_home_filter_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clImage -> {
                        binding.clFilter.visibility = View.GONE
                        when (m.name) {
                            "All" -> {
                                val data = HashMap<String, String>()
                                data["status"] = "0"
                                viewModel.getListing(Constants.LISTING, data)
                            }

                            "Draft" -> {
                                val data = HashMap<String, String>()
                                data["status"] = "1"
                                viewModel.getListing(Constants.LISTING, data)
                            }

                            "Active" -> {
                                val data = HashMap<String, String>()
                                data["status"] = "2"
                                viewModel.getListing(Constants.LISTING, data)
                            }

                            "Paused" -> {
                                val data = HashMap<String, String>()
                                data["status"] = "3"
                                viewModel.getListing(Constants.LISTING, data)
                            }

                            "Rented" -> {
                                val data = HashMap<String, String>()
                                data["status"] = "4"
                                viewModel.getListing(Constants.LISTING, data)
                            }
                        }

                        for (i in homeThirdFilterAdapter.list) {
                            i.isSelected = i.name == m.name
                        }
                        homeThirdFilterAdapter.notifyDataSetChanged()
                    }
                }
            }
        homeThirdFilterAdapter.list = filterList()
        binding.rvThirdFilterHome.adapter = homeThirdFilterAdapter


    }

    /*** search Api call **/
    private fun searchListing() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    searchRunnable?.let { searchHandler?.removeCallbacks(it) }
                    searchRunnable = Runnable {
                        progressType = true
                        val data = HashMap<String, String>()
                        data["search"] = s.toString()
                        viewModel.getListing(Constants.LISTING, data)
                    }
                    searchHandler?.postDelayed(searchRunnable!!, 1000)

                } else {
                    val data = HashMap<String, String>()
                    viewModel.getListing(Constants.LISTING, data)
                }
            }

        })
    }


    /** handle click **/
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // filter click
                R.id.ivFilter -> {
                    if (filterOpen == 1) {
                        currentSource = FilterSource.FILTER
                        val intent = Intent(requireContext(), FilterActivity::class.java)
                        commonLauncher.launch(intent)
                    } else if (filterOpen == 2) {
                        currentSource = FilterSource.DISCOVER
                        val intent = Intent(requireContext(), DiscoverySettingsActivity::class.java)
                        commonLauncher.launch(intent)
                    }
                }

                R.id.ivCalender -> {
                    val intent = Intent(requireActivity(), CalenderActivity::class.java)
                    startActivity(intent)
                }
                // bell icon click
                R.id.ivSettings, R.id.ivNotification -> {

                    val intent = Intent(requireActivity(), NotificationActivity::class.java)
                    startActivity(intent)
                }
                // btn CreateNewLis click
                R.id.btnCreateNewLis -> {

                    val intent = Intent(requireActivity(), BasicDetailsActivity::class.java)
                    startActivity(intent)
                }
                // btn roommate click
                R.id.clRoommate -> {
                    scrollType = ""
                    binding.check = 1
                    binding.userType = 1
                    userTypeLike = 1
                    binding.buttonClick = 1
                    filterOpen = 1
                    BindingUtils.userRole = 1
                }
                // btn home click
                R.id.clHome -> {
                    scrollType = ""
                    binding.check = 2
                    binding.userType = 2
                    BindingUtils.userRole = 2
                    binding.buttonClick = 1
                    userTypeLike = 2
                    filterOpen = 2


                }

                R.id.ivProviderFilter -> {
                    if (binding.clFilter.isGone) {
                        binding.clFilter.visibility = View.VISIBLE
                    } else {
                        binding.clFilter.visibility = View.GONE
                    }
                }

                R.id.tvFirst -> {
                    val data = HashMap<String, String>()
                    data["sort"] = "sort_newest"
                    viewModel.getListing(Constants.LISTING, data)
                    binding.clFilter.visibility = View.GONE
                }

                R.id.tvSecond -> {
                    val data = HashMap<String, String>()
                    data["sort"] = "sort_oldest"
                    viewModel.getListing(Constants.LISTING, data)
                    binding.clFilter.visibility = View.GONE
                }

                R.id.tvThird -> {
                    val data = HashMap<String, String>()
                    data["sort"] = "sort_name_az"
                    viewModel.getListing(Constants.LISTING, data)
                    binding.clFilter.visibility = View.GONE
                }

                R.id.tvFourth -> {
                    val data = HashMap<String, String>()
                    data["sort"] = "sort_name_za"
                    viewModel.getListing(Constants.LISTING, data)
                    binding.clFilter.visibility = View.GONE
                }

            }
        }


        binding.ivCard.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val width = v.width
                val clickedX = event.x
                if (clickedX < width / 2) {
                    if (userTypeLike == 1) {
                        binding.cardStackView.onClickLeft()
                    } else {
                        binding.kolodaHomeType.onClickLeft()
                    }

                } else {
                    if (userTypeLike == 1) {
                        binding.cardStackView.onClickRight()
                    } else {
                        binding.kolodaHomeType.onClickRight()
                    }

                }
            }
            true
        }

        binding.ivLine.setOnTouchListener { v, event ->
            binding.clFilter.visibility = View.GONE
            if (event.action == MotionEvent.ACTION_UP) {
                val width = v.width
                val clickedX = event.x
                if (clickedX < width / 2) {
                    binding.buttonClick = 1
                } else {
                    binding.buttonClick = 2

                }
            }
            true
        }



        binding.cardStackView.kolodaListener = object : KolodaListener {
            override fun onCardSwipedLeft(position: Int) {
                Log.d("Koloda", "Card finished swiping left at position $position")
                val pos = if (position <= 0) {
                    position + 1
                } else {
                    position - 1
                }
                val item = adapter.getItemAt(pos)
                val id = item?._id
                val data = HashMap<String, Any>()
                data["id"] = id.toString()
                data["action"] = "dislike"
                data["type"] = "user"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)

            }


            override fun onClickLeft(position: Int) {
                val pos = if (position <= 0) {
                    position + 1
                } else {
                    position - 1
                }
                val item = adapter.getItemAt(pos)
                val id = item?._id
                val data = HashMap<String, Any>()
                data["id"] = id.toString()
                data["action"] = "dislike"
                data["type"] = "user"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)
            }


            override fun onCardSwipedRight(position: Int) {
                val pos = if (position <= 0) {
                    position + 1
                } else {
                    position - 1
                }
                val item = adapter.getItemAt(pos)
                val id = item?._id
                val data = HashMap<String, Any>()
                data["id"] = id.toString()
                data["action"] = "like"
                data["type"] = "user"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)
            }

            override fun onClickRight(position: Int) {
                val pos = if (position <= 0) {
                    position + 1
                } else {
                    position - 1
                }
                val item = adapter.getItemAt(pos)
                val id = item?._id
                val data = HashMap<String, Any>()
                data["id"] = id.toString()
                data["action"] = "like"
                data["type"] = "user"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)
            }


            override fun onCardDrag(position: Int, cardView: View, progress: Float) {
                val ivLike = cardView.findViewById<AppCompatImageView>(R.id.ivLike)
                val ivDislike = cardView.findViewById<AppCompatImageView>(R.id.ivDislike)
                if (progress > 0) {
                    // Swiping right → show Like
                    ivLike?.alpha = progress
                    ivDislike?.alpha = 0f
                } else if (progress < 0) {
                    // Swiping left → show Dislike
                    ivLike?.alpha = 0f
                    ivDislike?.alpha = -progress // Make it positive
                } else {
                    // Neutral position
                    ivLike?.alpha = 0f
                    ivDislike?.alpha = 0f
                }
            }

        }

        binding.kolodaHomeType.kolodaListener = object : KolodaListener {
            override fun onCardSwipedLeft(position: Int) {
                val pos = if (position <= 0) {
                    position + 1
                } else {
                    position - 1
                }
                val item = homeAdapterSwipe.homeGetItemAt(pos)
                val id = item?._id
                val data = HashMap<String, Any>()
                data["id"] = id.toString()
                data["action"] = "dislike"
                data["type"] = "listing"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)
            }


            override fun onClickLeft(position: Int) {
                val pos = if (position <= 0) {
                    position + 1
                } else {
                    position - 1
                }
                val item = homeAdapterSwipe.homeGetItemAt(pos)
                val id = item?._id
                val data = HashMap<String, Any>()
                data["id"] = id.toString()
                data["action"] = "dislike"
                data["type"] = "listing"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)
            }


            override fun onCardSwipedRight(position: Int) {
                val pos = if (position <= 0) {
                    position + 1
                } else {
                    position - 1
                }
                val item = homeAdapterSwipe.homeGetItemAt(pos)
                val id = item?._id
                val data = HashMap<String, Any>()
                data["id"] = id.toString()
                data["action"] = "like"
                data["type"] = "listing"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)
            }

            override fun onClickRight(position: Int) {
                val pos = if (position <= 0) {
                    position + 1
                } else {
                    position - 1
                }
                val item = homeAdapterSwipe.homeGetItemAt(pos)
                val id = item?._id
                val data = HashMap<String, Any>()
                data["id"] = id.toString()
                data["action"] = "like"
                data["type"] = "listing"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)
            }


            override fun onCardDrag(position: Int, cardView: View, progress: Float) {
                val ivHomeLike = cardView.findViewById<AppCompatImageView>(R.id.ivHomeLike)
                val ivHomeDisLike = cardView.findViewById<AppCompatImageView>(R.id.ivHomeDisLike)
                if (progress > 0) {
                    // Swiping right → show Like
                    ivHomeLike?.alpha = progress
                    ivHomeDisLike?.alpha = 0f
                } else if (progress < 0) {
                    // Swiping left → show Dislike
                    ivHomeLike?.alpha = 0f
                    ivHomeDisLike?.alpha = -progress // Make it positive
                } else {
                    // Neutral position
                    ivHomeLike?.alpha = 0f
                    ivHomeDisLike?.alpha = 0f
                }
            }
        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getHomeApi" -> {
                            val userData = sharedPrefManager.getRole()
                            if (userData == 1) {
                                val data = HashMap<String, String>()
                                viewModel.getHomeApiListening(Constants.MATCH_LOOKING_LISTING, data)
                            }
                            try {
                                val myDataModel: HomeApiResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    var messageCount = myDataModel.unReadNotifications ?: 0
                                    if (messageCount > 0) {
                                        binding.ivNotificationCount.visibility = View.VISIBLE
                                        binding.tvNotificationCount.visibility = View.GONE
                                        binding.ivNotificationCount.text = "$messageCount"
                                    } else {
                                        binding.tvNotificationCount.visibility = View.GONE
                                        binding.ivNotificationCount.visibility = View.GONE
                                    }


                                    adapter = RoommateSwipeAdapter(
                                        requireContext(), myDataModel.data as List<HomeApiData>
                                    )

                                    binding.cardStackView.adapter = adapter


                                    if ((myDataModel.data.isNotEmpty())) {
                                        binding.tvEmpty.visibility = View.GONE
                                    } else {
                                        binding.tvEmpty.text = getString(R.string.no_roommate_data)
                                        binding.tvEmpty.visibility = View.VISIBLE
                                    }


                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            }finally {
                                hideLoading()
                            }
                        }

                        "getHomeApiListening" -> {
                            val userData = sharedPrefManager.getRole()
                            if (userData == 2) {
                                val data = HashMap<String, String>()
                                viewModel.getHomeApi(Constants.MATCH_LOOKING_ROOMMATE, data)
                            }
                            try {
                                val myDataModel: HomeRoomType? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    var messageCount = myDataModel.unReadNotifications ?: 0
                                    if (messageCount > 0) {
                                        binding.ivNotificationCount.visibility = View.VISIBLE
                                        binding.ivNotificationCount.text = "$messageCount"
                                        binding.tvNotificationCount.visibility = View.GONE
                                    } else {
                                        binding.tvNotificationCount.visibility = View.GONE
                                        binding.ivNotificationCount.visibility = View.GONE
                                    }


                                    homeAdapterSwipe = HomeSwipeAdapter(
                                        requireContext(), myDataModel.data as List<HomeRoomTData>
                                    )
                                    binding.kolodaHomeType.adapter = homeAdapterSwipe

                                    homeAdapter.list = myDataModel.data

                                    if (homeAdapter.list.isNotEmpty()) {
                                        binding.tvEmptySecond.visibility = View.GONE
                                    } else {
                                        binding.tvEmptySecond.text =
                                            getString(R.string.no_room_data)
                                        binding.tvEmptySecond.visibility = View.VISIBLE
                                    }

                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApiListening: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "likeDisLike" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                showSuccessToast(myDataModel?.message.toString())

                            } catch (e: Exception) {
                                Log.e("error", "likeDisLike: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "getListing" -> {
                            try {
                                val myDataModel: GetListingResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    var messageCount = myDataModel.unReadNotifications ?: 0

                                    if (messageCount > 0) {
                                        binding.ivNotificationCount.visibility = View.GONE
                                        binding.tvNotificationCount.visibility = View.VISIBLE
                                        binding.tvNotificationCount.text = "$messageCount"
                                    } else {
                                        binding.ivNotificationCount.visibility = View.GONE
                                        binding.tvNotificationCount.visibility = View.GONE
                                    }
                                    homeThirdAdapter.list = myDataModel.data

                                    if (homeThirdAdapter.list.isNotEmpty()) {
                                        binding.tvEmpty.visibility = View.GONE
                                    } else {
                                        binding.tvEmpty.text = getString(R.string.no_listing_data)
                                        binding.tvEmpty.visibility = View.VISIBLE
                                    }

                                }

                            } catch (e: Exception) {
                                Log.e("error", "likeDisLike: $e")
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

    private fun filterList(): ArrayList<HomeFilterList> {
        val filterList = ArrayList<HomeFilterList>()
        filterList.add(HomeFilterList(0, getString(R.string.all), true))
        filterList.add(HomeFilterList(1, getString(R.string.draft), false))
        filterList.add(HomeFilterList(2, getString(R.string.active), false))
        filterList.add(HomeFilterList(3, getString(R.string.paused), false))
        filterList.add(HomeFilterList(4, getString(R.string.rented), false))
        return filterList
    }


}