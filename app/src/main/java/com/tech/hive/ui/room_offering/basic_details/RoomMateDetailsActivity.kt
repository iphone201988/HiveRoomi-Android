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
import com.tech.hive.data.model.RoomMateModelItem
import com.tech.hive.databinding.ActivityRoomMateDetailsBinding
import com.tech.hive.databinding.AddRoomMateItemViewBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomMateDetailsActivity : BaseActivity<ActivityRoomMateDetailsBinding>() {
    private val viewModel: BasicDetailsVM by viewModels()
    private lateinit var roomMateAdapter: SimpleRecyclerViewAdapter<RoomMateModelItem, AddRoomMateItemViewBinding>
    private var roomiesList = ArrayList<RoomMateModelItem>()
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
        return R.layout.activity_room_mate_details
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
        BindingUtils.statusBarStyle(this@RoomMateDetailsActivity)
        BindingUtils.statusBarTextColor(this@RoomMateDetailsActivity, false)
        // check state
        binding.ageType = ""

        // adapter
        initAdapter()

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

        roomDetailType = intent.getStringExtra("roomDetailType")
        sizeType = intent.getStringExtra("sizeType")
        furnishedType = intent.getStringExtra("furnishedType")
        roommateType = intent.getStringExtra("roommateType")
        listingData = intent.getParcelableExtra<GetListingData>("basicDetail")
        listingData?.let {
            it.roommates?.filterNotNull()?.forEach { list ->
                roomiesList.add(RoomMateModelItem(list.gender ?: "", list.age.toString() ?: ""))
            }

            roomMateAdapter.list = roomiesList
        }
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btn add
                R.id.btnAdd -> {
                    if (validate()) {
                        roomMateAdapter.list.add(
                            RoomMateModelItem(
                                binding.etGender.text.toString().trim().lowercase(),
                                binding.etAge.text.toString().trim()
                            )
                        )
                        binding.etAge.setText("")
                        binding.etGender.setText("")
                        binding.etAge.clearFocus()
                        roomMateAdapter.notifyDataSetChanged()
                    }
                }
                // btn next
                R.id.btnContinue -> {
                    if (roomMateAdapter.list.isNotEmpty()) {
                        HouseholdLifestyleActivity.roomMateModelItem =
                            roomMateAdapter.list as ArrayList<RoomMateModelItem>
                        val intent = Intent(
                            this@RoomMateDetailsActivity,
                            ApartmentFeaturesActivity::class.java
                        )
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
                        if (listingData != null) {
                            intent.putExtra("basicDetail", listingData)
                        }

                        startActivity(intent)
                    } else {
                        showInfoToast("Please add at least one room mate")
                    }

                }

                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.etGender -> {
                    personalDialog()
                }
            }
        }

        // etAge

        binding.etAge.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ageType = "ageType"
            } else {
                binding.ageType = ""
            }
        }


    }


    /** handle adapter **/
    private fun initAdapter() {
        roomMateAdapter =
            SimpleRecyclerViewAdapter(R.layout.add_room_mate_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    // cancel item
                    R.id.ivCancel -> {
                        roomMateAdapter.removeItem(pos)
                        roomMateAdapter.notifyItemChanged(pos, null)
                    }
                }
            }
        roomMateAdapter.list = roomiesList
        binding.rvAddRoommate.adapter = roomMateAdapter
    }

    /** validations **/
    private fun validate(): Boolean {
        if (binding.etGender.text.toString().isEmpty()) {
            showToast("Please select gender")
            return false
        } else if (binding.etAge.text.toString().isEmpty()) {
            showToast("Please enter age")
            return false
        }
        return true
    }


    /** personal dialog  handel ***/
    private fun personalDialog() {
        personal = BaseCustomDialog(this@RoomMateDetailsActivity, R.layout.personal_dialog_item) {

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
                    binding.etGender.setText(m.toString())
                    personal?.dismiss()
                }
            }
        }
        ageAdapter.list = firstTypeList()
        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    private fun firstTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.men),
            getString(R.string.women),
            getString(R.string.other),

            )
    }


}