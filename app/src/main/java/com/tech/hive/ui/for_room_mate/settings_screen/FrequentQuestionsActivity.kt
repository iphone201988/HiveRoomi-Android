package com.tech.hive.ui.for_room_mate.settings_screen

import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.FrequentModel
import com.tech.hive.databinding.ActivityFrequentQuestionsBinding
import com.tech.hive.databinding.FrequentItemViewBinding
import com.tech.hive.ui.for_room_mate.settings.SettingsVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FrequentQuestionsActivity : BaseActivity<ActivityFrequentQuestionsBinding>() {
    private val viewModel: SettingsVM by viewModels()

    // adapter
    private lateinit var frequentAdapter: SimpleRecyclerViewAdapter<FrequentModel, FrequentItemViewBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_frequent_questions
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
    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@FrequentQuestionsActivity)
        BindingUtils.statusBarTextColor(this@FrequentQuestionsActivity, false)
        // adapter
        initAdapter()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }

    }

    /** handle api response **/
    private fun initObserver() {

    }

    /** handle adapter **/
    private fun initAdapter() {
        frequentAdapter =
            SimpleRecyclerViewAdapter(R.layout.frequent_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clQn -> {
                        showToast("Clicked ${pos + 1}")
                    }
                }
            }
        frequentAdapter.list = getList()
        binding.rvFrequentQn.adapter = frequentAdapter
    }

    // list
    private fun getList(): ArrayList<FrequentModel> {
        val list = ArrayList<FrequentModel>()
        list.add(FrequentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry."))
        list.add(FrequentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry."))
        list.add(FrequentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry."))
        list.add(FrequentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry."))
        list.add(FrequentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry."))
        list.add(FrequentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry."))
        list.add(FrequentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry."))
        return list
    }


}