package com.tech.hive.ui.for_room_mate.profile

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.activity.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.databinding.ActivityChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_change_password
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
        // check state
        binding.passwordType = ""
        binding.newType = ""
        binding.confirmType = ""
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // click
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }
                // ivVisiblePassword button click
                R.id.ivVisiblePassword -> {
                    showOrHidePassword()
                }
                // ivNewVisiblePassword button click
                R.id.ivVisibleNewPassword -> {
                    showOrHideNewPassword()
                }
                // ivVisibleConfirmPassword button click
                R.id.ivVisibleConfirmPassword -> {
                    showOrHideConfirmPassword()
                }
                // btnChange Password button click
                R.id.btnChangePassword -> {
                    onBackPressedDispatcher.onBackPressed()
                }
            }

        }
        // etPassword
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.passwordType = p0.toString()
            }

        })

        // etNewPassword
        binding.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.newType = p0.toString()
            }

        })

        // etConfirmPassword
        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.confirmType = p0.toString()
            }

        })
    }

    /*** show or confirm hide password **/
    private fun showOrHidePassword() {
        if (binding.etPassword.text.toString().trim().isNotEmpty()) {
            if (binding.etPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.ivVisiblePassword.setImageResource(R.drawable.show_password)
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.ivVisiblePassword.setImageResource(R.drawable.hide_password)
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etPassword.setSelection(binding.etPassword.length())
        }
    }

    /*** show or confirm hide password **/
    private fun showOrHideNewPassword() {
        if (binding.etNewPassword.text.toString().trim().isNotEmpty()) {
            if (binding.etNewPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.ivVisibleNewPassword.setImageResource(R.drawable.show_password)
                binding.etNewPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.ivVisibleNewPassword.setImageResource(R.drawable.hide_password)
                binding.etNewPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etNewPassword.setSelection(binding.etNewPassword.length())
        }
    }

    /*** show or hide confirm password **/
    private fun showOrHideConfirmPassword() {
        if (binding.etConfirmPassword.text.toString().trim().isNotEmpty()) {
            if (binding.etConfirmPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.ivVisibleConfirmPassword.setImageResource(R.drawable.show_password)
                binding.etConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.ivVisibleConfirmPassword.setImageResource(R.drawable.hide_password)
                binding.etConfirmPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.length())
        }
    }


    /** handle api response **/
    private fun initObserver() {
        viewModel.profileObserver.observe(this) {

        }

    }

}