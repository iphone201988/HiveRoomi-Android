package com.tech.hive.ui.quiz.question

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.AnswerSendRequest
import com.tech.hive.data.model.QuestionData
import com.tech.hive.data.model.QuizAnswerResponse
import com.tech.hive.data.model.QuizQuestionResponse
import com.tech.hive.data.model.UserQuizAnswer
import com.tech.hive.databinding.FragmentQuizQuestionBinding
import com.tech.hive.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizQuestionFragment : BaseFragment<FragmentQuizQuestionBinding>() {
    private val viewModel: QuizQuestionFragmentVM by viewModels()
    private lateinit var questionAdapter: QuestionHeaderAdapter
    private var currentIndex = 0
    private var allQuestions = listOf<QuestionData>()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_quiz_question
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // view
        initView()
        // observer
        initObserver()
        // api call
        viewModel.getQuizQuestion(Constants.GET_QUIZ)
    }


    /** handle view **/
    private fun initView() {
        // status bar color change
        BindingUtils.statusBarTextColor(requireActivity(), true)
        // click
        initCLick()
        // set percentage
        setProgress(binding.progressionGuideline, 30)


    }


    /** api response observer **/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "quizAnswerApi" -> {
                            try {
                                val myDataModel: QuizAnswerResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    sharedPrefManager.saveQuiz(true)
                                    findNavController().navigate(
                                        R.id.navigateToQuizCompleteFragment,
                                        null,
                                        NavOptions.Builder()
                                            .setPopUpTo(R.id.navigateToQuizCompleteFragment, true)
                                            .build()
                                    )

                                }

                            } catch (e: Exception) {
                                Log.e("error", "quizAnswerApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "getQuizQuestion" -> {
                            try {
                                val myDataModel: QuizQuestionResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    allQuestions = myDataModel.data as List<QuestionData>
                                    questionAdapter = QuestionHeaderAdapter(allQuestions.getOrNull(currentIndex))
                                    binding.rvQuestion.adapter = questionAdapter

                                }

                            } catch (e: Exception) {
                                Log.e("error", "getQuizQuestion: $e")
                            } finally {
                                hideLoading()
                            }
                        }


                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {

                }
            }
        }
    }


    /*** click event handel ***/
    private fun initCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) setOnClickListener@{
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }

                R.id.tcCompleteLater -> {
                    val intent = Intent(requireContext(), DashboardActivity::class.java)
                    startActivity(intent)
                    requireActivity().finishAffinity()


                }

                // next button click
                R.id.btnNext -> {
                    val currentQuestion = allQuestions[currentIndex]
                    if (!isAllAnswersSelected(currentQuestion)) {
                        Toast.makeText(
                            requireContext(), "Please select all answers", Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    buildUserAnswers(currentQuestion)
                   // setProgress(binding.progressionGuideline, 30)
                    if (currentIndex < allQuestions.size - 1) {
                        currentIndex++
                        questionAdapter.updateQuestion(allQuestions[currentIndex])
                        when(currentIndex){
                            1 ->{
                                setProgress(binding.progressionGuideline, 70)
                            }
                            2 ->{
                                setProgress(binding.progressionGuideline, 90)
                            }
                            3->{
                                setProgress(binding.progressionGuideline, 100)
                            }
                        }
                        binding.rvQuestion.post {
                            (binding.rvQuestion.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
                        }
                    } else {
                      var selectedData =  allQuestions.flatMap { buildUserAnswers(it) }
                        var sendRequestData = AnswerSendRequest(selectedData)
                          viewModel.quizAnswerApi(
                         Constants.userLanguage, Constants.QUIZ_ANSWER, request = sendRequestData)

                    }
                }

            }
        }
    }


    fun buildUserAnswers(questionData: QuestionData): List<UserQuizAnswer> {
        val result = mutableListOf<UserQuizAnswer>()

        questionData.quiz?.forEach { quiz ->
            quiz?.groupValue?.forEach { groupValue ->
                val quizId = groupValue?._id ?: return@forEach

                when (groupValue.type) {
                    "option" -> {
                        val selectedOption =
                            groupValue.options?.firstOrNull { it?.selectedAnswer == true }
                        selectedOption?.value?.let { value ->
                            result.add(UserQuizAnswer(quizId, value))
                        }
                    }

                    "text" -> {
                        val enteredValue = groupValue.options?.firstOrNull()?.inputValue
                        if (!enteredValue.isNullOrBlank()) {
                            result.add(UserQuizAnswer(quizId, enteredValue))
                        }
                    }
                }
            }
        }

        return result
    }

    fun isAllAnswersSelected(questionData: QuestionData): Boolean {
        questionData.quiz?.forEach { quiz ->
            quiz?.groupValue?.forEach { groupValue ->
                val hasSelected = when (groupValue?.type) {
                    "option" -> groupValue.options?.any { it?.selectedAnswer == true } == true
                    "text" -> groupValue.options?.any { !it?.inputValue.isNullOrBlank() } == true
                    else -> false
                }
                if (!hasSelected) return false
            }
        }
        return true
    }


    private fun setProgress(guideline: Guideline, percentage: Int?) {
        if (percentage != null && percentage in 0..100) {
            val layoutParams = guideline.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.guidePercent = percentage / 100f
            guideline.layoutParams = layoutParams
        }
    }


}