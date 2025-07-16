package com.tech.hive.ui.auth.regulations

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.data.api.Constants
import com.tech.hive.databinding.FragmentRegulationsBinding
import com.tech.hive.ui.auth.AuthActivityVM
import com.tech.hive.ui.dashboard.DashboardActivity
import com.tech.hive.ui.quiz.QuizActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegulationsFragment : BaseFragment<FragmentRegulationsBinding>() {
    private val viewModel: AuthActivityVM by viewModels()
    override fun onCreateView(view: View) {
        // view
        initView()
        // click
        initOnClick()
        // observer
        initObserver()
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_regulations
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    /** handle view **/
    private fun initView() {

    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
             when(it?.id){
                 R.id.btnAccept->{
                     val data = sharedPrefManager.getRole()
                     when (data) {
                         1 -> {
                             val intent = Intent(requireActivity(), QuizActivity::class.java)
                             startActivity(intent)
                             requireActivity().finishAffinity()
                         }
                         3 -> {
                             val intent = Intent(requireActivity(), DashboardActivity::class.java)
                             startActivity(intent)
                             requireActivity().finishAffinity()
                         }
                         else -> {
                             val intent = Intent(requireActivity(), QuizActivity::class.java)
                             startActivity(intent)
                             requireActivity().finishAffinity()
                         }
                     }
                 }
             }
        }

    }

    /** handle api response **/
    private fun initObserver() {

    }

}