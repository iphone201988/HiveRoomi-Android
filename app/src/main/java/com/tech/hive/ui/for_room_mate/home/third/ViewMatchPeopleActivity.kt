package com.tech.hive.ui.for_room_mate.home.third

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
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.GetMatchByIdResponse
import com.tech.hive.data.model.GetMatchData
import com.tech.hive.databinding.ActivityViewMatchPeopleBinding
import com.tech.hive.databinding.MatchByIdRvItemBinding
import com.tech.hive.ui.for_room_mate.home.HomeFragmentVM
import com.tech.hive.ui.for_room_mate.messages.chat.ChatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewMatchPeopleActivity : BaseActivity<ActivityViewMatchPeopleBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    private lateinit var byIdMatchesAdapter: SimpleRecyclerViewAdapter<GetMatchData, MatchByIdRvItemBinding>
    private var position = -1
    override fun getLayoutResource(): Int {
        return R.layout.activity_view_match_people
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@ViewMatchPeopleActivity)
        BindingUtils.statusBarTextColor(this@ViewMatchPeopleActivity, false)
        // click
        initOnClick()
        // observer
        initObserver()
        // adapter
        initByIdMatchesAdapter()
        // get data intent
        val matchId = intent.getStringExtra("matchId")
        val sendTitle = intent.getStringExtra("sendTitle")
        if (sendTitle?.isNotEmpty() == true){
            binding.tvTitle.text = sendTitle
        }
        if (matchId?.isNotEmpty() == true) {
            val data = HashMap<String, String>()
            data["id"] = matchId
            viewModel.getMatchId(Constants.LISTING_MATCH, data)
        }
    }


    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this@ViewMatchPeopleActivity) {
            when (it?.id) {
                R.id.ivBack -> {
                    finish()
                }

            }
        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.observeCommon.observe(this@ViewMatchPeopleActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getMatchId" -> {
                            try {
                                val myDataModel: GetMatchByIdResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    byIdMatchesAdapter.list = myDataModel.data

                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "acceptRejectAPi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (position != -1) {
                                        byIdMatchesAdapter.removeItem(position)
                                        byIdMatchesAdapter.notifyDataSetChanged()
                                    }
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

    /** adapter response **/
    private fun initByIdMatchesAdapter() {
        byIdMatchesAdapter =
            SimpleRecyclerViewAdapter(R.layout.match_by_id_rv_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    // cancel
                    R.id.ivCancel -> {
                        position = pos
                        val data = HashMap<String, Any>()
                        data["action"] = "reject"
                        data["id"] = m._id.toString()
                        viewModel.acceptRejectAPi(Constants.MATCH_ACCEPT_REJECT, data)

                    }
                    // like
                    R.id.ivLike -> {
                        position = pos
                        val data = HashMap<String, Any>()
                        data["action"] = "accept"
                        data["id"] = m._id.toString()
                        viewModel.acceptRejectAPi(Constants.MATCH_ACCEPT_REJECT, data)
                    }
                    // chat
                    R.id.ivChat -> {
                        val intent = Intent(this@ViewMatchPeopleActivity, ChatActivity::class.java)
                        intent.putExtra("socketId", m.userId?._id)
                        intent.putExtra("matchId", m.userId?._id)
                        startActivity(intent)
                    }

                }
            }

        binding.rvMatchById.adapter = byIdMatchesAdapter
    }
}