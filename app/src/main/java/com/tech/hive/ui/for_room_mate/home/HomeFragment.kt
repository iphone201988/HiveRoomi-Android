package com.tech.hive.ui.for_room_mate.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.GetListingData
import com.tech.hive.data.model.GetListingResponse
import com.tech.hive.data.model.HomeApiResponse
import com.tech.hive.data.model.HomeFilterList
import com.tech.hive.data.model.HomeRoomTData
import com.tech.hive.data.model.HomeRoomType
import com.tech.hive.databinding.FragmentHomeBinding
import com.tech.hive.databinding.RvDiscoverItemBinding
import com.tech.hive.databinding.RvHomeFilterItemBinding
import com.tech.hive.databinding.ThirdUserRvItemBinding
import com.tech.hive.ui.for_room_mate.calenders.CalenderActivity
import com.tech.hive.ui.for_room_mate.filters.FilterActivity
import com.tech.hive.ui.for_room_mate.home.cardstackview.CardStackLayoutManager
import com.tech.hive.ui.for_room_mate.home.cardstackview.CardStackListener
import com.tech.hive.ui.for_room_mate.home.cardstackview.Direction
import com.tech.hive.ui.for_room_mate.home.cardstackview.Duration
import com.tech.hive.ui.for_room_mate.home.cardstackview.StackFrom
import com.tech.hive.ui.for_room_mate.home.cardstackview.SwipeAnimationSetting
import com.tech.hive.ui.for_room_mate.home.cardstackview.SwipeableMethod
import com.tech.hive.ui.for_room_mate.home.sample.CardStackAdapter
import com.tech.hive.ui.for_room_mate.home.second.SecondMatchActivity
import com.tech.hive.ui.for_room_mate.home.third.ThirdMatchActivity
import com.tech.hive.ui.notification.NotificationActivity
import com.tech.hive.ui.room_offering.basic_details.BasicDetailsActivity
import com.tech.hive.ui.room_offering.discover.DiscoverySettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), CardStackListener {
    private val viewModel: HomeFragmentVM by viewModels()

    private lateinit var homeAdapter: SimpleRecyclerViewAdapter<HomeRoomTData, RvDiscoverItemBinding>
    private lateinit var homeThirdAdapter: SimpleRecyclerViewAdapter<GetListingData, ThirdUserRvItemBinding>
    private lateinit var homeThirdFilterAdapter: SimpleRecyclerViewAdapter<HomeFilterList, RvHomeFilterItemBinding>
    var createSpots = ArrayList<CardItem>()
    private var userTypeLike = 1
    private val cardAdapter by lazy { CardStackAdapter(createSpots, requireContext()) }
    private val manager by lazy { CardStackLayoutManager(requireActivity(), this) }
    private var scrollType = ""
    private var searchHandler: Handler? = Handler()
    private var searchRunnable: Runnable? = null
    private var filterOpen = 0
    var progressType = false
    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(view: View) {
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

    }


    override fun onResume() {
        super.onResume()
        var userData = sharedPrefManager.getRole()
        binding.clFilter.visibility = View.GONE
        binding.userType = userData
        binding.buttonClick = 1
        binding.check = userData
        userTypeLike = userData

        if (userData == 1) {
            // api call
            viewModel.getHomeApi(Constants.MATCH_LOOKING_ROOMMATE)
            filterOpen = 1
        } else if (userData == 2) {
            filterOpen = 2
            // api call
            viewModel.getHomeApiListening(Constants.MATCH_LOOKING_LISTING)
        } else {
            filterOpen = 0
            // api call
            val data = HashMap<String, String>()
            viewModel.getListing(Constants.LISTING, data)
        }
    }

    /** handle adapter **/
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

//                        val isAlreadyChecked = m.check
//                        for (i in homeThirdAdapter.list) {
//                            i.check = false
//                        }
//                        m.check = !isAlreadyChecked
//                        homeThirdAdapter.notifyDataSetChanged()
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
                        startActivity(Intent(requireContext(), FilterActivity::class.java))
                    } else if (filterOpen == 2) {
                        startActivity(
                            Intent(
                                requireContext(), DiscoverySettingsActivity::class.java
                            )
                        )
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
                    filterOpen = 1
                    viewModel.getHomeApi(Constants.MATCH_LOOKING_ROOMMATE)
                }
                // btn home click
                R.id.clHome -> {
                    scrollType = ""
                    binding.check = 2
                    binding.userType = 2
                    binding.buttonClick = 1
                    userTypeLike = 2
                    filterOpen = 2
                    viewModel.getHomeApiListening(Constants.MATCH_LOOKING_LISTING)
                }

                R.id.ivProviderFilter -> {
                    if (binding.clFilter.visibility == View.GONE) {
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
            binding.clFilter.visibility = View.GONE
            if (event.action == MotionEvent.ACTION_UP) {
                var data = sharedPrefManager.getRole()
                val width = v.width
                val clickedX = event.x
                if (clickedX < width / 2) {
                    val setting = SwipeAnimationSetting.Builder().setDirection(Direction.Left)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(AccelerateInterpolator()).build()
                    manager.setSwipeAnimationSetting(setting)
                    if (userTypeLike == 1) {
                        binding.cardStackView.swipe()
                    } else {
                        binding.cardStackView1.swipe()
                    }

                } else {
                    val setting = SwipeAnimationSetting.Builder().setDirection(Direction.Right)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(AccelerateInterpolator()).build()
                    manager.setSwipeAnimationSetting(setting)
                    if (data == 1) {
                        binding.cardStackView.swipe()
                    } else {
                        binding.cardStackView1.swipe()
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

        // undu button click
        /* rewind.setOnClickListener {
             val setting = RewindAnimationSetting.Builder()
                 .setDirection(Direction.Bottom)
                 .setDuration(Duration.Normal.duration)
                 .setInterpolator(DecelerateInterpolator())
                 .build()
             manager.setRewindAnimationSetting(setting)
             cardStackView.rewind()
         }*/
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    if (!progressType) {
                        progressType = false
                        showLoading()
                    }

                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getHomeApi" -> {
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

                                    createSpots.clear()
                                    myDataModel.data.filterNotNull().forEach { user ->
                                        createSpots.add(CardItem.HomeRoom(user))
                                    }
                                    initialize()

                                    if ((myDataModel.data.size > 0)) {
                                        binding.tvEmpty.visibility = View.GONE
                                    } else {
                                        binding.tvEmpty.text = getString(R.string.no_roommate_data)
                                        binding.tvEmpty.visibility = View.VISIBLE
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "getHomeApiListening" -> {
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
                                    createSpots.clear()
                                    myDataModel.data.filterNotNull().forEach { room ->
                                        createSpots.add(CardItem.RoomListing(room))
                                    }
                                    initialize()
                                    homeAdapter.list = myDataModel.data

                                    if (homeAdapter.list.size > 0) {
                                        binding.tvEmpty.visibility = View.GONE
                                    } else {
                                        binding.tvEmpty.text = getString(R.string.no_room_data)
                                        binding.tvEmpty.visibility = View.VISIBLE
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
                                if (myDataModel != null) {


                                }

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

                                    if (homeThirdAdapter.list.size > 0) {
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


    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
        when (direction) {
            Direction.Left -> {
                scrollType = "left"
            }

            Direction.Right -> {
                scrollType = "right"
            }

            else -> {}
        }
        val position = manager.topPosition - 1
        if (position >= 0 && position < cardAdapter.getItems().size) {
            val currentItem = cardAdapter.getItems()[position]

            val id = when (currentItem) {
                is CardItem.HomeRoom -> currentItem.user._id
                is CardItem.RoomListing -> currentItem.room._id
            } ?: ""

            val data = hashMapOf<String, Any>(
                "id" to id,
                "action" to if (direction == Direction.Left) "dislike" else "like",
                "type" to if (userTypeLike == 1) "user" else "listing"
            )

            Log.d("SwipeDebug", "Calling API with data: $data")
            viewModel.likeDisLike(Constants.MATCH_LIKE, data)
        } else {
            Log.e("SwipeDebug", "Invalid position: $position")
        }
    }


    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.d("dgfgfgdff", "onCardAppeared: ")
        val currentItem = cardAdapter.getItems()[position]

        val id = when (currentItem) {
            is CardItem.HomeRoom -> currentItem.user._id
            is CardItem.RoomListing -> currentItem.room._id
        } ?: ""

        val data = HashMap<String, Any>()
        data["id"] = id

        when (scrollType) {
            "left" -> {
                data["action"] = "dislike"
                data["type"] = if (userTypeLike == 1) "user" else "listing"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)
            }

            "right" -> {
                data["action"] = "like"
                data["type"] = if (userTypeLike == 1) "user" else "listing"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)
            }
        }
    }


    override fun onCardDisappeared(view: View?, position: Int) {
        Log.d("dgfgfgdff", "onCardDisappeared: ")
    }


    private fun initialize() {
        manager.setStackFrom(StackFrom.Bottom)
        manager.setVisibleCount(2)
        manager.setTranslationInterval(6.0f)
        manager.setScaleInterval(0.90f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())

        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = cardAdapter
        binding.cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }


        val newManager = CardStackLayoutManager(requireContext(), this).apply {
            setStackFrom(StackFrom.Bottom)
            setVisibleCount(2)
            setTranslationInterval(6.0f)
            setScaleInterval(0.90f)
            setSwipeThreshold(0.3f)
            setMaxDegree(20.0f)
            setDirections(Direction.HORIZONTAL)
            setCanScrollHorizontal(true)
            setCanScrollVertical(true)
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
        }
        binding.cardStackView1.layoutManager = newManager
        binding.cardStackView1.adapter = cardAdapter
        binding.cardStackView1.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }


    }


}