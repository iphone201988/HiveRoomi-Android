package com.tech.hive.ui.quiz.question

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.Answer
import com.tech.hive.data.model.AnswerSendResponse
import com.tech.hive.data.model.QuestionModel
import com.tech.hive.data.model.QuizAnswerResponse
import com.tech.hive.data.model.UserQuizAnswer
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
//        viewModel.getQuizQuestion(Constants.GET_QUIZ)

        // observer
        initObserver()
    }


    /** handle view **/
    private fun initView() {
        // status bar color change
        BindingUtils.statusBarTextColor(requireActivity(), true)
        // click
        initCLick()
        fullQuestionList = getQuestionSecondList()
        // set first data
        val start = questionsPerPage.take(currentPageIndex).sum()
        val count = questionsPerPage[currentPageIndex]
        val end = minOf(start + count, fullQuestionList.size)

        val currentQuestions = fullQuestionList.subList(start, end)
        questionAdapter = QuestionAdapter(currentQuestions)
        binding.rvQuestion.adapter = questionAdapter
        // set percentage
        setProgress(binding.progressionGuideline, 30)
        binding.tvSelectLanguage.text = getString(R.string.essential_filters)
        binding.tvChoose.text = getString(R.string.we_ll_find_the_right_homes_for_you_right_away)

    }


    private fun loadNextPage(answerSendResponse: AnswerSendResponse) {
        if (currentPageIndex >= questionsPerPage.size) {
            binding.btnNext.text = getString(R.string.continues)


            viewModel.quizAnswerApi(
                Constants.userLanguage, Constants.QUIZ_ANSWER, request = answerSendResponse
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
                                    findNavController().navigate(
                                        R.id.navigateToQuizCompleteFragment,
                                        null,
                                        NavOptions.Builder()
                                            .setPopUpTo(R.id.navigateToQuizCompleteFragment, true)
                                            .build()
                                    )

                                }

                            } catch (e: Exception) {
                                Log.e("error", "callSignUpApi: $e")
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

    val selectedAnswers = mutableListOf<UserQuizAnswer>()

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
                            question.answer1.any { it.selectedAnswer } || question.answer.any { it.selectedAnswer }
                        } else {
                            question.answer.any { it.selectedAnswer }
                        }
                    }

                    if (!allSelected) {
                        showInfoToast("Please select answer for all questions")
                        return@observe
                    }



                    currentQuestions.forEach { question ->
                        if (question.type == 2) {
                            // Collect from answer1 (type 2)
                            question.answer1.filter { it.selectedAnswer }.forEach { selected ->
                                selectedAnswers.add(
                                    UserQuizAnswer(
                                        answer = selected.value, quizId = question.id1
                                    )
                                )
                            }

                            // Collect from answer (normal)
                            question.answer.filter { it.selectedAnswer }.forEach { selected ->
                                selectedAnswers.add(
                                    UserQuizAnswer(
                                        answer = selected.value, quizId = question.id
                                    )
                                )
                            }
                        } else {
                            // Normal collection for other types
                            question.answer.filter { it.selectedAnswer }.forEach { selected ->
                                selectedAnswers.add(
                                    UserQuizAnswer(
                                        answer = selected.value, quizId = question.id
                                    )
                                )
                            }
                        }
                    }

                    val answerSendResponse = AnswerSendResponse(userQuizAnswer = selectedAnswers)

                    Log.d("fdgfggdf", "initCLick: $answerSendResponse")

                    currentPageIndex++
                    loadNextPage(answerSendResponse)

                    when (currentPageIndex) {
                        1 -> {
                            binding.tcCompleteLater.visibility = View.GONE
                            setProgress(binding.progressionGuideline, 80)
                            binding.tvSelectLanguage.text = getString(R.string.compatibility)
                            binding.tvChoose.text = getString(R.string.for_peaceful_cohabitation)
                        }

                        2 -> {
                            setProgress(binding.progressionGuideline, 90)
                            binding.tvSelectLanguage.text = getString(R.string.deal_breakers)
                            binding.tvChoose.text =
                                getString(R.string.quick_yes_no_questions_for_compatibility)
                        }

                        3 -> {
                            setProgress(binding.progressionGuideline, 100)
                            binding.tvSelectLanguage.text = getString(R.string.your_worst_housing)
                            binding.tvChoose.text =
                                getString(R.string.choose_one_this_will_be_your_main_filter)
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
                id = "6846d912fad0b51247b99578",
                heading = getString(R.string.question_1),
                question = getString(R.string.when_do_you_need_it),
                heading1 = "",
                question1 = "",
                id1 = "",
                type = 1,
                answer = listOf(
                    Answer(
                        1,
                        getString(R.string.right_away_within_7_days),
                        getString(R.string.within_7_days)
                    ),
                    Answer(1, getString(R.string.in_2_4_weeks), getString(R.string._2_4_weeks)),
                    Answer(1, getString(R.string.in_1_2_months), getString(R.string._1_2_months)),
                    Answer(
                        1,
                        getString(R.string.in_more_than_2_months),
                        getString(R.string.more_than_2_months)
                    )
                ),

                answer1 = listOf(
                    Answer(
                        1,
                        getString(R.string.right_away_within_7_days),
                        getString(R.string.within_7_days)
                    ),
                    Answer(1, getString(R.string.in_2_4_weeks), getString(R.string._2_4_weeks)),
                    Answer(1, getString(R.string.in_1_2_months), getString(R.string._1_2_months)),
                    Answer(
                        1,
                        getString(R.string.in_more_than_2_months),
                        getString(R.string.more_than_2_months)
                    )
                ),


                )
        )
        list.add(
            QuestionModel(
                id = "6846d950fad0b51247b99580",
                heading = getString(R.string.question_2),
                question = getString(R.string.monthly_budget_all_included),
                heading1 = "",
                question1 = "",
                id1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, getString(R.string._200_400), getString(R.string._200_400_only)),
                    Answer(1, getString(R.string._400_600), getString(R.string._400_600_only)),
                    Answer(1, getString(R.string._600_800), getString(R.string._600_800_only)),
                    Answer(1, getString(R.string._800), getString(R.string._800_plus))
                ),
                answer1 = listOf(
                    Answer(1, getString(R.string._200_400), getString(R.string._200_400_only)),
                    Answer(1, getString(R.string._400_600), getString(R.string._400_600_only)),
                    Answer(1, getString(R.string._600_800), getString(R.string._600_800_only)),
                    Answer(1, getString(R.string._800), getString(R.string._800_plus))
                ),
            )
        )
        list.add(
            QuestionModel(
                id = "6846da14bb77f53a3b10651f",
                heading = getString(R.string.question_3),
                question = getString(R.string.preferred_area),
                heading1 = "",
                question1 = "",
                id1 = "",
                type = 1,
                answer = listOf(
                    Answer(
                        2, getString(R.string.enter_location), getString(R.string.enter_location)
                    )
                ),
                answer1 = listOf(
                    Answer(
                        2, getString(R.string.enter_location), getString(R.string.enter_location)
                    )
                ),
            )
        )
        list.add(
            QuestionModel(
                id = "6846da3cbb77f53a3b106524",
                heading = "Question 4",
                question = "What are you looking for?",
                heading1 = "",
                question1 = "",
                id1 = "",
                type = 1,
                answer = listOf(
                    Answer(
                        1, getString(R.string.single_room), getString(R.string.single_room_small)
                    ),
                    Answer(
                        1, getString(R.string.double_room), getString(R.string.double_room_small)
                    ),
                    Answer(1, getString(R.string.studio1), getString(R.string.studio_small)),
                    Answer(
                        1,
                        getString(R.string.entire_apartment_to_share),
                        getString(R.string.entire_apartment_small)
                    ),
                    Answer(1, getString(R.string.i_m_flexible), getString(R.string.flexible_small))
                ),
                answer1 = listOf(
                    Answer(
                        1, getString(R.string.single_room), getString(R.string.single_room_small)
                    ),
                    Answer(
                        1, getString(R.string.double_room), getString(R.string.double_room_small)
                    ),
                    Answer(1, getString(R.string.studio1), getString(R.string.studio_small)),
                    Answer(
                        1,
                        getString(R.string.entire_apartment_to_share),
                        getString(R.string.entire_apartment_small)
                    ),
                    Answer(1, getString(R.string.i_m_flexible), getString(R.string.flexible_small))
                ),
            )
        )
        list.add(
            QuestionModel(
                id = "6846db39bb77f53a3b106531",
                heading = getString(R.string.question_5),
                question = getString(R.string.your_lifestyle),
                heading1 = "",
                question1 = "",
                id1 = "",
                type = 1,
                answer = listOf(
                    Answer(
                        1,
                        getString(R.string.early_bird_up_at_7_in_bed_at_23),
                        getString(R.string.early_bird)
                    ), Answer(
                        1,
                        getString(R.string.balanced_standard_rhythms_8_24),
                        getString(R.string.balanced)
                    ), Answer(
                        1,
                        getString(R.string.night_owl_i_live_at_night),
                        getString(R.string.night_owl)
                    ), Answer(
                        1,
                        getString(R.string.random_unpredictable_hours),
                        getString(R.string.random)
                    )
                ),
                answer1 = listOf(
                    Answer(1, "\uD83C\uDF05 Early Bird - Up at 7, in bed at 23", "early_bird"),
                    Answer(1, "⚖\uFE0F Balanced - Standard rhythms (8-24)", "balanced"),
                    Answer(1, "\uD83E\uDD89 Night Owl - I live at night", "night_owl"),
                    Answer(1, "\uD83C\uDFB2 Random - Unpredictable hours", "random")
                ),
            )
        )
        list.add(
            QuestionModel(
                id = "6846e12387956d4cd2609c04",
                heading = getString(R.string.question_6),
                question = getString(R.string.cleaning_common_areas),
                heading1 = "",
                question1 = "",
                id1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, getString(R.string._5_5_everything_is_always_perfect), "5"),
                    Answer(1, getString(R.string._4_5_clean_and_tidy), "4"),
                    Answer(1, getString(R.string._3_5_normal_not_obsessive), "3"),
                    Answer(1, getString(R.string._2_5_relax_i_m_not_obsessed), "2"),
                    Answer(1, getString(R.string._1_5_chaos_doesn_t_bother_me), "1")
                ),
                answer1 = listOf(
                    Answer(1, getString(R.string._5_5_everything_is_always_perfect), "5"),
                    Answer(1, getString(R.string._4_5_clean_and_tidy), "4"),
                    Answer(1, getString(R.string._3_5_normal_not_obsessive), "3"),
                    Answer(1, getString(R.string._2_5_relax_i_m_not_obsessed), "2"),
                    Answer(1, getString(R.string._1_5_chaos_doesn_t_bother_me), "1")
                ),
            )
        )
        list.add(
            QuestionModel(
                id = "6846e2842753828a29b47f49",
                heading = getString(R.string.question_7),
                question = getString(R.string.socializing_at_home),
                heading1 = "",
                question1 = "",
                id1 = "",
                type = 1,
                answer = listOf(
                    Answer(
                        1,
                        getString(R.string.social_butterfly_i_love_having_dinners_and_evenings_together),
                        getString(
                            R.string.social_butterfly
                        )
                    ), Answer(
                        1,
                        getString(R.string.friendly_chatting_yes_but_with_my_space),
                        getString(R.string.friendly)
                    ), Answer(
                        1,
                        getString(R.string.professional_friendly_but_each_for_themselves),
                        getString(R.string.professional)
                    ), Answer(
                        1, getString(R.string.ghost_home_total_privacy), getString(R.string.ghost)
                    )
                ),

                answer1 = listOf(
                    Answer(
                        1,
                        "\uD83C\uDF89 Social Butterfly - I love having \n" + "dinners and evenings together",
                        "social_butterfly"
                    ), Answer(
                        1, "☕ Friendly - Chatting yes, but with \n" + "my space", "friendly"
                    ), Answer(
                        1,
                        "\uD83E\uDD10 Professional - Friendly but each \n" + "for themselves",
                        "professional"
                    ), Answer(1, "\uD83D\uDEAA Ghost - Home = total privacy", "ghost")
                ),
            )
        )

        list.add(
            QuestionModel(
                id = "6846e33c2753828a29b47f55",
                heading = "\uD83D\uDEAC Smoking",
                question = "Do I smoke?",
                heading1 = "",
                question1 = "Do I accept smokers?",
                id1 = "6846e3672753828a29b47f5c",
                type = 2,
                answer = listOf(
                    Answer(1, getString(R.string.yes), getString(R.string.yes_only)), Answer(
                        1, getString(
                            R.string.no
                        ), getString(R.string.no_only)
                    )
                ),
                answer1 = listOf(
                    Answer(1, getString(R.string.yes), getString(R.string.yes_only)), Answer(
                        1, getString(
                            R.string.no
                        ), getString(R.string.no_only)
                    ), Answer(
                        1, getString(R.string.outside_only), getString(R.string.outside_only_small)
                    )
                )
            )
        )
        list.add(
            QuestionModel(
                id = "6846e3822753828a29b47f64",
                heading = "\uD83D\uDC3E Animals",
                question = "Do I have animals?",
                heading1 = "",
                question1 = "Do I accept them?",
                id1 = "6846e39f2753828a29b47f6b",
                type = 2,
                answer = listOf(
                    Answer(1, getString(R.string.yes), getString(R.string.yes_only)), Answer(
                        1, getString(
                            R.string.no
                        ), getString(R.string.no_only)
                    )
                ),
                answer1 = listOf(
                    Answer(1, getString(R.string.yes), getString(R.string.yes_only)), Answer(
                        1, getString(
                            R.string.no
                        ), getString(R.string.no_only)
                    ), Answer(
                        1, getString(R.string.depends), getString(R.string.depends_small)
                    )
                )
            )
        )
        list.add(
            QuestionModel(
                id = "6846e3c92753828a29b47f73",
                heading = getString(R.string.noise),
                question = getString(R.string.do_i_tolerate_music_tv),
                heading1 = "",
                question1 = "",
                id1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, getString(R.string.yes), getString(R.string.yes_only)), Answer(
                        1, getString(
                            R.string.no
                        ), getString(R.string.no_only)
                    ), Answer(
                        1, getString(R.string.with_headphones), getString(R.string.with_headphones)
                    )
                ),

                answer1 = listOf(
                    Answer(1, getString(R.string.yes), getString(R.string.yes_only)), Answer(
                        1, getString(
                            R.string.no
                        ), getString(R.string.no_only)
                    ), Answer(
                        1, getString(R.string.with_headphones), getString(R.string.with_headphones)
                    )
                )
            ),

            )

        list.add(
            QuestionModel(
                id = "6846e3ec2753828a29b47f7b",
                heading = "\uD83D\uDC65 Guests",
                question = "Overnight guests ok?",
                heading1 = "",
                question1 = "",
                id1 = "",
                type = 1,
                answer = listOf(
                    Answer(1, getString(R.string.yes), getString(R.string.yes_only)),
                    Answer(1, "No", "no"),
                    Answer(1, "With advance notice", "with_notice")
                ),
                answer1 = listOf(
                    Answer(1, getString(R.string.yes), getString(R.string.yes_only)),
                    Answer(1, "No", "no"),
                    Answer(1, "With advance notice", "with_notice")
                ),
            )

        )


        list.add(
            QuestionModel(
                id = "6846e7452753828a29b47f88",
                heading = getString(R.string.question_9),
                question = getString(R.string.your_worst_housing_nightmare),
                heading1 = "",
                question1 = "",
                id1 = "",
                type = 1,
                answer = listOf(
                    Answer(
                        1,
                        getString(R.string.dirty_messy_roommates),
                        getString(R.string.dirty_roommates)
                    ),
                    Answer(
                        1,
                        getString(R.string.too_much_noise_parties),
                        getString(R.string.too_much_noise)
                    ),
                    Answer(
                        1,
                        getString(R.string.smell_of_smoke_in_the_house),
                        getString(R.string.smell_of_smoke)
                    ),
                    Answer(
                        1,
                        getString(R.string.unmanaged_pets),
                        getString(R.string.unmanaged_pets_only)
                    ),
                    Answer(
                        1,
                        getString(R.string.hidden_costs_crazy_bills),
                        getString(R.string.hidden_costs)
                    ),
                    Answer(
                        1,
                        getString(R.string.no_privacy_too_intrusive),
                        getString(R.string.no_privacy)
                    ),
                    Answer(
                        1,
                        getString(R.string.cold_house_structural_problems),
                        getString(R.string.cold_house)
                    ),
                    Answer(
                        1,
                        getString(R.string.intrusive_owner),
                        getString(R.string.intrusive_owner_only)
                    ),
                ),
                answer1 = listOf(
                    Answer(1, getString(R.string.yes), getString(R.string.yes_only)), Answer(
                        1, getString(
                            R.string.no
                        ), getString(R.string.no_only)
                    ), Answer(
                        1, getString(R.string.outside_only), getString(R.string.no_privacy_only)
                    )
                )
            )
        )

        return list
    }

}