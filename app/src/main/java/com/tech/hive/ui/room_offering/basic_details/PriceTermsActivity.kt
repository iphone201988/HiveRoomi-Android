package com.tech.hive.ui.room_offering.basic_details

import android.app.DatePickerDialog
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
import com.tech.hive.databinding.ActivityPriceTermsBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class PriceTermsActivity : BaseActivity<ActivityPriceTermsBinding>() {
    private val viewModel: BasicDetailsVM by viewModels()
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var ageAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    private var roomType: String? = null
    private var titleType: String? = null
    private var bioType: String? = null
    private var locationType: String? = null
    private var listingData: GetListingData? = null
    override fun getLayoutResource(): Int {
        return R.layout.activity_price_terms
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
        BindingUtils.statusBarStyle(this@PriceTermsActivity)
        BindingUtils.statusBarTextColor(this@PriceTermsActivity, false)
        // check state
        binding.priceType = ""
        binding.utilityPriceType = ""
        binding.depositType = ""


        // Initialize from intent
        roomType = intent.getStringExtra("roomType")
        titleType = intent.getStringExtra("titleType")
        bioType = intent.getStringExtra("bioType")
        locationType = intent.getStringExtra("locationType")

        listingData = intent.getParcelableExtra<GetListingData>("basicDetail")
        listingData?.let {
            binding.etPrice.setText(it.price.toString())
            binding.etUtilityPrice.setText(it.utilitiesPrice.toString())
            binding.etDeposit.setText(it.deposit.toString())
            binding.etContract.setText(it.contractLength)
            binding.etStay.setText(it.minimumStay)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("MMMM dd yyyy", Locale.getDefault())
            val date = inputFormat.parse(it.availableFrom.toString())
            val formattedDate = outputFormat.format(date!!)
            binding.etStatus.setText(formattedDate)
        }
    }

    /** handle clicks **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.btnContinue -> {
                    if (validate()){
                        val intent = Intent(this@PriceTermsActivity, RoomDetailsActivity::class.java)
                        intent.putExtra("roomType", roomType)
                        intent.putExtra("titleType", titleType)
                        intent.putExtra("bioType", bioType)
                        intent.putExtra("locationType", locationType)
                        intent.putExtra("priceType", binding.etPrice.text.toString().trim())
                        intent.putExtra("utilityPriceType", binding.etUtilityPrice.text.toString().trim())
                        intent.putExtra("depositType", binding.etDeposit.text.toString().trim())
                        intent.putExtra("contractType", binding.etContract.text.toString().trim())
                        intent.putExtra("availableType", binding.etStatus.text.toString().trim())
                        intent.putExtra("minimumStayType", binding.etStay.text.toString().trim())
                        if (listingData!=null){
                            intent.putExtra("basicDetail", listingData)
                        }

                        startActivity(intent)
                    }

                }

                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.etContract -> {
                    personalDialog(1)
                }

                R.id.etStay -> {
                    personalDialog(2)
                }

                R.id.etStatus, R.id.ivStatus -> {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val maxDateCalendar = Calendar.getInstance()
                    maxDateCalendar.add(Calendar.YEAR, -12)

                    val datePickerDialog = DatePickerDialog(
                        this@PriceTermsActivity,
                        { _, selectedYear, selectedMonth, selectedDay ->
                            val selectedCalendar = Calendar.getInstance()
                            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                            val sdf = SimpleDateFormat("MMMM dd yyyy", Locale.getDefault())
                            val formattedDate = sdf.format(selectedCalendar.time)
                            binding.etStatus.setText(formattedDate)

                        },
                        year,
                        month,
                        day
                    )

                    datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis
                    datePickerDialog.show()
                }
            }
        }

        // etPrice
        binding.etPrice.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.priceType = "priceType"
            } else {
                binding.priceType = ""
            }
        }


        // etUtilityPrice
        binding.etUtilityPrice.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.utilityPriceType = "utilityPriceType"
            } else {
                binding.utilityPriceType = ""
            }
        }

        // etDeposit
        binding.etDeposit.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.depositType = "depositType"
            } else {
                binding.depositType = ""
            }
        }


    }

    /** personal dialog  handel ***/
    private fun personalDialog(type: Int) {
        personal = BaseCustomDialog(this@PriceTermsActivity, R.layout.personal_dialog_item) {

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
                            binding.etContract.setText(m.toString())
                        }

                        2 -> {
                            binding.etStay.setText(m.toString())
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
        }

        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    private fun firstTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.one_month),
            getString(R.string.two_months),
            getString(R.string.three_months),
            getString(R.string.four_months),
            getString(R.string.flexible)
        )
    }

    private fun secondTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.one_month),
            getString(R.string.two_months),
            getString(R.string.three_months),
            getString(R.string.four_months),
        )
    }

    /*** add validation ***/
    private fun validate(): Boolean {
        val price = binding.etPrice.text.toString().trim()
        val utilityPrice = binding.etUtilityPrice.text.toString().trim()
        val deposit = binding.etDeposit.text.toString().trim()
        val contract = binding.etContract.text.toString().trim()
        val availableFrom = binding.etStatus.text.toString().trim()
        val minimumStay = binding.etStay.text.toString().trim()
        if (price.isEmpty()) {
            showInfoToast("Please enter price")
            return false
        } else if (utilityPrice.isEmpty()) {
            showInfoToast("Please enter utility price")
            return false
        } else if (deposit.isEmpty()) {
            showInfoToast("Please enter deposit price")
            return false
        } else if (contract.isEmpty()) {
            showInfoToast("Please select contract")
            return false
        } else if (availableFrom.isEmpty()) {
            showInfoToast("Please select date")
            return false
        } else if (minimumStay.isEmpty()) {
            showInfoToast("Please select minimum stay")
            return false
        }
        return true
    }

}