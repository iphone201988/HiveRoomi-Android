package com.tech.hive.ui.for_room_mate.home

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
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
import com.tech.hive.data.model.CompatibilityModel
import com.tech.hive.data.model.MatchProfileUserResponse
import com.tech.hive.databinding.ActivityMatchedProfileBinding
import com.tech.hive.databinding.ApartmentImageItemViewBinding
import com.tech.hive.databinding.CompatibilityItemViewBinding
import com.tech.hive.ui.for_room_mate.messages.chat.ChatActivity
import com.tech.hive.ui.for_room_mate.settings_screen.ReportActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchedProfileActivity : BaseActivity<ActivityMatchedProfileBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()

    // adapter
    private lateinit var compatibilityAdapter: SimpleRecyclerViewAdapter<CompatibilityModel, CompatibilityItemViewBinding>
    private lateinit var apartmentImageAdapter: SimpleRecyclerViewAdapter<CompatibilityModel, ApartmentImageItemViewBinding>
    var profileIdFirst = ""
    var commonId = ""

    override fun getLayoutResource(): Int {
        return R.layout.activity_matched_profile
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
        profileIdFirst = intent.getStringExtra("profileIdFirst").toString()
        if (profileIdFirst.isNotEmpty()) {
            val data = HashMap<String, String>()
            viewModel.getMatchedProfile(data, Constants.MATCH_USER_ID + "$profileIdFirst")
        }
    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@MatchedProfileActivity)
        BindingUtils.statusBarTextColor(this@MatchedProfileActivity, false)
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

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.ivLikeBefore,R.id.tvLikeBefore->{
                    val data = HashMap<String, Any>()
                    data["id"] = commonId
                    data["action"]="like"
                    data["type"] = "user"
                  viewModel.matchLikeApi(Constants.MATCH_LIKE,data)
                }
                R.id.ivReportBefore,R.id.tvReportBefore->{
                    val intent = Intent(this@MatchedProfileActivity, ReportActivity::class.java)
                    intent.putExtra("reportId",commonId)
                    intent.putExtra("reportType","user")
                    startActivity(intent)
                }
                R.id.ivLike,R.id.tvLike->{

                }
                R.id.ivChat,R.id.tvChat->{
                    val intent = Intent(this@MatchedProfileActivity, ChatActivity::class.java)
                    intent.putExtra("chatId",commonId)
                    startActivity(intent)
                }
                R.id.ivReport,R.id.tvReport->{
                val intent = Intent(this@MatchedProfileActivity, ReportActivity::class.java)
                    intent.putExtra("reportId",commonId)
                    intent.putExtra("reportType","user")
                    startActivity(intent)
                }
            }
        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.observeCommon.observe(this@MatchedProfileActivity) {
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
                                    commonId = myDataModel.data._id.toString()
                                    when(myDataModel.data.likeStatus){
                                        1->{
                                            binding.tvLike.text = getString(R.string.pending)
                                            binding.match = 2
                                        }
                                        2->{
                                            binding.tvLike.text = getString(R.string.matched)
                                            binding.match = 2
                                        }
                                        3->{
                                            binding.tvLike.text = getString(R.string.rejected)
                                            binding.match = 2
                                        }else -> {
                                        binding.match = 1
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