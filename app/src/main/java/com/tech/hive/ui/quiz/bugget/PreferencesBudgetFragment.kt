package com.tech.hive.ui.quiz.bugget

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.FragmentPreferencesBudgetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreferencesBudgetFragment : BaseFragment<FragmentPreferencesBudgetBinding>() {
    private val viewModel: PreferencesBudgetFragmentVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_preferences_budget
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // status bar color change
        BindingUtils.statusBarTextColor(requireActivity(), true)
        // set state
        binding.roomMateType = ""
        binding.locationType = ""
        binding.budgetType = ""

        // click
        initClick()
    }

    /***  click handel event ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // btnDone button click
                R.id.btnDone -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateToQuizCompleteFragment, null
                    )
                }

            }
        }


        // etLocation
        binding.etLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.locationType = p0.toString()
            }

        })
        // etBudget
        binding.etBudget.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.budgetType = p0.toString()
            }

        })
        // etPet
        binding.etGender.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.roomMateType = p0.toString()
            }

        })
    }

}