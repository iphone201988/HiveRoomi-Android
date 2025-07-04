package com.tech.hive.ui.room_offering.basic_details

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.ActivityPriceTermsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PriceTermsActivity : BaseActivity<ActivityPriceTermsBinding>() {
    private val viewModel: BasicDetailsVM by viewModels()
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
        // observer
        initObserver()
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
        binding.contractType = ""
        binding.availableType = ""
        binding.stayType = ""
    }

    /** handle clicks **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.btnContinue -> {
                    startActivity(Intent(this, RoomDetailsActivity::class.java))
                }

                R.id.ivBack->{
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        // etPrice
        binding.etPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.priceType = p0.toString()
            }

        })
        // etUtilityPrice
        binding.etUtilityPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.utilityPriceType = p0.toString()
            }

        })
        // etDeposit
        binding.etDeposit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.depositType = p0.toString()
            }

        })
        // etContract
        binding.etContract.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.contractType = p0.toString()
            }

        })

        // etStay
        binding.etStay.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.stayType = p0.toString()
            }

        })

    }

    /** handle api response **/
    private fun initObserver() {

    }

}