package com.tech.hive.ui.for_room_mate.settings_screen

import android.util.Log
import android.view.View
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
import com.tech.hive.data.model.CommonQuestionsData
import com.tech.hive.data.model.CommonQuestionsResponse
import com.tech.hive.data.model.PrivacyModel
import com.tech.hive.databinding.ActivityCommunityBinding
import com.tech.hive.databinding.CommunityRvItemBinding
import com.tech.hive.databinding.PrivacyPolicyRvItemBinding
import com.tech.hive.ui.for_room_mate.settings.SettingsVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityActivity : BaseActivity<ActivityCommunityBinding>() {
    private val viewModel: SettingsVM by viewModels()

    // adapter
    private lateinit var communityAdapter: SimpleRecyclerViewAdapter<CommonQuestionsData, CommunityRvItemBinding>
    private lateinit var privacyPolicyAdapter: SimpleRecyclerViewAdapter<PrivacyModel, PrivacyPolicyRvItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_community
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

        // get intent data
        val intent = intent.getStringExtra("typeCommunity")
        if (intent != null) {
            if (intent.contains("community")) {

                binding.tvHeading.text = getString(R.string.welcome_to_nour_community)
                binding.tvSubHeading.text = getString(R.string.community_title)
                binding.btnGotIt.text = getString(R.string.got_it)
                binding.tvTitle.text = getString(R.string.community_guidelines)
                binding.tvAnswer.text =
                    getString(R.string.i_have_read_and_agree_to_follow_the_community_guidelines)
                binding.rvPrivacy.visibility = View.INVISIBLE
                binding.rvCommunity.visibility = View.VISIBLE
                // api call
                viewModel.askedQuestionsApiCall(com.tech.hive.data.api.Constants.USER_COMMUNITY_GUIDELINES)
            } else {
                binding.tvHeading.text = getString(R.string.your_privacy_matters)
                binding.tvSubHeading.text = getString(R.string.privacy_policy_title)
                binding.btnGotIt.text = getString(R.string.accept)
                binding.tvTitle.text = getString(R.string.privacy_policy)
                binding.tvAnswer.text =
                    getString(R.string.i_have_read_and_agree_to_follow_the_privacy_policy)
                binding.rvPrivacy.visibility = View.VISIBLE
                binding.rvCommunity.visibility = View.INVISIBLE
            }
        }
    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@CommunityActivity)
        BindingUtils.statusBarTextColor(this@CommunityActivity, false)
        // adapter
        initAdapter()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    finish()
                }

                R.id.btnGotIt -> {
                    finish()
                }

                R.id.ivCheck -> {
                    binding.isCheck = !(binding.isCheck ?: false)
                }

            }
        }

    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.settingObserver.observe(this@CommunityActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "askedQuestionsApiCall" -> {
                            try {
                                val myDataModel: CommonQuestionsResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    communityAdapter.list = myDataModel.data
                                    showSuccessToast(myDataModel.message.toString())

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
        communityAdapter =
            SimpleRecyclerViewAdapter(R.layout.community_rv_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clQn -> {
                        showToast("Clicked ${pos + 1}")
                    }
                }
            }

        binding.rvCommunity.adapter = communityAdapter

        privacyPolicyAdapter =
            SimpleRecyclerViewAdapter(R.layout.privacy_policy_rv_item, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clQn -> {
                        showToast("Clicked ${pos + 1}")
                    }
                }
            }
        privacyPolicyAdapter.list = getPrivacyList()
        binding.rvPrivacy.adapter = privacyPolicyAdapter
    }


    // list
    private fun getPrivacyList(): ArrayList<PrivacyModel> {
        val list = ArrayList<PrivacyModel>()
        list.add(
            PrivacyModel(
                "Lorem Ipsum ",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            )
        )
        list.add(
            PrivacyModel(
                "Lorem Ipsum ",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            )
        )
        list.add(
            PrivacyModel(
                "Lorem Ipsum ",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            )
        )
        list.add(
            PrivacyModel(
                "Lorem Ipsum ",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            )
        )
        list.add(
            PrivacyModel(
                "Lorem Ipsum ",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            )
        )
        list.add(
            PrivacyModel(
                "Lorem Ipsum ",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            )
        )
        list.add(
            PrivacyModel(
                "Lorem Ipsum ",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            )
        )
        list.add(
            PrivacyModel(
                "Lorem Ipsum ",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
            )
        )
        return list
    }


}