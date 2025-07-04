package com.tech.hive.ui.auth.change_password

import android.graphics.Rect
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.databinding.FragmentSetUpNewPasswordBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetUpNewPasswordFragment : BaseFragment<FragmentSetUpNewPasswordBinding>() {
    private val viewModel: ChangePasswordFragmentVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_set_up_new_password
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // check state
        binding.newType = ""
        binding.confirmType = ""
        // click
        initClick()
        // observer
        initObserver()
    }

    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
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
                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.navigateToPersonalFragment, null
                        )
                    }
                }

            }
        }


        binding.etConfirmPassword.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.confirmType = "confirm"
                } else {
                    binding.confirmType = ""
                }
            }
        binding.etNewPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.newType = "new"
            } else {
                binding.newType = ""
            }
        }


        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                Log.d("Keyboard", "Visible")
            } else {
                binding.apply {
                    newType = ""
                    confirmType = ""
                    etNewPassword.clearFocus()
                    etConfirmPassword.clearFocus()
                }

            }
        }


    }


    /***** changePassword api call ***/
    private fun changePasswordApi() {
        try {
            val data = HashMap<String, Any>()
            data["otp"] = binding.etNewPassword.text.toString().trim()
            data["type"] = binding.etConfirmPassword.text.toString().trim()
            viewModel.changePassword(Constants.userLanguage,data, "Constants.LOGIN")
        } catch (e: Exception) {
            Log.e("error", "forgotEmail: $e")
        }
    }

    /****** api response observer ***/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "changePassword" -> {
                            try {
                                //  val myDataModel: SignupResponseModel? = ImageUtils.parseJson(it.data.toString())

                            } catch (e: Exception) {
                                Log.e("error", "callSignUpApi: $e")
                            }
                        }

                    }
                }

                Status.ERROR -> {
                    showErrorToast(it.message.toString())
                }

                else -> {

                }
            }
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
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        if (newPassword.isEmpty()) {
            showInfoToast("Please enter new password")
            return false
        } else if (confirmPassword.isEmpty()) {
            showInfoToast("Please enter confirm password")
            return false
        } else if (newPassword != confirmPassword) {
            showInfoToast("Confirm password does not match password")
            return false
        }
        return true
    }

}
