package com.tech.hive.ui.for_room_mate.home

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.DiscoverModel
import com.tech.hive.data.model.HomeApiResponse
import com.tech.hive.data.model.HomeFilterList
import com.tech.hive.data.model.HomeRoomType
import com.tech.hive.data.model.ThirdTypeModel
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
import com.tech.hive.ui.for_room_mate.home.sample.SpotDiffCallback
import com.tech.hive.ui.notification.NotificationActivity
import com.tech.hive.ui.room_offering.basic_details.BasicDetailsActivity
import com.tech.hive.ui.room_offering.discover.DiscoverySettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), CardStackListener {

    private val viewModel: HomeFragmentVM by viewModels()

    private lateinit var homeAdapter: SimpleRecyclerViewAdapter<DiscoverModel, RvDiscoverItemBinding>
    private lateinit var homeThirdAdapter: SimpleRecyclerViewAdapter<ThirdTypeModel, ThirdUserRvItemBinding>
    private lateinit var homeThirdFilterAdapter: SimpleRecyclerViewAdapter<HomeFilterList, RvHomeFilterItemBinding>
    var createSpots = ArrayList<CardItem>()
    private val cardAdapter by lazy { CardStackAdapter(createSpots, requireContext()) }
    private val manager by lazy {
        CardStackLayoutManager(
            requireActivity(), this
        )
    }
    private var scrollType = ""


    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

        // view
        initView()
        // click
        initOnClick()
        binding.userType = Constants.userType
        binding.buttonClick = 1
        binding.check = Constants.userType

        when (Constants.userType) {
            1 -> {
                // api call
                viewModel.getHomeApi(Constants.MATCH_LOOKING_ROOMMATE)
            }

            2 -> {
                // api call
                viewModel.getHomeApiListening(Constants.MATCH_LOOKING_LISTING)
            }
        }
    }


    /** handle view **/
    private fun initView() {
        // observer
        initObserver()
        // adapter
        initAdapter()
        binding.check = 1
    }


    /** handle adapter **/
    private fun initAdapter() {
        // home adapter
        homeAdapter = SimpleRecyclerViewAdapter(R.layout.rv_discover_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clAnswer -> {

                }
            }
        }
        homeAdapter.list = getHomeList()
        binding.rvHome.adapter = homeAdapter

        homeThirdAdapter =
            SimpleRecyclerViewAdapter(R.layout.third_user_rv_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.ivThreeDot -> {
                        val isAlreadyChecked = m.isCheck
                        for (i in homeThirdAdapter.list) {
                            i.isCheck = false
                        }
                        m.isCheck = !isAlreadyChecked
                        homeThirdAdapter.notifyDataSetChanged()
                    }
                }
            }
        homeThirdAdapter.list = getHomeThirdList()
        binding.rvThirdHome.adapter = homeThirdAdapter

        homeThirdFilterAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_home_filter_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clImage -> {
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


    // home List
    private fun getHomeThirdList(): ArrayList<ThirdTypeModel> {
        val list = ArrayList<ThirdTypeModel>()
        list.add(
            ThirdTypeModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 1 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            ThirdTypeModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 2 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            ThirdTypeModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 3 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            ThirdTypeModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 4 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            ThirdTypeModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 5 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            ThirdTypeModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 6 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            ThirdTypeModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 7 Roommates",
                "No Smoking Pets Allowed"
            )
        )

        return list
    }

    // home List
    private fun getHomeList(): ArrayList<DiscoverModel> {
        val list = ArrayList<DiscoverModel>()
        list.add(
            DiscoverModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 2 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            DiscoverModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 2 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            DiscoverModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 2 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            DiscoverModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 2 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            DiscoverModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 2 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            DiscoverModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 2 Roommates",
                "No Smoking Pets Allowed"
            )
        )
        list.add(
            DiscoverModel(
                "Cozy Room in NYC",
                "$600/month",
                "Isola Single 2 Roommates",
                "No Smoking Pets Allowed"
            )
        )

        return list
    }

    /** handle click **/
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // filter click
                R.id.ivFilter -> {
                    if (Constants.userType == 1) {
                        startActivity(Intent(requireContext(), FilterActivity::class.java))
                    } else {
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
                    Constants.userType = 1
                    viewModel.getHomeApi(Constants.MATCH_LOOKING_ROOMMATE)
                }
                // btn home click
                R.id.clHome -> {
                    scrollType = ""
                    binding.check = 2
                    binding.userType = 2
                    binding.buttonClick = 1
                    viewModel.getHomeApiListening(Constants.MATCH_LOOKING_LISTING)
                }
            }
        }

        binding.ivCard.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val width = v.width
                val clickedX = event.x
                if (clickedX < width / 2) {
                    val setting = SwipeAnimationSetting.Builder().setDirection(Direction.Left)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(AccelerateInterpolator()).build()
                    manager.setSwipeAnimationSetting(setting)
                    if (Constants.userType == 1) {
                        binding.cardStackView.swipe()
                    } else {
                        binding.cardStackView1.swipe()
                    }

                } else {
                    val setting = SwipeAnimationSetting.Builder().setDirection(Direction.Right)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(AccelerateInterpolator()).build()
                    manager.setSwipeAnimationSetting(setting)
                    if (Constants.userType == 1) {
                        binding.cardStackView.swipe()
                    } else {
                        binding.cardStackView1.swipe()
                    }

                }
            }
            true
        }


        binding.ivLine.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val width = v.width
                val clickedX = event.x
                if (clickedX < width / 2) {
                    binding.buttonClick = 1
                } else {
                    binding.buttonClick = 2
                    Constants.userType = 2
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
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getHomeApi" -> {
                            try {
                                val myDataModel: HomeApiResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    createSpots.clear()
                                    myDataModel.data.filterNotNull().forEach { user ->
                                        createSpots.add(CardItem.HomeRoom(user))
                                    }
                                    initialize()
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
                                    createSpots.clear()
                                    myDataModel.data.filterNotNull().forEach { room ->
                                        createSpots.add(CardItem.RoomListing(room))
                                    }
                                    initialize()
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
        filterList.add(HomeFilterList("All"))
        filterList.add(HomeFilterList("Active"))
        filterList.add(HomeFilterList("In Review"))
        filterList.add(HomeFilterList("Rented"))
        return filterList
    }


    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

//    override fun onCardSwiped(direction: Direction?) {
//        Log.d("dgfgfgdff", "onCardSwiped: ")
//        when (direction) {
//            Direction.Left -> {
//                scrollType = "left"
//            }
//
//            Direction.Right -> {
//                scrollType = "right"
//            }
//
//            else -> {}
//        }
//
//    }

    override fun onCardSwiped(direction: Direction?) {
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
                "type" to if (Constants.userType == 1) "user" else "listing"
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
                data["type"] = if (Constants.userType == 1) "user" else "listing"
                viewModel.likeDisLike(Constants.MATCH_LIKE, data)
            }

            "right" -> {
                data["action"] = "like"
                data["type"] = if (Constants.userType == 1) "user" else "listing"
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

    private fun paginate() {
        val old = cardAdapter.getItems()
        val new = old + createSpots // Safer way of plus()
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setItems(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

    private fun reload() {
        val old = cardAdapter.getItems()
        val new = createSpots
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setItems(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

//
//    private fun addFirst(size: Int) {
//        val old = cardAdapter.getSpots()
//        val new = mutableListOf<HomeApiData>().apply {
//            addAll(old)
//            for (i in 0 until size) {
//                add(manager.topPosition, createSpots)
//            }
//        }
//        val callback = SpotDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        cardAdapter.setSpots(new)
//        result.dispatchUpdatesTo(cardAdapter)
//    }
//
//    private fun addLast(size: Int) {
//        val old = cardAdapter.getSpots()
//        val new = mutableListOf<HomeApiData>().apply {
//            addAll(old)
//            addAll(List(size) { createSpots})
//        }
//        val callback = SpotDiffCallback(old, new)
//        val result = DiffUtil.calculateDiff(callback)
//        cardAdapter.setSpots(new)
//        result.dispatchUpdatesTo(cardAdapter)
//    }

    private fun removeFirst(size: Int) {
        val old = cardAdapter.getItems()
        if (old.isEmpty()) return

        val new = old.toMutableList().apply {
            repeat(size.coerceAtMost(this.size)) {
                removeAt(manager.topPosition.coerceAtMost(lastIndex))
            }
        }

        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setItems(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

    private fun removeLast(size: Int) {
        val old = cardAdapter.getItems()
        if (old.isEmpty()) return

        val new = old.toMutableList().apply {
            repeat(size.coerceAtMost(this.size)) {
                removeAt(this.size - 1)
            }
        }

        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setItems(new)
        result.dispatchUpdatesTo(cardAdapter)
    }


//    private fun replace() {
//        val old = cardAdapter.getSpots()
//        val new = mutableListOf<HomeApiData>().apply {
//            addAll(old)
//            removeAt(manager.topPosition)
//            add(manager.topPosition, createSpots)
//        }
//        cardAdapter.setSpots(new)
//        cardAdapter.notifyItemChanged(manager.topPosition)
//    }

    private fun swap() {
        val old = cardAdapter.getItems()
        if (old.size < 2) return  // Nothing to swap if less than 2 items

        val new = old.toMutableList().apply {
            val firstIndex = manager.topPosition.coerceAtMost(lastIndex)
            val first = removeAt(firstIndex)
            val last = removeAt(lastIndex)
            add(firstIndex, last)
            add(first) // Add first item at the end
        }

        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setItems(new)
        result.dispatchUpdatesTo(cardAdapter)
    }


}