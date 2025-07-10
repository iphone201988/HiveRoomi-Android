package com.tech.hive.ui.for_room_mate.matches

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.PendingMatchData
import com.tech.hive.data.model.PendingMatchResponse
import com.tech.hive.databinding.FragmentMatchesBinding
import com.tech.hive.databinding.MatchesItemViewBinding
import com.tech.hive.databinding.PendingMatchRvItemBinding
import com.tech.hive.ui.for_room_mate.filters.FilterActivity
import com.tech.hive.ui.for_room_mate.home.MatchedProfileActivity
import com.tech.hive.ui.for_room_mate.messages.chat.ChatActivity
import com.tech.hive.ui.room_offering.discover.DiscoverySettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchesFragment : BaseFragment<FragmentMatchesBinding>() {
    private val viewModel: MatchesFragmentVM by viewModels()
    private lateinit var matchesAdapter: SimpleRecyclerViewAdapter<PendingMatchData, MatchesItemViewBinding>
    private lateinit var pendingMatchesAdapter: SimpleRecyclerViewAdapter<PendingMatchData, PendingMatchRvItemBinding>
    private var position = -1
    var check = false
    override fun onCreateView(view: View) {
        // view
        initView()
        // click
        initOnClick()
        // observer
        initObserver()
        // check
        binding.check = 1
        binding.visibilityHandel = Constants.userType

        var userData = sharedPrefManager.getLoginData()
        if (userData != null) {
            if (sharedPrefManager.getLoginData()?.profileRole == 2) {
                check = false
                sharedPrefManager.saveSide("2")
            } else {
                check = true
                sharedPrefManager.saveSide("1")
            }
            if (sharedPrefManager.getSide() == "1") {
                binding.ivSettings.setImageResource(R.drawable.first_selected)
            } else if (sharedPrefManager.getSide() == "2") {
                binding.ivSettings.setImageResource(R.drawable.second_selected)
            } else {
                binding.ivSettings.visibility = View.GONE
            }
            if (userData.profileRole == 1) {
                val data = HashMap<String, String>()
                data["type"] = "user"
                viewModel.getMatchApi(Constants.GET_MATCH, data)
            } else {
                val data = HashMap<String, String>()
                data["type"] = "listing"
                viewModel.getMatchApi(Constants.GET_MATCH, data)
            }
        }

    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_matches
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    /** handle view **/
    private fun initView() {
        // adapter
        initMatchAdapter()
        initPendingMatchAdapter()

    }

    /** handle click **/
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // filter click
                R.id.ivSettings -> {
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
                // friends click
                R.id.tvFriend -> {
                    binding.check = 1
                    if (sharedPrefManager.getSide() == "1") {
                        val data = HashMap<String, String>()
                        data["type"] = "user"
                        viewModel.getMatchApi(Constants.GET_MATCH, data)
                    } else {
                        val data = HashMap<String, String>()
                        data["type"] = "listing"
                        viewModel.getMatchApi(Constants.GET_MATCH, data)
                    }

                }
                // request click
                R.id.tvRequest -> {
                    binding.check = 2
                    if (sharedPrefManager.getSide() == "1") {
                        val data = HashMap<String, String>()
                        data["type"] = "user"
                        viewModel.getMatchPendingApi(Constants.MATCH_PENDING_LIKE, data)
                    } else {
                        val data = HashMap<String, String>()
                        data["type"] = "listing"
                        viewModel.getMatchPendingApi(Constants.MATCH_PENDING_LIKE, data)
                    }

                }
            }

        }

        binding.ivSettings.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val width = v.width
                val clickedX = event.x
                if (clickedX < width / 2) {
                    if (!check) {
                        binding.check = 1
                        binding.ivSettings.setImageResource(R.drawable.first_selected)
                        sharedPrefManager.saveSide("1")
                        check = true
                        val data = HashMap<String, String>()
                        data["type"] = "user"
                        viewModel.getMatchApi(Constants.GET_MATCH, data)
                    }
                } else {
                    if (check) {
                        binding.check = 1
                        check = false
                        binding.ivSettings.setImageResource(R.drawable.second_selected)
                        sharedPrefManager.saveSide("2")
                        val data = HashMap<String, String>()
                        data["type"] = "listing"
                        viewModel.getMatchApi(Constants.GET_MATCH, data)
                    }
                }
            }
            true
        }

    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.matchObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getMatchApi" -> {
                            try {
                                val myDataModel: PendingMatchResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    matchesAdapter.clearList()
                                    matchesAdapter.list =
                                        myDataModel.data as List<PendingMatchData?>?
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "getMatchPendingApi" -> {
                            try {
                                val myDataModel: PendingMatchResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    pendingMatchesAdapter.clearList()
                                    pendingMatchesAdapter.list = myDataModel.data
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
                                        pendingMatchesAdapter.removeItem(position)
                                        pendingMatchesAdapter.notifyDataSetChanged()
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

    /** handle adapter **/
    private fun initMatchAdapter() {
        matchesAdapter =
            SimpleRecyclerViewAdapter(R.layout.matches_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    // view profile
                    R.id.ivImage -> {
                        val intent = Intent(requireContext(), MatchedProfileActivity::class.java)
                        intent.putExtra("profileId", m._id)
                        startActivity(intent)
                    }

                }
            }

        binding.rvMatches.adapter = matchesAdapter
    }

    private fun initPendingMatchAdapter() {
        pendingMatchesAdapter =
            SimpleRecyclerViewAdapter(R.layout.pending_match_rv_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    // cancel
                    R.id.ivCancel -> {
                        position = pos
                        val data = HashMap<String, Any>()
                        data["action"] = "reject"   //accept,reject
                        data["id"] = m._id.toString()
                        viewModel.acceptRejectAPi(Constants.MATCH_ACCEPT_REJECT, data)

                    }
                    // like
                    R.id.ivLike -> {
                        position = pos
                        val data = HashMap<String, Any>()
                        data["action"] = "accept" //accept,reject
                        data["id"] = m._id.toString()
                        viewModel.acceptRejectAPi(Constants.MATCH_ACCEPT_REJECT, data)

                    }
                }
            }

        binding.rvPendingMatches.adapter = pendingMatchesAdapter
    }

}