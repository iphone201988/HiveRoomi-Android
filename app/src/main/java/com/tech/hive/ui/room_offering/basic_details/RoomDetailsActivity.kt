package com.tech.hive.ui.room_offering.basic_details

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.ActivityRoomDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomDetailsActivity : BaseActivity<ActivityRoomDetailsBinding>() {
    private val viewModel: BasicDetailsVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_room_details
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
        BindingUtils.statusBarStyle(this@RoomDetailsActivity)
        BindingUtils.statusBarTextColor(this@RoomDetailsActivity, false)
        // check state
        binding.roomType = ""
        binding.sizeType = ""
        binding.statusType = ""
        binding.roommateType = ""

    }

    /** handle clicks **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btn Next
                R.id.btnContinue -> {
                    startActivity(Intent(this, RoomMateDetailsActivity::class.java))
                }
                R.id.ivBack->{
                    onBackPressedDispatcher.onBackPressed()
                }
            }

        }
        // etType
        binding.etRoom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.roomType = p0.toString()
            }

        })

        // etSize
        binding.etSize.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.sizeType = p0.toString()
            }

        })

        // etStatus
        binding.etStatus.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.statusType = p0.toString()
            }

        })
        // etRoommate
        binding.etRoommate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.roommateType = p0.toString()
            }

        })

    }

    /** handle api response **/
    private fun initObserver() {

    }

}