package com.tech.hive.ui.auth.login

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
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
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.LoginResponse
import com.tech.hive.databinding.FragmentLoginBinding
import com.tech.hive.ui.dashboard.DashboardActivity
import com.tech.hive.ui.enum_class.DeviceType
import com.tech.hive.ui.enum_class.LoginType
import com.tech.hive.ui.quiz.QuizActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.TimeZone

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: LoginFragmentVM by viewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var token = ""
    private var loginManager: LoginManager? = null
    private var callbackManager: CallbackManager? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // check state
        binding.emailType = ""
        binding.passwordType = ""
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

                // Signup button click
                R.id.tvSignup -> {
                    BindingUtils.preventMultipleClick(it)
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToSignUpFragment, null
                    )

                }
                // forgot button click
                R.id.tvForgot -> {
                    BindingUtils.preventMultipleClick(it)
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToChangePasswordFragment, null
                    )
                }
                // continue button click
                R.id.btnContinue -> {
                    BindingUtils.preventMultipleClick(it)
                    if (validate()) {
                        lifecycleScope.launch {
                            showLoading()
                            val userEmail = binding.etEmail.text.toString().trim()
                            val userPassword = binding.etPassword.text.toString().trim()
                            loginFirebase(userEmail, userPassword)
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

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is visible
                Log.d("Keyboard", "Visible")
            } else {
                binding.passwordType = ""
                binding.emailType = ""
                binding.etEmail.clearFocus()
                binding.etPassword.clearFocus()
            }
        }


    }


    /** login  using firebase **/
    private fun loginFirebase(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.getIdToken(true)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val idToken = task.result?.token
                            if (user.email?.isNotEmpty() == true && idToken?.isNotEmpty() == true) {
                                callLoginApi(user.email!!, user.uid, idToken)
                            }
                        } else {
                            Log.e("FirebaseToken", "Error getting token", task.exception)
                        }
                    }
                } else {
                    hideLoading()
                    showErrorToast("Invalid credentials")
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
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val idToken = tokenTask.result?.token
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


    /***** login api call ***/
    private fun callLoginApi(email: String, userId: String, firebaseToken: String) {
        try {
            try {
                val data = HashMap<String, Any>()
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

    /** api response observer **/
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

                                        val bundle = Bundle().apply {
                                            putString("userEmail", myDataModel.data.email)
                                        }

                                        if (myDataModel.data.sendOtp == true) {
                                            BindingUtils.navigateWithSlide(
                                                findNavController(),
                                                R.id.navigateToVerifyFragment,
                                                bundle
                                            )
                                        }
                                        else {
                                            if (myDataModel.data.isProfileComplete == false) {
                                                if (myDataModel.data.profileRole == 3) {
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

                                            else if (myDataModel.data.profileRole == 3) {
                                                val intent = Intent(
                                                    requireContext(), DashboardActivity::class.java
                                                )
                                                startActivity(intent)
                                                requireActivity().finishAffinity()
                                            }

                                            else if (myDataModel.data.isQuizComplete == false || myDataModel.data.totalQuizDone == 0) {
                                                val intent = Intent(
                                                    requireActivity(),
                                                    QuizActivity::class.java
                                                )
                                                startActivity(intent)
                                                requireActivity().finishAffinity()
                                            } else {
                                                val intent = Intent(
                                                    requireContext(), DashboardActivity::class.java
                                                )
                                                startActivity(intent)
                                                requireActivity().finishAffinity()

                                            }
                                        }


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
                                        if (myDataModel.data.isProfileComplete == false) {
                                            if (myDataModel.data.profileRole == 3) {
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

                                        } else if (myDataModel.data.isQuizComplete == false || myDataModel.data.totalQuizDone == 0) {
                                            BindingUtils.navigateWithSlide(
                                                findNavController(),
                                                R.id.navigateToQuizFragment,
                                                null
                                            )
                                        } else {
                                            val intent = Intent(
                                                requireContext(), DashboardActivity::class.java
                                            )
                                            startActivity(intent)
                                            requireActivity().finishAffinity()

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

    /*** hide password **/
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

    /*** add validation ***/
    private fun validate(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        if (email.isEmpty()) {
            showInfoToast("Please enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showInfoToast("Please enter a valid email")
            return false
        } else if (password.isEmpty()) {
            showInfoToast("Please enter password")
            return false
        }
        return true
    }


}
