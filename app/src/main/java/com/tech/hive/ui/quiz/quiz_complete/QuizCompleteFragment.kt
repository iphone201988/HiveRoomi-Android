package com.tech.hive.ui.quiz.quiz_complete

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.FragmentQuizCompleteBinding
import com.tech.hive.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizCompleteFragment : BaseFragment<FragmentQuizCompleteBinding>() {
    private val viewModel: QuizCompleteFragmentVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_quiz_complete
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // status bar color change
        BindingUtils.statusBarTextColor(requireActivity(), true)
        // click
        initClick()
    }

    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // btnVerify button click
                R.id.btnViewMatches -> {
                    val intent = Intent(requireActivity(), DashboardActivity::class.java)
                    startActivity(intent)
                    requireActivity().finishAffinity()
                }
            }
        }
    }


}