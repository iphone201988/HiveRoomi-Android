package com.tech.hive.ui.for_room_mate.matches

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.showToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.MatchesModel
import com.tech.hive.databinding.FragmentMatchesBinding
import com.tech.hive.databinding.MatchesItemViewBinding
import com.tech.hive.ui.for_room_mate.filters.FilterActivity
import com.tech.hive.ui.for_room_mate.home.MatchedProfileActivity
import com.tech.hive.ui.for_room_mate.home.second.SecondMatchActivity
import com.tech.hive.ui.room_offering.discover.DiscoverySettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchesFragment : BaseFragment<FragmentMatchesBinding>() {
    private val viewModel: MatchesFragmentVM by viewModels()
    private lateinit var matchesAdapter: SimpleRecyclerViewAdapter<MatchesModel, MatchesItemViewBinding>
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
        initAdapter()

    }

    /** handle click **/
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
                }
                // request click
                R.id.tvRequest -> {
                    binding.check = 2
                }
            }

        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.matchObserver.observe(viewLifecycleOwner) {

        }

    }

    /** handle adapter **/
    private fun initAdapter() {
        matchesAdapter =
            SimpleRecyclerViewAdapter(R.layout.matches_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    // view profile
                    R.id.ivImage -> {
                        when (Constants.userType) {
                            1 -> {
                                val intent =
                                    Intent(requireContext(), MatchedProfileActivity::class.java)
                                intent.putExtra("matchType", "after")
                                startActivity(intent)
                            }

                            2 -> {
                                val intent =
                                    Intent(requireActivity(), SecondMatchActivity::class.java)
                                intent.putExtra("secondMatchType", "after")
                                startActivity(intent)
                            }

                            else -> {
                                val intent =
                                    Intent(requireContext(), MatchedProfileActivity::class.java)
                                intent.putExtra("matchType", "after")
                                startActivity(intent)
                            }
                        }

                    }
                    // cancel
                    R.id.ivCancel -> {
                        showToast("Cancel")
                    }
                    // like
                    R.id.ivLike -> {
                        showToast("Like")
                    }
                }
            }
        matchesAdapter.list = getList()
        binding.rvMatches.adapter = matchesAdapter
    }

    // list
    private fun getList(): ArrayList<MatchesModel> {
        val list = ArrayList<MatchesModel>()
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        list.add(MatchesModel(R.drawable.ic_match_dummy, "Chris Salvatore"))
        return list
    }

}