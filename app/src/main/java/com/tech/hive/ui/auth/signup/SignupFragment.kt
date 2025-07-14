package com.tech.hive.ui.auth.signup

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.LoginResponse
import com.tech.hive.databinding.FragmentSignupBinding
import com.tech.hive.ui.auth.login.LoginFragmentVM
import com.tech.hive.ui.enum_class.DeviceType
import com.tech.hive.ui.enum_class.LoginType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONException
import java.util.TimeZone


@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>() {
    private val viewModel: LoginFragmentVM by viewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var token = ""
    private var loginManager : LoginManager ? = null
    private var callbackManager : CallbackManager ? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_signup
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // check state
        binding.emailType = ""
        binding.passwordType = ""
        binding.confirmType = ""
        binding.fullNameType = ""
        // click
        initClick()
        // observer
        initObserver()

        // firebase get token
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            token = it.result

        }
        // firebase initialize
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)


    }


    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // ivVisiblePassword button click
                R.id.ivVisiblePassword -> {
                    showOrHidePassword()
                }
                // ivVisibleConfirmPassword button click
                R.id.ivVisibleConfirmPassword -> {
                    showOrHideConfirmPassword()
                }

                // tvLogin button click
                R.id.tvLogin -> {
                    BindingUtils.preventMultipleClick(it)
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToLoginFragment, null
                    )
                }
                // btnContinue button click
                R.id.btnSign -> {
                    BindingUtils.preventMultipleClick(it)
                    if (validate()) {
                        lifecycleScope.launch {
                            showLoading()
                            val userEmail = binding.etEmail.text.toString().trim()
                            val userPassword = binding.etPassword.text.toString().trim()
                            signUpWithFirebase(userEmail, userPassword)
                        }

                    }
                }

                // google button click
                R.id.clGoogle -> {
                    BindingUtils.preventMultipleClick(it)
                    signIn()
                }
                // facebook button click
                R.id.clFaceBook -> {
                    BindingUtils.preventMultipleClick(it)
                    facebookLogin()
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
        binding.etPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.passwordType = "password"
            } else {
                binding.passwordType = ""
            }
        }

        binding.etConfirmPassword.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.confirmType = "confirmType"
                } else {
                    binding.confirmType = ""
                }
            }
        binding.etFullName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.fullNameType = "fullNameType"
            } else {
                binding.fullNameType = ""
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
                    passwordType = ""
                    emailType = ""
                    confirmType = ""
                    fullNameType = ""
                    etEmail.clearFocus()
                    etPassword.clearFocus()
                    etConfirmPassword.clearFocus()
                    etFullName.clearFocus()
                }
            }
        }
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            var hasProgressBarShown = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(password: Editable?) {
                password?.let {
                    val text = it.toString()

                    if (text.isEmpty()) {
                        hasProgressBarShown = false
                        binding.viewBackgroundProgression.visibility = View.GONE
                        binding.passwordStrengthBar.visibility = View.GONE
                        binding.tvProgress.visibility = View.GONE
                        binding.tvPasswordError.visibility = View.GONE
                        return
                    }

                    val hasUpper = text.any { ch -> ch.isUpperCase() }
                    val hasDigit = text.any { ch -> ch.isDigit() }
                    val hasSpecial = text.any { ch -> !ch.isLetterOrDigit() }
                    val onlyLetters = text.all { it.isLetter() }
                    val lengthOK = text.length >= 6

                    if (lengthOK || hasProgressBarShown) {
                        hasProgressBarShown = true
                        binding.viewBackgroundProgression.visibility = View.VISIBLE
                        binding.passwordStrengthBar.visibility = View.VISIBLE

                        when {
                            lengthOK && hasUpper && hasSpecial && hasDigit -> {
                                // Strong
                                binding.viewBackgroundProgression.setBackgroundResource(R.drawable.green_bg)
                                setProgress(binding.progressionGuideline, 100)
                                binding.tvProgress.text = "Strong password"
                                binding.tvProgress.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.green
                                    )
                                )
                                binding.passwordStrengthBar.visibility = View.VISIBLE
                                binding.tvProgress.visibility = View.VISIBLE
                                binding.tvPasswordError.visibility = View.GONE
                            }

                            lengthOK && hasUpper && hasSpecial && !hasDigit -> {
                                // Good (Uppercase + Special)
                                binding.viewBackgroundProgression.setBackgroundResource(R.drawable.yellow_bg)
                                setProgress(binding.progressionGuideline, 70)
                                binding.tvProgress.text = "Good password"
                                binding.tvProgress.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.yellow
                                    )
                                )
                                binding.passwordStrengthBar.visibility = View.VISIBLE
                                binding.tvProgress.visibility = View.VISIBLE
                                binding.tvPasswordError.visibility = View.GONE
                            }

                            lengthOK && hasUpper && hasDigit && !hasSpecial -> {

                                // Good (Uppercase + Number)
                                binding.viewBackgroundProgression.setBackgroundResource(R.drawable.yellow_bg)
                                setProgress(binding.progressionGuideline, 70)
                                binding.tvProgress.text = "Good password"
                                binding.tvProgress.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.yellow
                                    )
                                )
                                binding.passwordStrengthBar.visibility = View.VISIBLE
                                binding.tvProgress.visibility = View.VISIBLE
                                binding.tvPasswordError.visibility = View.GONE
                            }

                            lengthOK && onlyLetters && hasUpper -> {
                                //  Fair (Only letters + Uppercase)
                                binding.viewBackgroundProgression.setBackgroundResource(R.drawable.orange_bg)
                                setProgress(binding.progressionGuideline, 50)
                                binding.tvProgress.text = "Fair password"
                                binding.tvProgress.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.orange
                                    )
                                )
                                binding.passwordStrengthBar.visibility = View.VISIBLE

                                binding.tvProgress.visibility = View.VISIBLE
                                binding.tvPasswordError.visibility = View.GONE

                            }

                            !lengthOK -> {
                                binding.tvPasswordError.visibility = View.VISIBLE
                                binding.tvProgress.visibility = View.GONE
                                setProgress(binding.progressionGuideline, 20)
                                binding.viewBackgroundProgression.setBackgroundResource(R.drawable.red_bg)
                            }

                            lengthOK -> {
                                binding.tvPasswordError.visibility = View.VISIBLE
                                binding.tvProgress.visibility = View.GONE

                                setProgress(binding.progressionGuideline, 20)
                                binding.viewBackgroundProgression.setBackgroundResource(R.drawable.red_bg)
                            }


                            else -> {
                                // Weak
                                binding.tvProgress.visibility = View.GONE
                                binding.passwordStrengthBar.visibility = View.GONE
                            }
                        }
                    } else {
                        setProgress(binding.progressionGuideline, 0)
                        binding.tvProgress.visibility = View.GONE
                        binding.tvPasswordError.visibility = View.VISIBLE
                    }
                }
            }


        })
    }

    /** signup using firebase **/
    private fun signUpWithFirebase(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.getIdToken(true)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val idToken = task.result?.token
                        if (user.email?.isNotEmpty() == true && idToken?.isNotEmpty() == true) {
                            callSignUpApi(user.email!!, user.uid, idToken)
                        }
                    } else {
                        Log.e("FirebaseToken", "Error getting token", task.exception)
                    }
                }
            } else {
                hideLoading()
                Log.e("FirebaseSignUp", "Error creating user", task.exception)
            }
        }
    }


    /** google sign in **/
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)

    }


    /** google launcher **/
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    showErrorToast("Google sign-in failed")
                }
            }
        }

     /** get token google and signup api call **/
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser

                user?.getIdToken(true)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val idToken = task.result?.token
                        if (user.email?.isNotEmpty() == true && user.displayName?.isNotEmpty() == true && idToken?.isNotEmpty() == true) {
                            socialApiCall(user.email!!, user.uid, idToken, user.displayName!!)
                        }
                    } else {
                        Log.e("FirebaseToken", "Error getting token", task.exception)
                    }
                }

            } else {
                showErrorToast("Failed to authenticate")
            }
        }
    }

    /** face book login function **/
    private fun facebookLogin() {
        FacebookSdk.sdkInitialize(requireContext())
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        loginManager = LoginManager.getInstance()
        callbackManager = CallbackManager.Factory.create()
        loginManager!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // Fetch user data from Facebook
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                showErrorToast("onCancel")
                Log.e("LoginScreen", "---onCancel")
            }

            override fun onError(error: FacebookException) {
                showErrorToast(error.message)
                Log.e("LoginScreen", "----onError: ${error.message}")
            }
        })

        // Log out the user before starting the login process
        loginManager!!.logOut()

        // Start Facebook login with read permissions
        loginManager!!.logInWithReadPermissions(this, listOf("email", "public_profile"))
    }

    /** get token face book and signup api call **/
    private fun handleFacebookAccessToken(token: AccessToken) {
        val auth = FirebaseAuth.getInstance()
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask  ->
                        if (tokenTask .isSuccessful) {
                            val idToken = tokenTask .result?.token
                            if (user.email?.isNotEmpty() == true && user.displayName?.isNotEmpty() == true && idToken?.isNotEmpty() == true) {
                                socialApiCall(user.email!!, user.uid, idToken, user.displayName!!)
                            }
                        } else {
                            Log.e("FirebaseToken", "Error getting token", task.exception)
                        }
                    }
                } else {
                    showErrorToast("Failed")

                }
            }
    }



   /** set password progress bar ***/
    fun setProgress(guideline: Guideline, percentage: Int?) {
        if (percentage != null && percentage in 0..100) {
            val layoutParams = guideline.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.guidePercent = percentage / 100f
            guideline.layoutParams = layoutParams
        }
    }




    /** social login api call ***/
    private fun socialApiCall(
        accountEmail: String, accountId: String, firebaseTokens: String, name: String
    ) {
        val data = HashMap<String, Any>()
        data["name"] = name
        data["email"] = accountEmail
        data["uid"] = accountId
        data["firebaseToken"] = firebaseTokens
        data["deviceToken"] = token
        data["loginType"] = LoginType.GMAIL.value
        data["deviceType"] = DeviceType.ANDROID.value
        data["profileRole"] = Constants.userType

        if (BindingUtils.latitude != null) {
            data["latitude"] = BindingUtils.latitude!!
        }
        if (BindingUtils.longitude != null) {
            data["longitude"] = BindingUtils.longitude!!
        }
        data["timezone"] = getCurrentTimeZoneIdentifier()

        if (accountEmail.isNotEmpty()) {
            viewModel.socialLogin(Constants.userLanguage, data, Constants.USER_LOGIN)
        }
    }

    /**  signup api call  **/
    private fun callSignUpApi(email: String, userId: String, firebaseToken: String) {
        try {
            val data = HashMap<String, Any>()
            data["name"] = binding.etFullName.text.toString().trim()
            data["email"] = email
            data["uid"] = userId
            data["firebaseToken"] = firebaseToken
            data["deviceToken"] = token
            data["loginType"] = LoginType.MANUAL.value
            data["deviceType"] = DeviceType.ANDROID.value
            data["profileRole"] = Constants.userType
            if (BindingUtils.latitude != null) {
                data["latitude"] = BindingUtils.latitude!!
            }
            if (BindingUtils.longitude != null) {
                data["longitude"] = BindingUtils.longitude!!
            }
            data["timezone"] = getCurrentTimeZoneIdentifier()

            viewModel.createAccount(Constants.userLanguage, data, Constants.USER_LOGIN)
        } catch (e: Exception) {
            Log.e("error", "callSignUpApi: $e")
        }


    }

    /** get current time zone ***/
    private fun getCurrentTimeZoneIdentifier(): String {
        val timeZone = TimeZone.getDefault()
        val timeZoneId = timeZone.id

        return if (timeZoneId == "Asia/Calcutta") {
            "Asia/Kolkata"
        } else {
            timeZoneId
        }
    }

    /****** api response observer ***/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "createAccount" -> {
                            try {
                                val myDataModel: LoginResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        sharedPrefManager.setLoginData(myDataModel.data)
                                        sharedPrefManager.saveToken(myDataModel.data.token.toString())
                                        sharedPrefManager.saveRole(myDataModel.data.profileRole)
                                        val email = binding.etEmail.text.toString().trim()
                                        val bundle = Bundle().apply {
                                            putString("userEmail", email)
                                        }
                                        BindingUtils.navigateWithSlide(
                                            findNavController(),
                                            R.id.navigateToVerifyFragment,
                                            bundle
                                        )
                                    }

                                }

                            } catch (e: Exception) {
                                Log.e("error", "callSignUpApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "socialLogin" -> {
                            try {
                                val myDataModel: LoginResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if (myDataModel.data != null) {
                                        sharedPrefManager.setLoginData(myDataModel.data)
                                        sharedPrefManager.saveToken(myDataModel.data.token.toString())
                                        sharedPrefManager.saveRole(myDataModel.data.profileRole)
                                        if (Constants.userType == 3) {
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

                                }

                            } catch (e: Exception) {
                                Log.e("error", "callSignUpApi: $e")
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
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val fullName = binding.etFullName.text.toString().trim()

        if (fullName.isEmpty()) {
            showInfoToast("Please enter name")
            return false
        } else if (email.isEmpty()) {
            showInfoToast("Please enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showInfoToast("Please enter a valid email")
            return false
        } else if (password.isEmpty()) {
            return false
        } else if (password.length < 6) {
            return false
        } else if (!password.any { it.isUpperCase() }) {
            return false
        } else if (confirmPassword.isEmpty()) {
            showInfoToast("Please enter confirm password")
            return false
        } else if (password != confirmPassword) {
            showInfoToast("Confirm password does not match password")
            return false
        }

        return true
    }


}

