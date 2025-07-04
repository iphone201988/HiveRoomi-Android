package com.tech.hive.ui.auth.boarding_type

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.api.Constants
import com.tech.hive.databinding.FragmentOnBoardingTypeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnBoardingTypeFragment : BaseFragment<FragmentOnBoardingTypeBinding>() {
    private val viewModel: OnBoardingTypeFragmentVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_on_boarding_type
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // select type
        Constants.userType = 0
        binding.selectType = 0
        // click
        initClick()
    }

    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // Roommate button click
                R.id.clRoommate -> {
                    binding.selectType = 1
                    Constants.userType = 1
                }
                // Home button click
                R.id.clHome -> {
                    binding.selectType = 2
                    Constants.userType = 2
                }
                // Offering button click
                R.id.clOffering -> {
                    binding.selectType = 3
                    Constants.userType = 3
                }
                // btnContinue button click
                R.id.btnContinue -> {
                    if (validate()) {

                        BindingUtils.navigateWithSlide(
                            findNavController(), R.id.navigateToLoginFragment, null
                        )
                    }

                }
            }
        }
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        if (Constants.userType == 0) {
            showInfoToast("Please select any role")
            return false
        }
        return true
    }

}