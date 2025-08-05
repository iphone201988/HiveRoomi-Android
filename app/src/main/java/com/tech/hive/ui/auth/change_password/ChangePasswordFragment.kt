package com.tech.hive.ui.auth.change_password

import android.graphics.Rect
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showSuccessToast
import com.tech.hive.data.api.Constants
import com.tech.hive.databinding.FragmentChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {
    private val viewModel: ChangePasswordFragmentVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_change_password
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // check state
        binding.emailType = ""
        // click
        initClick()
        // observer
        initObserver()
        // Make keyboard resize layout
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        // Keyboard insets listener to avoid send button getting hidden
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.setPadding(0, 0, 0, imeHeight)
            insets
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
                R.id.btnChangePassword -> {
                    if (validate()) {
                        showLoading()
                        forgotPassword(binding.etEmail.text.toString().trim())
//                        BindingUtils.navigateWithSlide(
//                            findNavController(), R.id.navigateToChangePasswordVerifyFragment, null
//                        )
                    }

                }


            }
        }
        // check focusable
        binding.etEmail.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.emailType = "email"
            } else {
                binding.emailType = ""
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
                binding.emailType = ""
                binding.etEmail.clearFocus()
            }
        }

    }


    /** Forgot Password using Firebase **/
    private fun forgotPassword(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                hideLoading()
                if (task.isSuccessful) {
                    showSuccessToast("Password reset link sent to your email.")
                    findNavController().popBackStack()
                } else {
                    hideLoading()
                    showErrorToast(task.exception?.message)
                }
            }
    }

    /***** forgot email api call ***/
    private fun forgotEmailApi() {
        try {
            val data = HashMap<String, Any>()
            data["email"] = binding.etEmail.text.toString().trim()
            viewModel.forgotEmail(Constants.userLanguage,data, "Constants.LOGIN")
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
                        "forgotEmail" -> {
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
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            showInfoToast("Please enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showInfoToast("Please enter a valid email")
            return false
        }
        return true
    }


}
