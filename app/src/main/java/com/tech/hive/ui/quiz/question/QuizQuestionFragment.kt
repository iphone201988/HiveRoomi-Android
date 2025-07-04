package com.tech.hive.ui.quiz.question

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.Answer
import com.tech.hive.data.model.QuestionModel
import com.tech.hive.databinding.FragmentQuizQuestionBinding
import com.tech.hive.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizQuestionFragment : BaseFragment<FragmentQuizQuestionBinding>() {
    private val viewModel: QuizQuestionFragmentVM by viewModels()

    private lateinit var fullQuestionList: List<QuestionModel>
    private lateinit var questionAdapter: QuestionAdapter
    private val questionsPerPage = listOf(4, 3, 4, 1)
    private var currentPageIndex = 0

    override fun getLayoutResource(): Int {
        return R.layout.fragment_quiz_question
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // view
        initView()
    }


    /** handle view **/
    private fun initView() {
        // status bar color change
        BindingUtils.statusBarTextColor(requireActivity(), true)
        // click
        initCLick()
        fullQuestionList = getQuestionSecondList()
        loadNextPage()
        setProgress(binding.progressionGuideline, 30)
        binding.tvSelectLanguage.text = "Essential Filters"
        binding.tvChoose.text = "We'll find the right homes for you right away"

    }


    private fun loadNextPage() {
        if (currentPageIndex >= questionsPerPage.size) {
            binding.btnNext.text = "Continue"
            findNavController().navigate(
                R.id.navigateToQuizCompleteFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.navigateToQuizCompleteFragment, true).build()
            )
            return
        }

        val start = questionsPerPage.take(currentPageIndex).sum()
        val count = questionsPerPage[currentPageIndex]
        val end = minOf(start + count, fullQuestionList.size)

        val currentQuestions = fullQuestionList.subList(start, end)
        questionAdapter = QuestionAdapter(currentQuestions)
        binding.rvQuestion.adapter = questionAdapter


    }


    /*** click event handel ***/
    private fun initCLick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
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
                    val start = questionsPerPage.take(currentPageIndex).sum()
                    val count = questionsPerPage[currentPageIndex]
                    val end = minOf(start + count, fullQuestionList.size)

                    val currentQuestions = fullQuestionList.subList(start, end)

                    val allSelected = currentQuestions.all { question ->
                        if (question.type == 2) {
                            question.answer1.any { it.selectedAnswer }
                        } else {
                            question.answer.any { it.selectedAnswer }
                        }
                    }

                    if (!allSelected) {
                        Toast.makeText(
                            requireContext(),
                            "Please select answer for all questions",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@observe
                    }


                    currentPageIndex++
                    loadNextPage()
                    when (currentPageIndex) {
                        1 -> {
                            binding.tcCompleteLater.visibility=View.GONE
                            setProgress(binding.progressionGuideline, 80)
                            binding.tvSelectLanguage.text = "Compatibility"
                            binding.tvChoose.text = "For peaceful cohabitation"
                        }

                        2 -> {
                            setProgress(binding.progressionGuideline, 90)
                            binding.tvSelectLanguage.text = "Deal Breakers"
                            binding.tvChoose.text = "Quick Yes/No questions for compatibility"
                        }

                        3 -> {
                            setProgress(binding.progressionGuideline, 100)
                            binding.tvChoose.text = "Your Worst Housing \n" + "Nightmare?"
                            binding.tvChoose.text = "Choose ONE (this will be your main filter)"
                        }
                    }
                }
            }
        }
    }


    private fun setProgress(guideline: Guideline, percentage: Int?) {
        if (percentage != null && percentage in 0..100) {
            val layoutParams = guideline.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.guidePercent = percentage / 100f
            guideline.layoutParams = layoutParams
        }
    }


    // question List
    private fun getQuestionSecondList(): ArrayList<QuestionModel> {
        val list = ArrayList<QuestionModel>()
        list.add(
            QuestionModel(
                heading = "Question 1",
                question = "When do you need it?",
                heading1 = "",
                question1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, "\uD83D\uDE80 Right away (within 7 days)"),
                    Answer(1, "\uD83D\uDCC5 In 2-4 weeks"),
                    Answer(1, "\uD83D\uDCC6 In 1-2 months"),
                    Answer(1, "\uD83D\uDDD3\uFE0F In more than 2 months")
                ),
                answer1 = listOf(
                    Answer(1, "\uD83D\uDE80 Right away (within 7 days)"),
                    Answer(1, "\uD83D\uDCC5 In 2-4 weeks"),
                    Answer(1, "\uD83D\uDCC6 In 1-2 months"),
                    Answer(1, "\uD83D\uDDD3\uFE0F In more than 2 months")
                ),
            )
        )
        list.add(
            QuestionModel(
                heading = "Question 2",
                question = "Monthly budget (all included)?",
                heading1 = "",
                question1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, "\uD83D\uDCB0 €200-400 "),
                    Answer(1, "\uD83D\uDCB5 €400-600"),
                    Answer(1, "\uD83D\uDCB4 €600-800"),
                    Answer(1, "\uD83D\uDCB6 €800+")
                ),
                answer1 = listOf(
                    Answer(1, "\uD83D\uDE80 Right away (within 7 days)"),
                    Answer(1, "\uD83D\uDCC5 In 2-4 weeks"),
                    Answer(1, "\uD83D\uDCC6 In 1-2 months"),
                    Answer(1, "\uD83D\uDDD3\uFE0F In more than 2 months")
                ),
            )
        )
        list.add(
            QuestionModel(
                heading = "Question 3",
                question = "Preferred area",
                heading1 = "",
                question1 = "",
                type = 1,
                answer = listOf(Answer(2, "Enter location")),
                answer1 = listOf(Answer(2, "\uD83D\uDE80 Right away (within 7 days)")),
            )
        )
        list.add(
            QuestionModel(
                heading = "Question 4",
                question = "What are you looking for?",
                heading1 = "",
                question1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, "\uD83D\uDECF\uFE0F Single room"),
                    Answer(1, "\uD83D\uDC65 Double room"),
                    Answer(1, "\uD83C\uDFE0 Studio"),
                    Answer(1, "\uD83C\uDFD8\uFE0F Entire apartment to share"),
                    Answer(1, "\uD83E\uDD37 I'm flexible")
                ),
                answer1 = listOf(
                    Answer(1, "\uD83D\uDE80 Right away (within 7 days)"),
                    Answer(1, "\uD83D\uDCC5 In 2-4 weeks"),
                    Answer(1, "\uD83D\uDCC6 In 1-2 months"),
                    Answer(1, "\uD83D\uDDD3\uFE0F In more than 2 months")
                ),
            )
        )
        list.add(
            QuestionModel(
                heading = "Question 5",
                question = "Your lifestyle",
                heading1 = "",
                question1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, "\uD83C\uDF05 Early Bird - Up at 7, in bed at 23"),
                    Answer(1, "⚖\uFE0F Balanced - Standard rhythms (8-24)"),
                    Answer(1, "\uD83E\uDD89 Night Owl - I live at night"),
                    Answer(1, "\uD83C\uDFB2 Random - Unpredictable hours")
                ),
                answer1 = listOf(
                    Answer(1, "\uD83D\uDE80 Right away (within 7 days)"),
                    Answer(1, "\uD83D\uDCC5 In 2-4 weeks"),
                    Answer(1, "\uD83D\uDCC6 In 1-2 months"),
                    Answer(1, "\uD83D\uDDD3\uFE0F In more than 2 months")
                ),
            )
        )
        list.add(
            QuestionModel(
                heading = "Question 6",
                question = "Cleaning common areas",
                heading1 = "",
                question1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, "✨ 5/5 - Everything is always perfect"),
                    Answer(1, "\uD83E\uDDF9 4/5 - Clean and tidy"),
                    Answer(1, "\uD83D\uDC4C 3/5 - Normal, not obsessive"),
                    Answer(1, "\uD83D\uDE0C 2/5 - Relax, I'm not obsessed"),
                    Answer(1, "\uD83E\uDD37 1/5 - Chaos doesn't bother me")
                ),
                answer1 = listOf(
                    Answer(1, "\uD83D\uDE80 Right away (within 7 days)"),
                    Answer(1, "\uD83D\uDCC5 In 2-4 weeks"),
                    Answer(1, "\uD83D\uDCC6 In 1-2 months"),
                    Answer(1, "\uD83D\uDDD3\uFE0F In more than 2 months")
                ),
            )
        )
        list.add(
            QuestionModel(
                heading = "Question 7",
                question = "Socializing at home",
                heading1 = "",
                question1 = "",
                type = 1,
                answer = listOf(
                    Answer(
                        1,
                        "\uD83C\uDF89 Social Butterfly - I love having \n" + "dinners and evenings together"
                    ), Answer(
                        1, "☕ Friendly - Chatting yes, but with \n" + "my space"
                    ), Answer(
                        1, "\uD83E\uDD10 Professional - Friendly but each \n" + "for themselves"
                    ), Answer(1, "\uD83D\uDEAA Ghost - Home = total privacy")
                ),
                answer1 = listOf(
                    Answer(1, "\uD83D\uDE80 Right away (within 7 days)"),
                    Answer(1, "\uD83D\uDCC5 In 2-4 weeks"),
                    Answer(1, "\uD83D\uDCC6 In 1-2 months"),
                    Answer(1, "\uD83D\uDDD3\uFE0F In more than 2 months")
                ),
            )
        )

        list.add(
            QuestionModel(
                heading = "\uD83D\uDEAC Smoking",
                question = "Do I smoke?",
                heading1 = "",
                question1 = "Do I accept smokers?",
                type = 2,
                answer = listOf(Answer(1, "Yes"), Answer(1, "No")),
                answer1 = listOf(Answer(1, "Yes"), Answer(1, "No"), Answer(1, "Outside Only"))
            )
        )
        list.add(
            QuestionModel(
                heading = "\uD83D\uDC3E Animals",
                question = "Do I have animals?",
                heading1 = "",
                question1 = "Do I accept them?",
                type = 2,
                answer = listOf(Answer(1, "Yes"), Answer(1, "No")),
                answer1 = listOf(Answer(1, "Yes"), Answer(1, "No"), Answer(1, "Depends"))
            )
        )
        list.add(
            QuestionModel(
                heading = "\uD83C\uDFB5 Noise",
                question = "Do I tolerate music/TV?",
                heading1 = "",
                question1 = "",
                type = 1,
                answer = listOf(Answer(1, "Yes"), Answer(1, "No"), Answer(1, "With headphones")),
                answer1 = listOf(Answer(1, "Yes"), Answer(1, "No"), Answer(1, "Outside Only"))
            )
        )
        list.add(
            QuestionModel(
                heading = "\uD83D\uDC65 Guests",
                question = "Overnight guests ok?",
                heading1 = "",
                question1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, "Yes"), Answer(1, "No"), Answer(1, "With advance notice")
                ),
                answer1 = listOf(Answer(1, "Yes"), Answer(1, "No"), Answer(1, "Outside Only"))
            )
        )


        list.add(
            QuestionModel(
                heading = "Question 9",
                question = "Your worst housing nightmare?",
                heading1 = "",
                question1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, "\uD83D\uDE24 Dirty/messy roommates"),
                    Answer(1, "\uD83C\uDFB8 Too much noise/parties"),
                    Answer(1, "\uD83D\uDEAD Smell of smoke in the house"),
                    Answer(1, "\uD83D\uDC15 Unmanaged pets"),
                    Answer(1, "\uD83D\uDCB8 Hidden costs/crazy bills"),
                    Answer(1, "\uD83D\uDC7B No privacy/too intrusive"),
                    Answer(1, "❄\uFE0F Cold house/structural problems"),
                    Answer(1, "\uD83D\uDEAB Intrusive owner"),

                    ),
                answer1 = listOf(Answer(1, "Yes"), Answer(1, "No"), Answer(1, "Outside Only"))
            )
        )

        return list
    }

}