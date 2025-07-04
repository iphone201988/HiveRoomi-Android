package com.tech.hive.ui.auth.change_password

import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.databinding.FragmentChangePasswordVerifyBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangePasswordVerifyFragment : BaseFragment<FragmentChangePasswordVerifyBinding>() {
    private val viewModel: ChangePasswordFragmentVM by viewModels()
    private lateinit var otpETs: Array<AppCompatEditText?>
    var isOtpComplete = false
    override fun getLayoutResource(): Int {
        return R.layout.fragment_change_password_verify
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initClick()
        // observer
        initObserver()
        // view
        initView()
    }

    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // btnVerify button click
                R.id.btnVerify -> {
                    if (validate()) {
                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.navigateToSetupNewPasswordFragment, null
                        )
                    }
                }

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
                    otpET1.clearFocus()
                    otpET2.clearFocus()
                    otpET3.clearFocus()
                    otpET4.clearFocus()
                }

            }
        }

    }


    /*** view ***/
    private fun initView() {
        otpETs = arrayOf(
            binding.otpET1, binding.otpET2, binding.otpET3, binding.otpET4
        )
        otpETs.forEachIndexed { index, editText ->
            editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty() && index != otpETs.size - 1) {
                        otpETs[index + 1]?.requestFocus()
                    }

                    // Check if all OTP fields are filled
                    isOtpComplete = otpETs.all { it!!.text?.isNotEmpty() == true }

                }
            })

            editText?.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text?.isEmpty() == true && index != 0) {
                        otpETs[index - 1]?.apply {
                            text?.clear()
                            requestFocus()
                        }
                    }
                }
                // Check if all OTP fields are filled
                isOtpComplete = otpETs.all { it!!.text?.isNotEmpty() == true }

                false
            }
        }
    }

    /***** verifyEmail api call ***/
    private fun verifyEmailApi() {
        try {
            val otpData =
                "${binding.otpET1.text}" + "${binding.otpET2.text}" + "${binding.otpET3.text}" + "${binding.otpET4.text}"
            val data = HashMap<String, Any>()
            data["otp"] = otpData
            data["type"] = 1
            viewModel.verifyEmail(Constants.userLanguage,data, "Constants.LOGIN")
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
                        "verifyEmail" -> {
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


    /*** add validation ***/
    private fun validate(): Boolean {
        val first = binding.otpET1.text.toString().trim()
        val second = binding.otpET2.text.toString().trim()
        val third = binding.otpET3.text.toString().trim()
        val four = binding.otpET4.text.toString().trim()
        if (first.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (second.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (third.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        } else if (four.isEmpty()) {
            showInfoToast("Please enter valid otp")
            return false
        }
        return true
    }

}