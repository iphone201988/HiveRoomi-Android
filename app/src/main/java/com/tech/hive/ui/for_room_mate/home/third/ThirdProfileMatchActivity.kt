package com.tech.hive.ui.for_room_mate.home.third

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.base.utils.showSuccessToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.CompatibilityModel
import com.tech.hive.data.model.MatchProfileUserResponse
import com.tech.hive.data.model.RatingsResponse
import com.tech.hive.databinding.ActivityThirdProfileMatchBinding
import com.tech.hive.databinding.ApartmentImageItemViewBinding
import com.tech.hive.databinding.CompatibilityItemViewBinding
import com.tech.hive.databinding.RatingDialogItemBinding
import com.tech.hive.ui.for_room_mate.home.HomeFragmentVM
import com.tech.hive.ui.for_room_mate.messages.chat.ChatActivity
import com.tech.hive.ui.for_room_mate.settings_screen.ReportActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThirdProfileMatchActivity : BaseActivity<ActivityThirdProfileMatchBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()

    // adapter
    private var ratingDialogItem: BaseCustomDialog<RatingDialogItemBinding>? = null
    private lateinit var compatibilityAdapter: SimpleRecyclerViewAdapter<CompatibilityModel, CompatibilityItemViewBinding>
    private lateinit var apartmentImageAdapter: SimpleRecyclerViewAdapter<CompatibilityModel, ApartmentImageItemViewBinding>
    var profileIdFirst = ""
    var commonId = ""
    var chatClick = false
    var ratingCount: Int = 0
    var ratingAverage: Double = 0.0
    override fun getLayoutResource(): Int {
        return R.layout.activity_third_profile_match
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
        profileIdFirst = intent.getStringExtra("profileIdThird").toString()
        if (profileIdFirst.isNotEmpty()) {
            val data = HashMap<String, String>()
            viewModel.getMatchedProfile(data, Constants.MATCH_USER_ID + "$profileIdFirst")
        }
    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@ThirdProfileMatchActivity)
        BindingUtils.statusBarTextColor(this@ThirdProfileMatchActivity, false)
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

    /** personal dialog  handel ***/
    private fun ratingDialog() {
        ratingDialogItem =
            BaseCustomDialog(this@ThirdProfileMatchActivity, R.layout.rating_dialog_item) {
                when (it?.id) {
                    R.id.btnContinue -> {
                        val rating = ratingDialogItem?.binding?.llStars?.rating ?: 0f
                        if (rating > 0) {
                            val data = HashMap<String, Any>()
                            data["id"] = commonId
                            data["rating"] = rating
                            viewModel.userRatings(Constants.USER_RATING, data)
                        } else {
                            showInfoToast("Please select rating")
                        }

                    }

                    R.id.ivClose -> {
                        ratingDialogItem?.dismiss()
                    }
                }
            }
        ratingDialogItem!!.create()
        ratingDialogItem!!.show()

        ratingDialogItem?.binding?.apply {
            tvRating.text = ratingAverage.toString()
            tvRatingCount.text = "$ratingCount ratings"
            ratingBar.setRating(ratingAverage.toFloat())
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

                R.id.ivLikeBefore, R.id.tvLikeBefore -> {
                    val data = HashMap<String, Any>()
                    data["id"] = commonId
                    data["action"] = "like"
                    data["type"] = "user"
                    viewModel.matchLikeApi(Constants.MATCH_LIKE, data)
                }

                R.id.ivReportBefore, R.id.tvReportBefore -> {
                    val intent = Intent(this@ThirdProfileMatchActivity, ReportActivity::class.java)
                    intent.putExtra("reportId", commonId)
                    intent.putExtra("reportType", "user")
                    startActivity(intent)
                }

                R.id.tvTitle1 -> {
                    ratingDialog()
                }

                R.id.ivChat, R.id.tvChat -> {
                    if (chatClick) {
                        val intent =
                            Intent(this@ThirdProfileMatchActivity, ChatActivity::class.java)
                        intent.putExtra("socketId", commonId)
                        intent.putExtra("matchId", commonId)
                        startActivity(intent)
                    }

                }

                R.id.ivReport, R.id.tvReport -> {
                    val intent = Intent(this@ThirdProfileMatchActivity, ReportActivity::class.java)
                    intent.putExtra("reportId", commonId)
                    intent.putExtra("reportType", "user")
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
                            val intent =
                                Intent(this@ThirdProfileMatchActivity, ChatActivity::class.java)
                            intent.putExtra("socketId", commonId)
                            intent.putExtra("matchId", commonId)
                            startActivity(intent)
                        }
                    }

                    else -> {
                        val intent =
                            Intent(this@ThirdProfileMatchActivity, ReportActivity::class.java)
                        intent.putExtra("reportId", commonId)
                        intent.putExtra("reportType", "user")
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
                    data["type"] = "user"
                    viewModel.matchLikeApi(Constants.MATCH_LIKE, data)
                } else {
                    val intent = Intent(this@ThirdProfileMatchActivity, ReportActivity::class.java)
                    intent.putExtra("reportId", commonId)
                    intent.putExtra("reportType", "user")
                    startActivity(intent)
                }
            }
            true
        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.observeCommon.observe(this@ThirdProfileMatchActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getMatchedProfile" -> {
                            try {
                                val myDataModel: MatchProfileUserResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    binding.bean = myDataModel.data
                                    ratingCount = myDataModel.data.totalRatings ?: 0
                                    ratingAverage = myDataModel.data.averageRating ?: 0.0
                                    binding.tvStarCount.text = ratingAverage.toString()
                                    commonId = myDataModel.data._id.toString()
                                    if (!myDataModel.data.linkedin.isNullOrEmpty()) {
                                        binding.tvLinkedin.text = myDataModel.data.linkedin
                                    } else {
                                        binding.tvLinkedin.text = getString(R.string.not_connected)
                                    }
                                    if (!myDataModel.data.instagram.isNullOrEmpty()) {
                                        binding.tvInstagram.text = myDataModel.data.instagram
                                    } else {
                                        binding.tvInstagram.text = getString(R.string.not_connected)
                                    }

                                    when (myDataModel.data.likeStatus) {
                                        1 -> {
                                            binding.tvLike.text = getString(R.string.pending)
                                            binding.match = 2
                                        }

                                        2 -> {
                                            binding.tvLike.text = getString(R.string.matched)
                                            binding.match = 2
                                            chatClick = true
                                        }

                                        3 -> {
                                            binding.tvLike.text = getString(R.string.rejected)
                                            binding.match = 2
                                        }

                                        else -> {
                                            binding.match = 1
                                        }
                                    }


                                    for (i in myDataModel.data.quizs!!) {
                                        when (i?.title) {
                                            "budget" -> {
                                                binding.tvBudget.text = i.answer
                                            }

                                            "acceptPets" -> {
                                                binding.tvPets.text = i.answer
                                            }

                                            "acceptSmokers" -> {
                                                binding.tvSmoking.text = i.answer
                                            }

                                            "guests" -> {
                                                binding.tvOverNight.text = i.answer
                                            }

                                            "cleaning" -> {
                                                binding.tvClean.text = i.answer
                                            }

                                            "lifestyle" -> {
                                                binding.tvSleep.text = i.answer
                                            }

                                        }

                                    }

                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
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
                                    binding.match = 2
                                    showSuccessToast("user liked")
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "userRatings" -> {
                            try {
                                val myDataModel: RatingsResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        ratingCount = myDataModel.data.totalRatings ?: 0
                                        ratingAverage = myDataModel.data.averageRating ?: 0.0
                                    }
                                    chatClick = true
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