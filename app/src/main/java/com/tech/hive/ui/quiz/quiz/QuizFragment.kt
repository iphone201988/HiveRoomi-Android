package com.tech.hive.ui.quiz.quiz

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.FragmentQuizBinding
import com.tech.hive.ui.quiz.QuizActivityVM
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuizFragment : BaseFragment<FragmentQuizBinding>() {
    private val viewModel: QuizActivityVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_quiz
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // status bar color change
        BindingUtils.statusBarTextColor(requireActivity())
        // click
        initClick()
    }


    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                //  button click
                R.id.btnStartQuiz -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.fragmentQuizQuestion, null
                    )
                }

            }
        }
    }
}

