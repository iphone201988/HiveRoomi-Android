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
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.CompatibilityModel
import com.tech.hive.databinding.ActivityApartmentFeaturesBinding
import com.tech.hive.databinding.FeaturesItemViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApartmentFeaturesActivity : BaseActivity<ActivityApartmentFeaturesBinding>() {
    private val viewModel: BasicDetailsVM by viewModels()
    private lateinit var amenitiesAdapter: SimpleRecyclerViewAdapter<CompatibilityModel, FeaturesItemViewBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_apartment_features
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
        BindingUtils.statusBarStyle(this@ApartmentFeaturesActivity)
        BindingUtils.statusBarTextColor(this@ApartmentFeaturesActivity, false)
        // check state
        binding.floorType = ""
        binding.elevatorType = ""
        binding.heatingType = ""
        binding.amenitiesType = ""
        // adapter
        initAdapter()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btn next
                R.id.btnContinue -> {
                    startActivity(Intent(this, HouseholdLifestyleActivity::class.java))
                }
            }
        }
        // etFloor
        binding.etFloor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.floorType = p0.toString()
            }

        })
        // etElevator
        binding.etElevator.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.elevatorType = p0.toString()
            }

        })
        // etHeating
        binding.etHeating.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.heatingType = p0.toString()
            }

        })
        // etAmenities
        binding.etAmenities.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.amenitiesType = p0.toString()
            }

        })
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.basicDetailObserver.observe(this) {

        }

    }

    /** handle adapter **/
    private fun initAdapter() {
        amenitiesAdapter =
            SimpleRecyclerViewAdapter(R.layout.features_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    // cancel item
                    R.id.ivCancel -> {
                        amenitiesAdapter.removeItem(pos)
                        amenitiesAdapter.notifyItemChanged(pos, null)
                    }
                }
            }
        amenitiesAdapter.list = getList()
        binding.rvAmenities.adapter = amenitiesAdapter
    }

    // getList
    private fun getList(): ArrayList<CompatibilityModel> {
        val list = ArrayList<CompatibilityModel>()
        list.add(CompatibilityModel("Laundry"))
        list.add(CompatibilityModel("Parking"))
        return list
    }


}