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
import com.tech.hive.data.model.CompatibilityModel
import com.tech.hive.data.model.GetListingData
import com.tech.hive.databinding.ActivityApartmentFeaturesBinding
import com.tech.hive.databinding.FeaturesItemViewBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import com.tech.hive.ui.room_offering.basic_details.PriceTermsActivity
import com.tech.hive.ui.room_offering.basic_details.RoomMateDetailsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApartmentFeaturesActivity : BaseActivity<ActivityApartmentFeaturesBinding>() {
    private val viewModel: BasicDetailsVM by viewModels()
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var ageAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    private var roomType: String? = null
    private var titleType: String? = null
    private var bioType: String? = null
    private var locationType: String? = null
    private var priceType: String? = null
    private var utilityPriceType: String? = null
    private var depositType: String? = null
    private var contractType: String? = null
    private var availableType: String? = null
    private var minimumStayType: String? = null

    private var roomDetailType: String? = null
    private var sizeType: String? = null
    private var furnishedType: String? = null
    private var roommateType: String? = null
    private var listingData: GetListingData? = null
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

    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@ApartmentFeaturesActivity)
        BindingUtils.statusBarTextColor(this@ApartmentFeaturesActivity, false)

        // intent data
        roomType = intent.getStringExtra("roomType")
        titleType = intent.getStringExtra("titleType")
        bioType = intent.getStringExtra("bioType")
        locationType = intent.getStringExtra("locationType")
        priceType = intent.getStringExtra("priceType")
        utilityPriceType = intent.getStringExtra("utilityPriceType")
        depositType = intent.getStringExtra("depositType")
        contractType = intent.getStringExtra("contractType")
        availableType = intent.getStringExtra("availableType")
        minimumStayType = intent.getStringExtra("minimumStayType")

        roomDetailType = intent.getStringExtra("roomDetailType")
        sizeType = intent.getStringExtra("sizeType")
        furnishedType = intent.getStringExtra("furnishedType")
        roommateType = intent.getStringExtra("roommateType")

        listingData = intent.getParcelableExtra<GetListingData>("basicDetail")
        listingData?.let {
            binding.etFloor.setText(it.floor)
            binding.etHeating.setText(it.heating)
            if (it.elevator == true){
                binding.etElevator.setText(getString(R.string.yes))
            }else{
                binding.etElevator.setText(getString(R.string.no))
            }
        }
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btn next
                R.id.btnContinue -> {
                    if (validate()){
                        val intent = Intent(this@ApartmentFeaturesActivity, AmenityActivity::class.java)
                        intent.putExtra("roomType", roomType)
                        intent.putExtra("titleType", titleType)
                        intent.putExtra("bioType", bioType)
                        intent.putExtra("locationType", locationType)
                        intent.putExtra("priceType", priceType)
                        intent.putExtra("utilityPriceType", utilityPriceType)
                        intent.putExtra("depositType", depositType)
                        intent.putExtra("contractType", contractType)
                        intent.putExtra("availableType", availableType)
                        intent.putExtra("minimumStayType", minimumStayType)
                        intent.putExtra("roomDetailType", roomDetailType)
                        intent.putExtra("sizeType", sizeType)
                        intent.putExtra("furnishedType", furnishedType)
                        intent.putExtra("roommateType", roommateType)
                        intent.putExtra("floorType", binding.etFloor.text.toString().trim())
                        intent.putExtra("elevatorType", binding.etElevator.text.toString().trim())
                        intent.putExtra("heatingType", binding.etHeating.text.toString().trim())
                        if (listingData!=null){
                            intent.putExtra("basicDetail", listingData)
                        }
                        startActivity(intent)
                    }
                }
                R.id.ivBack->{
                    onBackPressedDispatcher.onBackPressed()
                }
                R.id.etFloor->{
                    personalDialog(1)
                }
                R.id.etElevator->{
                    personalDialog(2)
                }
                R.id.etHeating->{
                    personalDialog(3)
                }
            }
        }

    }





    /** personal dialog  handel ***/
    private fun personalDialog(type:Int) {
        personal = BaseCustomDialog(this@ApartmentFeaturesActivity, R.layout.personal_dialog_item) {

        }
        personal!!.create()
        personal!!.show()
        // adapter
        initAdapterRoom(type)
    }

    /** handle adapter **/
    private fun initAdapterRoom(type:Int) {
        ageAdapter = SimpleRecyclerViewAdapter(R.layout.un_pin_layout, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.consMainUnPin -> {
                    when(type){
                        1->{
                            binding.etFloor.setText(m.toString())
                        }
                        2->{
                            binding.etElevator.setText(m.toString())
                        }
                        3->{
                            binding.etHeating.setText(m.toString())
                        }

                    }

                    personal?.dismiss()
                }
            }
        }
        when(type){
            1->{
                ageAdapter.list = firstTypeList()
            }
            2->{
                ageAdapter.list = secondTypeList()
            }
            3->{
                ageAdapter.list = thirdTypeList()
            }
        }

        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    private fun firstTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.floor_ground),
            getString(R.string.floor_1st),
            getString(R.string.floor_2nd),
            getString(R.string.floor_3rd),
            getString(R.string.floor_4th),
            getString(R.string.floor_5th_plus)
        )
    }
    private fun secondTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.yes),
            getString(R.string.no),

        )
    }
    private fun thirdTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.independent),
            getString(R.string.centralized),
            getString(R.string.heating_no),
        )
    }

    /*** add validation ***/
    private fun validate(): Boolean {
        val floor = binding.etFloor.text.toString().trim()
        val elevator = binding.etElevator.text.toString().trim()
        val heating = binding.etHeating.text.toString().trim()

        if (floor.isEmpty()) {
            showInfoToast("Please select floor type")
            return false
        } else if (elevator.isEmpty()) {
            showInfoToast("Please select elevator")
            return false
        } else if (heating.isEmpty()) {
            showInfoToast("Please select heating")
            return false
        }
        return true
    }
}