package com.tech.hive.ui.room_offering.basic_details

import android.content.Intent
import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.data.model.GetListingData
import com.tech.hive.databinding.ActivityAmenityBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmenityActivity : BaseActivity<ActivityAmenityBinding>() {
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
    private var floorType: String? = null
    private var elevatorType: String? = null
    private var heatingType: String? = null
    private var listingData: GetListingData? = null
    override fun getLayoutResource(): Int {
        return R.layout.activity_amenity
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
        floorType = intent.getStringExtra("floorType")
        elevatorType = intent.getStringExtra("elevatorType")
        heatingType = intent.getStringExtra("heatingType")

        listingData = intent.getParcelableExtra<GetListingData>("basicDetail")
        listingData?.let {
            if (it.privateBathroom == true){
                binding.etPrivate.setText(getString(R.string.yes))
            }else{
                binding.etPrivate.setText(getString(R.string.no))
            }
            if (it.wifi == true){
                binding.etWifi.setText(getString(R.string.yes))
            }else{
                binding.etWifi.setText(getString(R.string.no))
            }
            if (it.kitchen == true){
                binding.etEquipped.setText(getString(R.string.yes))
            }else{
                binding.etEquipped.setText(getString(R.string.no))
            }
            if (it.washingMachine == true){
                binding.etWashing.setText(getString(R.string.yes))
            }else{
                binding.etWashing.setText(getString(R.string.no))
            }
            if (it.airConditioner == true){
                binding.etAir.setText(getString(R.string.yes))
            }else{
                binding.etAir.setText(getString(R.string.no))
            }
            if (it.balcony == true){
                binding.etBalcony.setText(getString(R.string.yes))
            }else{
                binding.etBalcony.setText(getString(R.string.no))
            }
            if (it.parking == true){
                binding.etParking.setText(getString(R.string.yes))
            }else{
                binding.etParking.setText(getString(R.string.no))
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
                        val intent = Intent(this@AmenityActivity, HouseholdLifestyleActivity::class.java)
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
                        intent.putExtra("floorType", floorType)
                        intent.putExtra("elevatorType", elevatorType)
                        intent.putExtra("heatingType", heatingType)

                        intent.putExtra("privateType", binding.etPrivate.text.toString().trim())
                        intent.putExtra("wifiType", binding.etWifi.text.toString().trim())
                        intent.putExtra("equippedType", binding.etEquipped.text.toString().trim())
                        intent.putExtra("washingType", binding.etWashing.text.toString().trim())
                        intent.putExtra("airType", binding.etAir.text.toString().trim())
                        intent.putExtra("balconyType", binding.etBalcony.text.toString().trim())
                        intent.putExtra("parkingType", binding.etParking.text.toString().trim())
                        if (listingData!=null){
                            intent.putExtra("basicDetail", listingData)
                        }
                        startActivity(intent)
                    }

                }

                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.etPrivate -> {
                    personalDialog(1)
                }

                R.id.etWifi -> {
                    personalDialog(2)
                }

                R.id.etEquipped -> {
                    personalDialog(3)
                }

                R.id.etWashing -> {
                    personalDialog(4)
                }

                R.id.etAir -> {
                    personalDialog(5)
                }

                R.id.etBalcony -> {
                    personalDialog(6)
                }

                R.id.etParking -> {
                    personalDialog(7)
                }


            }
        }

    }

    /** personal dialog  handel ***/
    private fun personalDialog(type: Int) {
        personal = BaseCustomDialog(this@AmenityActivity, R.layout.personal_dialog_item) {

        }
        personal!!.create()
        personal!!.show()
        // adapter
        initAdapterRoom(type)
    }

    /** handle adapter **/
    private fun initAdapterRoom(type: Int) {
        ageAdapter = SimpleRecyclerViewAdapter(R.layout.un_pin_layout, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.consMainUnPin -> {
                    when (type) {
                        1 -> {
                            binding.etPrivate.setText(m.toString())
                        }

                        2 -> {
                            binding.etWifi.setText(m.toString())
                        }

                        3 -> {
                            binding.etEquipped.setText(m.toString())
                        }

                        4 -> {
                            binding.etWashing.setText(m.toString())
                        }

                        5 -> {
                            binding.etAir.setText(m.toString())
                        }

                        6 -> {
                            binding.etBalcony.setText(m.toString())
                        }

                        7 -> {
                            binding.etParking.setText(m.toString())
                        }
                    }
                    personal?.dismiss()
                }
            }
        }
        ageAdapter.list = firstTypeList()
        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    private fun firstTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.yes),
            getString(R.string.no),
        )
    }
    /*** add validation ***/
    private fun validate(): Boolean {
        val private = binding.etPrivate.text.toString().trim()
        val wifi = binding.etWifi.text.toString().trim()
        val equipped = binding.etEquipped.text.toString().trim()
        val washing = binding.etWashing.text.toString().trim()
        val air = binding.etAir.text.toString().trim()
        val balcony = binding.etBalcony.text.toString().trim()
        val parking = binding.etParking.text.toString().trim()

        if (private.isEmpty()) {
            showInfoToast("Please select bathroom type")
            return false
        } else if (wifi.isEmpty()) {
            showInfoToast("Please select wifi type")
            return false
        } else if (equipped.isEmpty()) {
            showInfoToast("Please select equipped kitchen")
            return false
        }
        else if (washing.isEmpty()) {
            showInfoToast("Please select washing machine")
            return false
        }
        else if (air.isEmpty()) {
            showInfoToast("Please select air conditioning")
            return false
        }
        else if (balcony.isEmpty()) {
            showInfoToast("Please select balcony")
            return false
        }else if (parking.isEmpty()) {
            showInfoToast("Please select parking")
            return false
        }
        return true
    }
}