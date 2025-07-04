package com.tech.hive.base

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.local.SharedPrefManager
import com.tech.hive.base.utils.hideKeyboard
import javax.inject.Inject


abstract class BaseFragment<Binding : ViewDataBinding> : Fragment() {
    lateinit var binding: Binding
    @Inject
    lateinit var sharedPrefManager: SharedPrefManager
    val parentActivity: BaseActivity<*>?
        get() = activity as? BaseActivity<*>
    lateinit var progressDialogAvl: ProgressDialogAvl
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateView(view)
        progressDialogAvl = ProgressDialogAvl(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.inflate(layoutInflater, layout,container,false)

        val vm = getViewModel()
        binding.setVariable(BR.vm, vm)
        vm.onUnAuth.observe(viewLifecycleOwner) {
            val activity = requireActivity() as BaseActivity<*>
         //   activity.showUnauthorised()
        }
        return binding.root
    }

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView(view: View)
    override fun onPause() {
        super.onPause()
        activity?.hideKeyboard()
    }
    fun hideLoading() {
        progressDialogAvl.isLoading(false)

    }

    fun showLoading() {
        progressDialogAvl.isLoading(true)
    }


    fun showErrorToast(msg: String? = "Something went wrong !!") {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = msg ?: "Showed null value !!"

        val toast = Toast(parentActivity)
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

        val toast = Toast(parentActivity)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
        toast.show()
    }


}