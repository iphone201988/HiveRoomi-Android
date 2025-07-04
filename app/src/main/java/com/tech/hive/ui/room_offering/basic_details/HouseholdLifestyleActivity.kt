package com.tech.hive.ui.room_offering.basic_details

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.BoostPlanModel
import com.tech.hive.data.model.CompatibilityModel
import com.tech.hive.databinding.ActivityHouseholdLifestyleBinding
import com.tech.hive.databinding.BoostPlanItemViewBinding
import com.tech.hive.databinding.DialogBoostBinding
import com.tech.hive.databinding.FeaturesItemViewBinding
import com.tech.hive.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HouseholdLifestyleActivity : BaseActivity<ActivityHouseholdLifestyleBinding>() {
    private val viewModel: BasicDetailsVM by viewModels()

    // adapter
    private lateinit var lookingAdapter: SimpleRecyclerViewAdapter<CompatibilityModel, FeaturesItemViewBinding>
    private lateinit var boostPlanAdapter: SimpleRecyclerViewAdapter<BoostPlanModel, BoostPlanItemViewBinding>

    // dialog
    private lateinit var boostDialog: BaseCustomDialog<DialogBoostBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_household_lifestyle
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
        BindingUtils.statusBarStyle(this@HouseholdLifestyleActivity)
        BindingUtils.statusBarTextColor(this@HouseholdLifestyleActivity, false)
        // check state
        binding.roommatesType = ""
        binding.smokingType = ""
        binding.petsType = ""
        binding.cleanType = ""
        binding.lookingType = ""
        // adapter
        initAdapter()
        // dialog
        initDialog()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btn boost
                R.id.clBoost -> {
                    boostDialog.show()
                }
                // btn next
                R.id.btnContinue -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        // etRoommates
        binding.etRoommates.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.roommatesType = p0.toString()
            }

        })

        // etSmoking
        binding.etSmoking.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.smokingType = p0.toString()
            }

        })

        // etPets
        binding.etPets.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.petsType = p0.toString()
            }

        })

        // etClean
        binding.etClean.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.cleanType = p0.toString()
            }
        })

        // etLooking
        binding.etLooking.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.lookingType = p0.toString()
            }
        })

    }

    /** handle api response **/
    private fun initObserver() {

    }

    /** handle adapter **/
    private fun initAdapter() {
        // main adapter
        lookingAdapter =
            SimpleRecyclerViewAdapter(R.layout.features_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    // cancel item
                    R.id.ivCancel -> {
                        lookingAdapter.removeItem(pos)
                        lookingAdapter.notifyItemChanged(pos, null)
                    }
                }
            }
        lookingAdapter.list = getList()
        binding.rvLooking.adapter = lookingAdapter
    }

    // get list
    private fun getList(): ArrayList<CompatibilityModel> {
        val list = ArrayList<CompatibilityModel>()
        list.add(CompatibilityModel("Workers"))
        list.add(CompatibilityModel("Student"))
        return list
    }

    /** handle dialog **/
    private fun initDialog() {
        boostDialog = BaseCustomDialog(this, R.layout.dialog_boost) {
            when (it.id) {
                R.id.btnCancel -> {
                    boostDialog.dismiss()
                }

                R.id.btnBoost -> {
                    boostDialog.dismiss()
                }

            }
        }
        boostDialog.setCanceledOnTouchOutside(false)
        boostDialog.setCancelable(false)

        // dialog adapter
        boostPlanAdapter =
            SimpleRecyclerViewAdapter(R.layout.boost_plan_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    //handle single plan selection
                    R.id.clMain -> {
                        for (i in boostPlanAdapter.list.indices) {
                            if (boostPlanAdapter.list[i].isSelected) {
                                boostPlanAdapter.list[i].isSelected = false
                            }
                        }
                        boostPlanAdapter.list[pos].isSelected = true
                        boostPlanAdapter.notifyDataSetChanged()
                    }
                }
            }
        boostPlanAdapter.list = getList2()
        boostDialog.binding.rvBoostPlan.adapter = boostPlanAdapter
    }

    // get list for boost plan
    private fun getList2(): ArrayList<BoostPlanModel> {
        val list = ArrayList<BoostPlanModel>()
        list.add(BoostPlanModel("Boost for 3 Days ", "₹149", "• ₹50 per day", "• Save 15%", true))
        list.add(BoostPlanModel("Boost for 1 Week ", "₹280", "• ₹40 per day", "• Save 15%"))
        list.add(BoostPlanModel("Boost for 1 Month ", "₹900", "• ₹30 per day", "• Save 15%"))
        return list
    }

}