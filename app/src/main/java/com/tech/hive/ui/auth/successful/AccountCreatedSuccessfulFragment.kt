package com.tech.hive.ui.auth.successful

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.FragmentAccountCreatedSuccessfulBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountCreatedSuccessfulFragment : BaseFragment<FragmentAccountCreatedSuccessfulBinding>() {
    private val viewModel: AccountSuccessfulVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_account_created_successful
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initClick()

    }

    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // btnContinue button click
                R.id.btnContinue -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToRegulationsFragment, null
                    )
                }

            }
        }

    }


}
