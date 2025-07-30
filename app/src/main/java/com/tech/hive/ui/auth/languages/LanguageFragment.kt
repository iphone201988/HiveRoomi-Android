package com.tech.hive.ui.auth.languages

import android.content.res.Configuration
import android.view.View
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.api.Constants
import com.tech.hive.databinding.FragmentLanguageBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

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
        val type = sharedPrefManager.getSelectType()
        if (type?.isNotEmpty() == true){
            binding.selectType = type.toInt()
        }

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
                   Constants.userLanguage = "en"
                   setLocale("en")
                   sharedPrefManager.setSelectType("1")
                   sharedPrefManager.setLocaleType("en")

               }
               // italian button click
               R.id.clItalian ->{
                   binding.selectType = 2
                   sharedPrefManager.setSelectType("2")
                   Constants.userLanguage = "it"
                   setLocale("it")
                   sharedPrefManager.setLocaleType("it")

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


    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        // recreate activity to reflect the locale change
        requireActivity().recreate()
    }


}

