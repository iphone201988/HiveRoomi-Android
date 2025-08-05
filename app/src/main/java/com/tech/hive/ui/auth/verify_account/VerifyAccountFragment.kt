package com.tech.hive.ui.auth.verify_account

import android.graphics.Rect
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.AccountVerifyResponse
import com.tech.hive.data.model.ResendOtpResponse
import com.tech.hive.databinding.FragmentVerifyAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyAccountFragment : BaseFragment<FragmentVerifyAccountBinding>() {
    private val viewModel: VerifyAccountFragmentVM by viewModels()
    private lateinit var otpETs: Array<AppCompatEditText?>
    private lateinit var otpTimer: CountDownTimer
    private var isOtpComplete = false
    private var userEmail: String? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_verify_account
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initClick()
        // view
        initView()
        // observer
        initObserver()
        // intent
        userEmail = arguments?.getString("userEmail")
        // start timer
        startOtpTimer()
        // Make keyboard resize layout
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        // Keyboard insets listener to avoid send button getting hidden
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.setPadding(0, 0, 0, imeHeight)
            insets
        }

        if (userEmail.isNullOrEmpty()) {
            val userData = sharedPrefManager.getLoginData()
            binding.tvChoose.text =
                getString(R.string.please_enter_the_otp_below_that_we_have_sent_to, userData?.email)
            val data = HashMap<String, Any>()
            data["email"] = userData?.email.toString()
            data["type"] = 2
            viewModel.resendOtpApi(Constants.userLanguage, data, Constants.RESEND_OTP)
        } else {
            binding.tvChoose.text =
                getString(R.string.please_enter_the_otp_below_that_we_have_sent_to, userEmail)
        }

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
                        verifyAccountApi()
                    }

                }
                // resend otp button click
                R.id.tvResendTime -> {
                    val userData = sharedPrefManager.getLoginData()
                    val data = HashMap<String, Any>()
                    if (!userEmail.isNullOrEmpty()) {
                        data["email"] = userEmail.toString()
                    } else {
                        data["email"] = userData?.email.toString()
                    }
                    data["type"] = 2
                    viewModel.resendOtpApi(Constants.userLanguage, data, Constants.RESEND_OTP)
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


    /** verifyAccount api call **/
    private fun verifyAccountApi() {
        try {
            val otpData =
                "${binding.otpET1.text}" + "${binding.otpET2.text}" + "${binding.otpET3.text}" + "${binding.otpET4.text}"
            val userData = sharedPrefManager.getLoginData()
            val data = HashMap<String, Any>()
            data["otp"] = otpData
            data["type"] = 1
            if (!userEmail.isNullOrEmpty()) {
                data["email"] = userEmail.toString()
            } else {
                data["email"] = userData?.email.toString()
            }
            viewModel.verifyAccount(Constants.userLanguage, data, Constants.VERIFY_OTP)
        } catch (e: Exception) {
            Log.e("error", "verifyAccount: $e")
        }
    }


    /** api response observer **/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "verifyAccount" -> {
                            try {
                                val myDataModel: AccountVerifyResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    sharedPrefManager.saveOtpToken(true)
                                    if (myDataModel.data?.profileRole == 3) {
                                        BindingUtils.navigateWithSlide(
                                            findNavController(),
                                            R.id.navigateToProviderTypeFragment,
                                            null
                                        )
                                    } else {
                                        BindingUtils.navigateWithSlide(
                                            findNavController(),
                                            R.id.navigateToPersonalFragment,
                                            null
                                        )
                                    }
                                }

                            } catch (e: Exception) {
                                Log.e("error", "verifyAccount: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "resendOtpApi" -> {
                            try {
                                val myDataModel: ResendOtpResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        binding.otpET1.setText("")
                                        binding.otpET2.setText("")
                                        binding.otpET3.setText("")
                                        binding.otpET4.setText("")
                                        startOtpTimer()
                                    }

                                }
                            } catch (e: Exception) {
                                Log.e("error", "verifyAccount: $e")
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
                            text?.clear()  // Clear the previous EditText before focusing
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


    /** start timer ***/
    private fun startOtpTimer() {
        val totalTime = 1 * 60 * 1000L

        otpTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvResendTime.isEnabled = false
                binding.tvResendTime.isFocusable = false
                binding.tvResendTime.isClickable = false
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                binding.tvResendTime.text = " " + String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.tvResendTime.text = " 00:00"
                binding.tvResendTime.text = "Resend OTP"
                binding.tvResendTime.isEnabled = true
                binding.tvResendTime.isFocusable = true
                binding.tvResendTime.isClickable = true
                otpTimer.cancel()
                // Handle OTP expiration if needed...
            }
        }
        otpTimer.start()
    }

}
