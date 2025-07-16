package com.tech.hive.ui.room_offering.basic_details

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.GetListingData
import com.tech.hive.databinding.ActivityRoomDetailsBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomDetailsActivity : BaseActivity<ActivityRoomDetailsBinding>() {
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
    private var listingData: GetListingData? = null
    override fun getLayoutResource(): Int {
        return R.layout.activity_room_details
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@RoomDetailsActivity)
        BindingUtils.statusBarTextColor(this@RoomDetailsActivity, false)
        // check state
        binding.sizeType = ""
        // click
        initOnClick()
        // get intent data
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
        listingData = intent.getParcelableExtra<GetListingData>("basicDetail")
        listingData?.let {
            binding.etRoom.setText(it.roomType)
            binding.etSize.setText(it.size.toString())
            binding.etStatus.setText(it.furnishingStatus)
            if (it.roommates?.isNotEmpty() == true){
                binding.etRoommate.setText(getString(R.string.yes))
            }else{
                binding.etRoommate.setText(getString(R.string.no))
            }

        }
    }

    /** handle clicks **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btn Next
                R.id.btnContinue -> {
                    if (validate()) {
                        if (binding.etRoommate.text.toString().trim().contains("Yes")){
                            val intent = Intent(this@RoomDetailsActivity, RoomMateDetailsActivity::class.java)
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
                            intent.putExtra("roomDetailType", binding.etRoom.text.toString().trim())
                            intent.putExtra("sizeType", binding.etSize.text.toString().trim())
                            intent.putExtra("furnishedType", binding.etStatus.text.toString().trim())
                            intent.putExtra("roommateType", binding.etRoommate.text.toString().trim())
                            if (listingData!=null){
                                intent.putExtra("basicDetail", listingData)
                            }
                            startActivity(intent)
                        }else{
                            val intent = Intent(this@RoomDetailsActivity, ApartmentFeaturesActivity::class.java)
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
                            intent.putExtra("roomDetailType", binding.etRoom.text.toString().trim())
                            intent.putExtra("sizeType", binding.etSize.text.toString().trim())
                            intent.putExtra("furnishedType", binding.etStatus.text.toString().trim())
                            intent.putExtra("roommateType", binding.etRoommate.text.toString().trim())
                            if (listingData!=null){
                                intent.putExtra("basicDetail", listingData)
                            }
                            startActivity(intent)
                        }

                    }

                }

                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.etRoommate -> {
                    personalDialog(3)
                }

                R.id.etStatus -> {
                    personalDialog(2)
                }

                R.id.etRoom -> {
                    personalDialog(1)
                }
            }
        }


        // etSize
        binding.etSize.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.sizeType = "sizeType"
            } else {
                binding.sizeType = ""
            }
        }


    }

    /** personal dialog  handel ***/
    private fun personalDialog(type: Int) {
        personal = BaseCustomDialog(this@RoomDetailsActivity, R.layout.personal_dialog_item) {

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
                            binding.etRoom.setText(m.toString())
                        }

                        2 -> {
                            binding.etStatus.setText(m.toString())

                        }

                        3 -> {
                            binding.etRoommate.setText(m.toString())
                        }

                    }

                    personal?.dismiss()
                }
            }
        }
        when (type) {
            1 -> {
                ageAdapter.list = firstTypeList()
            }

            2 -> {
                ageAdapter.list = secondTypeList()
            }

            3 -> {
                ageAdapter.list = thirdTypeList()
            }
        }

        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    private fun firstTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.single_rooms),
            getString(R.string.double_rooms),
            getString(R.string.shared_room),

            )
    }

    private fun secondTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.unfurnished),
            getString(R.string.partially_furnished),
            getString(R.string.fully_furnished),
        )
    }

    private fun thirdTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.yes),
            getString(R.string.no),

            )
    }

    /*** add validation ***/
    private fun validate(): Boolean {
        val room = binding.etRoom.text.toString().trim()
        val size = binding.etSize.text.toString().trim()
        val deposit = binding.etStatus.text.toString().trim()
        val roommate = binding.etRoommate.text.toString().trim()

        if (room.isEmpty()) {
            showInfoToast("Please select room type")
            return false
        } else if (size.isEmpty()) {
            showInfoToast("Please enter size")
            return false
        } else if (deposit.isEmpty()) {
            showInfoToast("Please select  furnished status")
            return false
        } else if (roommate.isEmpty()) {
            showInfoToast("Please select roommate type")
            return false
        }
        return true
    }


}