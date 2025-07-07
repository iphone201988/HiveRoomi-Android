package com.tech.hive.ui.quiz.question

import android.util.Log
import com.google.gson.JsonObject
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.Resource
import com.tech.hive.base.utils.event.SingleRequestEvent
import com.tech.hive.data.api.ApiHelper
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.AnswerSendResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizQuestionFragmentVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {
    val observeCommon = SingleRequestEvent<JsonObject>()
    fun getQuizQuestion(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(Constants.userLanguage, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getQuizQuestion", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getQuizQuestion", "getQuizQuestion: $e")
            }

        }

    }

    fun quizAnswerApi(lang: String, url: String, request: AnswerSendResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForQuiz(lang, url, request).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("quizAnswerApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("quizAnswerApi", "quizAnswerApi: $e")
            }

        }

    }

}