package com.tech.hive.ui.for_room_mate.home

import android.annotation.SuppressLint
import android.content.Intent
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
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.DiscoverModel
import com.tech.hive.data.model.HomeFilterList
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
import com.tech.hive.ui.for_room_mate.home.sample.Spot
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

    private val cardAdapter by lazy { CardStackAdapter(createSpots(), requireContext()) }
    private val manager by lazy {
        CardStackLayoutManager(
            requireActivity(), this
        )
    }


    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // card stack view
        initialize()
        // view
        initView()
        // click
        initOnClick()
        binding.userType = Constants.userType
        binding.buttonClick = 1
        binding.check = Constants.userType

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
                    binding.check = 1
                    binding.userType = 1
                    Constants.userType = 1
                }
                // btn home click
                R.id.clHome -> {
                    binding.check = 2
                    binding.userType = 2
                    binding.buttonClick = 1
                    Constants.userType = 2
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
        viewModel.homeObserver.observe(viewLifecycleOwner) {

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

    override fun onCardSwiped(direction: Direction?) {
    }

    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
    }

    override fun onCardDisappeared(view: View?, position: Int) {

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
        val old = cardAdapter.getSpots()
        val new = old.plus(createSpots())
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setSpots(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

    private fun reload() {
        val old = cardAdapter.getSpots()
        val new = createSpots()
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setSpots(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

    private fun addFirst(size: Int) {
        val old = cardAdapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                add(manager.topPosition, createSpot())
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setSpots(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

    private fun addLast(size: Int) {
        val old = cardAdapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            addAll(List(size) { createSpot() })
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setSpots(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

    private fun removeFirst(size: Int) {
        if (cardAdapter.getSpots().isEmpty()) {
            return
        }

        val old = cardAdapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(manager.topPosition)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setSpots(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

    private fun removeLast(size: Int) {
        if (cardAdapter.getSpots().isEmpty()) {
            return
        }

        val old = cardAdapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(this.size - 1)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setSpots(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

    private fun replace() {
        val old = cardAdapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            removeAt(manager.topPosition)
            add(manager.topPosition, createSpot())
        }
        cardAdapter.setSpots(new)
        cardAdapter.notifyItemChanged(manager.topPosition)
    }

    private fun swap() {
        val old = cardAdapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            val first = removeAt(manager.topPosition)
            val last = removeAt(this.size - 1)
            add(manager.topPosition, last)
            add(first)
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        cardAdapter.setSpots(new)
        result.dispatchUpdatesTo(cardAdapter)
    }

    private fun createSpot(): Spot {
        return Spot(
            name = "Chris Salvatore, 25",
            profession = "Software Engineer",
            money = "$300-800",
            parties = "No Parties on weekend",
            url = R.drawable.card_image
        )
    }

    private fun createSpots(): List<Spot> {
        val spots = java.util.ArrayList<Spot>()
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "Kyoto",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "No Parties on weekend",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "Kyoto",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "No Parties on weekend",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "Kyoto",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "No Parties on weekend",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "Kyoto",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "No Parties on weekend",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "Kyoto",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "No Parties on weekend",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "Kyoto",
                url = R.drawable.card_image
            )
        )
        spots.add(
            Spot(
                name = "Chris Salvatore, 25",
                profession = "Software Engineer",
                money = "$300-800",
                parties = "No Parties on weekend",
                url = R.drawable.card_image
            )
        )

        return spots
    }

}