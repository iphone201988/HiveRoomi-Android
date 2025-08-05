package com.tech.hive.ui.for_room_mate.filters

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.amazonaws.regions.Regions
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.location.LocationSearchManager
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.RoommateModelClass
import com.tech.hive.databinding.ActivityFilterBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.text.contains

@AndroidEntryPoint
class FilterActivity() : BaseActivity<ActivityFilterBinding>() {
    private val viewModel: FilterActivityVM by viewModels()
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var ageAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    private var lat : Double?=null
    private var long : Double?=null
    override fun getLayoutResource(): Int {
        return R.layout.activity_filter
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

    /**handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@FilterActivity)
        BindingUtils.statusBarTextColor(this@FilterActivity, false)
        // check state
        binding.locationType = ""

    }

    /**handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // back button
                R.id.ivBack -> {
                    finish()
                }
                // apply filter button
                R.id.btnApply -> {
                        val age = binding.etAge.text.toString().trim()
                        val profession = binding.etProfession.text.toString().trim()
                        val campus = binding.etCampus.text.toString().trim()
                        val gender = binding.etGender.text.toString().trim().lowercase()
                       // val language = binding.etLanguage.text.toString().trim()
                        val location = binding.etLocation.text.toString().trim()
                        val roommate = RoommateModelClass(
                            age = age,
                            professionRole = profession,
                            campus = campus,
                            gender = gender,
                       //     language = language,
                            location = location,
                            lat = lat,
                            long = long
                        )
                        val resultIntent = Intent()
                        resultIntent.putExtra("filtered_roommate", roommate)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                }
                // location
                R.id.ivAge, R.id.etAge -> {
                    personalDialog(1)
                }
                // profession
                R.id.etProfession -> {
                    personalDialog(2)
                }
                // campus
                 R.id.etCampus -> {
                    personalDialog(3)
                }
                // gender
                R.id.etGender -> {
                    personalDialog(4)
                }
                // lang
                 R.id.etLanguage -> {
                    personalDialog(5)
                }


            }
        }

        // check focusable
        binding.etLocation.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.locationType = "locationType"
            } else {
                binding.locationType = ""
            }
        }

        binding.etProfession.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true && s.contains("Student")) {
                    binding.etCampus.visibility = View.VISIBLE
                    binding.tvCampus.visibility = View.VISIBLE
                    binding.ivCampus.visibility = View.VISIBLE
                } else {
                    binding.etCampus.visibility = View.GONE
                    binding.tvCampus.visibility = View.GONE
                    binding.ivCampus.visibility = View.GONE
                }
            }

        })

        val locationSearchManager = LocationSearchManager(
            context = this,
            identityPoolId = "eu-north-1:c8a8cb6e-799b-43dc-ae59-737224071479",
            region = Regions.EU_NORTH_1,
            placeIndexName = getString(R.string.place_index_name)
        )

        locationSearchManager.setupSearch(
            autoCompleteTextView = binding.etLocation, onResultSelected = { label, coordinate ->
                binding.etLocation.clearFocus()
                // Hide keyboard
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etLocation.windowToken, 0)
                lat = coordinate.latitude
                long = coordinate.longitude
            })

    }


    /** personal dialog  handel ***/
    private fun personalDialog(type : Int) {
        personal = BaseCustomDialog(this@FilterActivity, R.layout.personal_dialog_item) {

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
                    when(type){
                        1->binding.etAge.setText(m.toString())
                        2->binding.etProfession.setText(m.toString())
                        3->binding.etCampus.setText(m.toString())
                        4->binding.etGender.setText(m.toString())
                        5->binding.etLanguage.setText(m.toString())
                    }
                    personal?.dismiss()
                }
            }
        }
        when(type){
            1->ageAdapter.list = ageList()
            2->ageAdapter.list = professionRoleList()
            3->ageAdapter.list = campusList()
            4->ageAdapter.list = genderList()
            5->ageAdapter.list = languageList()
        }
        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    /** list data add  **/
    private fun professionRoleList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.student),
            getString(R.string.doctor),
            getString(R.string.nurse),
            getString(R.string.caregiver),
            getString(R.string.therapist),
            getString(R.string.physiotherapist),
            getString(R.string.social_worker),
            getString(R.string.teacher),
            getString(R.string.parent),
            getString(R.string.volunteer),
            getString(R.string.other)
        )
    }

    private fun genderList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.men), getString(R.string.women), getString(R.string.other)
        )
    }

    private fun languageList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.english), getString(R.string.italian)
        )
    }

    private fun ageList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.age_18_22),
            getString(R.string.age_23_27),
            getString(R.string.age_28_32),
            getString(R.string.age_33_37),
            getString(R.string.age_38_42),
            getString(R.string.age_43_47),
            getString(R.string.age_48_52),
            getString(R.string.age_53_57),
            getString(R.string.age_58_64),
            getString(R.string.age_65_plus)
        )
    }

    private fun campusList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.campus_university_of_milan),
            getString(R.string.campus_university_of_milan_bicocca),
            getString(R.string.campus_polytechnic_university_of_milan),
            getString(R.string.campus_luigi_bocconi_university),
            getString(R.string.campus_catholic_university_sacred_heart),
            getString(R.string.campus_vita_salute_san_raffaele_university),
            getString(R.string.campus_iulm),
            getString(R.string.campus_humanitas_university),
            getString(R.string.campus_brera_academy_of_fine_arts),
            getString(R.string.campus_naba),
            getString(R.string.campus_marangoni_institute),
            getString(R.string.campus_ied)
        )
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val age = binding.etAge.text.toString().trim()
        val profession = binding.etProfession.text.toString().trim()
        val campus = binding.etCampus.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()
        val language = binding.etLanguage.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        if (age.isEmpty()) {
            showInfoToast("Please select age")
            return false
        } else if (profession.isEmpty()) {
            showInfoToast("Please select profession")
            return false
        }else if (profession.contains("Student") && campus.isEmpty()) {
            showInfoToast("Please select campus")
            return false
        } else if (gender.isEmpty()) {
            showInfoToast("Please select gender")
            return false
        } else if (language.isEmpty()) {
            showInfoToast("Please select language")
            return false
        } else if (location.isEmpty()) {
            showInfoToast("Please enter location")
            return false
        }
        return true
    }



}