package com.tech.hive.ui.room_offering.discover

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.DiscoverAnswerModel
import com.tech.hive.data.model.DiscoverQuestionModel
import com.tech.hive.databinding.ActivityDiscoverySettingsBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverySettingsActivity : BaseActivity<ActivityDiscoverySettingsBinding>() {
    private val viewModel: DiscoverySettingsActivityVM by viewModels()
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var ageAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_discovery_settings
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@DiscoverySettingsActivity)
        BindingUtils.statusBarTextColor(this@DiscoverySettingsActivity, false)
        // click
        initOnCLick()
        // adapter
        val adapter = DiscoverQuestion(getQuestionSecondList())
        binding.rvDiscover.layoutManager = LinearLayoutManager(this)
        binding.rvDiscover.adapter = adapter

    }

    /*** all click handel  **/
    private fun initOnCLick() {
        viewModel.onClick.observe(this@DiscoverySettingsActivity) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.btnApply -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.etProvider, R.id.ivProvider -> {
                    personalDialog()
                }
            }
        }
    }


    /** personal dialog  handel ***/
    private fun personalDialog() {
        personal = BaseCustomDialog(this@DiscoverySettingsActivity, R.layout.personal_dialog_item) {

        }
        personal!!.create()
        personal!!.show()
        // adapter
        initAdapterRoom()
    }

    /** handle adapter **/
    private fun initAdapterRoom() {
        ageAdapter = SimpleRecyclerViewAdapter(R.layout.un_pin_layout, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.consMainUnPin -> {
                    binding.etProvider.setText(m.toString())
                    personal?.dismiss()
                }
            }
        }

        ageAdapter.list = secondTypeList()

        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    private fun secondTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.unfurnished),
            getString(R.string.partially_furnished),
            getString(R.string.fully_furnished),








            getString(R.string.equipped_kitchen)
        )
    }


    // question List
    private fun getQuestionSecondList(): ArrayList<DiscoverQuestionModel> {
        val list = ArrayList<DiscoverQuestionModel>()
        list.add(
            DiscoverQuestionModel(
                question = getString(R.string.amenities), answer = listOf(
                    DiscoverAnswerModel( getString(R.string.private_bathroom)),
                    DiscoverAnswerModel(getString(R.string.fast_wi_fi_smart_working_gaming)),
                    DiscoverAnswerModel(getString(R.string.equipped_kitchen)),
                    DiscoverAnswerModel(getString(R.string.washing_machine)),
                    DiscoverAnswerModel(getString(R.string.air_conditioning)),
                )
            )
        )
        list.add(
            DiscoverQuestionModel(
                question = getString(R.string.property_features), answer = listOf(
                    DiscoverAnswerModel(getString(R.string.balcony_terrace)),
                    DiscoverAnswerModel(getString(R.string.parking_space)),
                )
            )
        )
        return list
    }
}