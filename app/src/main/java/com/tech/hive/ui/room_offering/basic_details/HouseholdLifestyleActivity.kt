package com.tech.hive.ui.room_offering.basic_details

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.data.model.HomeRoomType
import com.tech.hive.data.model.ImageModel
import com.tech.hive.databinding.ActivityHouseholdLifestyleBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import com.tech.hive.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class HouseholdLifestyleActivity : BaseActivity<ActivityHouseholdLifestyleBinding>() {
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

    private var privateType: String? = null
    private var wifiType: String? = null
    private var equippedType: String? = null
    private var washingType: String? = null
    private var airType: String? = null
    private var balconyType: String? = null
    private var parkingType: String? = null

    companion object {
        var sendImage = ArrayList<ImageModel>()
        var sendVideo: File? = null
    }

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
        floorType = intent.getStringExtra("floorType")
        elevatorType = intent.getStringExtra("elevatorType")
        heatingType = intent.getStringExtra("heatingType")

        privateType = intent.getStringExtra("privateType")
        wifiType = intent.getStringExtra("wifiType")
        equippedType = intent.getStringExtra("equippedType")
        washingType = intent.getStringExtra("washingType")
        airType = intent.getStringExtra("airType")
        balconyType = intent.getStringExtra("balconyType")
        parkingType = intent.getStringExtra("parkingType")
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btn next
                R.id.btnContinue -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }

                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }


                R.id.etSmoking -> {
                    personalDialog(1)
                }

                R.id.etPets -> {
                    personalDialog(2)
                }

                R.id.etClean -> {
                    personalDialog(3)
                }

                R.id.etLooking -> {
                    personalDialog(4)
                }
            }


        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.basicDetailObserver.observe(this@HouseholdLifestyleActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "basicDetailApiCall" -> {
                            try {
                                val myDataModel: HomeRoomType? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {

                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApiListening: $e")
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


    /** personal dialog  handel ***/
    private fun personalDialog(type: Int) {
        personal =
            BaseCustomDialog(this@HouseholdLifestyleActivity, R.layout.personal_dialog_item) {

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
                            binding.etSmoking.setText(m.toString())
                        }

                        2 -> {
                            binding.etPets.setText(m.toString())
                        }

                        3 -> {
                            binding.etClean.setText(m.toString())
                        }

                        4 -> {
                            binding.etLooking.setText(m.toString())
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
                ageAdapter.list = thirdList()
            }

            4 -> {
                ageAdapter.list = fourList()
            }
        }

        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    private fun firstTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.never),
            getString(R.string.sometimes),
            getString(R.string.often),

            )
    }

    private fun secondTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.yes),
            getString(R.string.no),

            )
    }

    private fun thirdList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.one),
            getString(R.string.two),
            getString(R.string.three),
            getString(R.string.four),
            getString(R.string.five),

            )
    }

    private fun fourList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.student),
            getString(R.string.workers),
            getString(R.string.anyone),


            )
    }


}