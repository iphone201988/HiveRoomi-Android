package com.tech.hive.ui.for_room_mate.profile

import android.text.InputType
import android.view.View
import androidx.activity.viewModels
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.showSuccessToast
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
                    if (validate()) {
                        val user = FirebaseAuth.getInstance().currentUser
                        val userEmail = sharedPrefManager.getLoginData()?.email
                        val oldPassword = binding.etPassword.text.toString().trim()
                        val newPassword = binding.etNewPassword.text.toString().trim()
                        val credential =
                            EmailAuthProvider.getCredential(userEmail.toString(), oldPassword)

                        user?.reauthenticate(credential)?.addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                user.updatePassword(newPassword)
                                    .addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            onBackPressedDispatcher.onBackPressed()
                                            showSuccessToast("Password changed successfully")
                                        } else {
                                            showToast(updateTask.exception?.message.toString())

                                        }
                                    }
                            } else {
                                showToast(authTask.exception?.message.toString())
                            }
                        }
                    }
                }
            }

        }
        // etPassword
        binding.etPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.passwordType = "passwordType"
            } else {
                binding.passwordType = ""
            }
        }


        // etNewPassword
        binding.etNewPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.newType = "newType"
            } else {
                binding.newType = ""
            }
        }


        // etConfirmPassword
        binding.etConfirmPassword.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.confirmType = "confirmType"
                } else {
                    binding.confirmType = ""
                }
            }

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


    /*** add validation ***/
    private fun validate(): Boolean {
        val password = binding.etPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (password.isEmpty()) {
            showInfoToast("Please enter old password")
            return false
        } else if (password.length < 6) {
            showInfoToast("Password must be at least 6 characters")
            return false
        } else if (!password.any { it.isUpperCase() }) {
            showInfoToast("Password must contain at least one uppercase letter")
            return false
        }
        if (newPassword.isEmpty()) {
            showInfoToast("Please enter new password")
            return false
        } else if (newPassword.length < 6) {
            showInfoToast("Password must be at least 6 characters")
            return false
        } else if (!newPassword.any { it.isUpperCase() }) {
            showInfoToast("Password must contain at least one uppercase letter")
            return false
        } else if (confirmPassword.isEmpty()) {
            showInfoToast("Please enter confirm password")
            return false
        } else if (newPassword != confirmPassword) {
            showInfoToast("New password and Confirm password do not match")
            return false
        }

        return true
    }
}