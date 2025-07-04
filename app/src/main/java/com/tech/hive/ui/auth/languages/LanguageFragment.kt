package com.tech.hive.ui.auth.languages

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.FragmentLanguageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : BaseFragment<FragmentLanguageBinding>() {
    private val viewModel: LanguageFragmentVM by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.fragment_language
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
      // select type
        binding.selectType = 1
        // click
        initClick()
    }

    /*** click event handel ***/
    private fun initClick() {
       viewModel.onClick.observe(viewLifecycleOwner){
           when(it?.id){
               // back button click
               R.id.ivBack ->{
                requireActivity().onBackPressed()
               }
               // english button click
               R.id.clEnglish ->{
                   binding.selectType = 1
               }
               // italian button click
               R.id.clItalian ->{
                   binding.selectType = 2
               }
               // next button click
               R.id.btnNext ->{
                   BindingUtils.navigateWithSlide(
                       findNavController(), R.id.navigateToOnBoardingFragment, null
                   )
               }
               }
           }
       }
    }

