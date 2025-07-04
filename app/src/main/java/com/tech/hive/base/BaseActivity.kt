package com.tech.hive.base

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.tech.hive.App
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.connectivity.ConnectivityProvider
import com.tech.hive.base.local.SharedPrefManager
import com.tech.hive.base.network.ErrorCodes
import com.tech.hive.base.network.NetworkError
import com.tech.hive.base.utils.AlertManager
import com.tech.hive.base.utils.event.NoInternetSheet
import com.tech.hive.base.utils.hideKeyboard
import com.tech.hive.databinding.ViewProgressSheetBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity(),
    ConnectivityProvider.ConnectivityStateListener {
    lateinit var progressDialogAvl: ProgressDialogAvl
    private var progressSheet: ProgressSheet? = null
    open val onRetry: (() -> Unit)? = null


    var onStartCount = 0
    lateinit var binding: Binding
    val app: App
        get() = application as App

    private lateinit var connectivityProvider: ConnectivityProvider
    private var noInternetSheet: NoInternetSheet? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.setContentView(this, layout)
        binding.setVariable(BR.vm, getViewModel())
        connectivityProvider = ConnectivityProvider.createProvider(this)
        connectivityProvider.addListener(this)
        progressDialogAvl = ProgressDialogAvl(this)
        onCreateView()
    }

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView()


    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }


    fun hideLoading() {
        progressDialogAvl.isLoading(false)
    }

    fun showLoading() {
        progressDialogAvl.isLoading(true)
    }
    fun onError(error: Throwable, showErrorView: Boolean) {
        if (error is NetworkError) {

            when (error.errorCode) {
                ErrorCodes.SESSION_EXPIRED -> {
                    showToast(getString(R.string.session_expired))
                    app.onLogout()
                }

                else -> AlertManager.showNegativeAlert(
                    this, error.message, getString(R.string.alert)
                )
            }
        } else {
            AlertManager.showNegativeAlert(
                this, getString(R.string.please_try_again), getString(R.string.alert)
            )
        }
    }

    override fun onDestroy() {

        connectivityProvider.removeListener(this)
        super.onDestroy()
    }


    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        if (supportFragmentManager.isStateSaved) {
            return
        }
        if (noInternetSheet == null) {
            noInternetSheet = NoInternetSheet()
            noInternetSheet?.isCancelable = false
        }
        if (state.hasInternet()) {
            if (noInternetSheet?.isVisible == true) noInternetSheet?.dismiss()
        } else {
            if (noInternetSheet?.isVisible == false) noInternetSheet?.show(
                supportFragmentManager, noInternetSheet?.tag
            )
        }
    }

    private fun ConnectivityProvider.NetworkState.hasInternet(): Boolean {
        return (this as? ConnectivityProvider.NetworkState.ConnectedState)?.hasInternet == true
    }

    fun textToRequestBody(text: String?): RequestBody {
        return text!!.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun multipartImageBody(image: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            "profile",
//            "profileImage",
            image.name, image.asRequestBody("image/png".toMediaTypeOrNull())
        )
    }

    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(InputMethodManager::class.java)
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }


    fun showToast(msg: String? = "Something went wrong !!") {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = msg ?: "Showed null value !!"

        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
        toast.show()
    }


    fun showInfoToast(msg: String? = "Something went wrong !!") {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_info_toast, null)
        val textView: TextView = layout.findViewById(R.id.toastText)
        textView.text = msg ?: "Showed null value !!"

        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
        toast.show()
    }


}