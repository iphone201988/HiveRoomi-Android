package com.tech.hive.ui.for_room_mate.filters

import android.view.View
import androidx.activity.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.ActivityFilterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterActivity : BaseActivity<ActivityFilterBinding>() {
    private val viewModel: FilterActivityVM by viewModels()
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
        // observer
        initObserver()
    }

    /**handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@FilterActivity)
        BindingUtils.statusBarTextColor(this@FilterActivity, false)
        // check state
        binding.locationType = ""
        binding.roleType = ""
        binding.genderType = ""
        binding.languageType = ""
        binding.ageType = ""
        binding.campusType = ""

    }

    /**handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // back button
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }
                // apply filter button
                R.id.btnApply -> {
                    onBackPressedDispatcher.onBackPressed()
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

        // check focusable
        binding.etAge.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ageType = "ageType"
            } else {
                binding.ageType = ""
            }
        }

        // check focusable
        binding.etCampus.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.campusType = "campusType"
            } else {
                binding.campusType = ""
            }
        }

        // check focusable
        binding.etLanguage.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.languageType = "languageType"
            } else {
                binding.languageType = ""
            }
        }

        // check focusable
        binding.etGender.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.genderType = "genderType"
            } else {
                binding.genderType = ""
            }
        }

        // check focusable
        binding.etProfession.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.roleType = "roleType"
            } else {
                binding.roleType = ""
            }
        }


    }

    /**handle api response **/
    private fun initObserver() {
        viewModel.filterObserver.observe(this) {}
    }

}