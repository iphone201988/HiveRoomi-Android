package com.tech.hive.ui.for_room_mate.settings

import android.util.Log
import com.google.gson.JsonObject
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.Resource
import com.tech.hive.base.utils.event.SingleRequestEvent
import com.tech.hive.data.api.ApiHelper
import com.tech.hive.data.api.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {
    val settingObserver = SingleRequestEvent<JsonObject>()

    fun reportAPiCall(url: String,request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            settingObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        settingObserver.postValue(Resource.success("reportAPiCall", it.body()))
                    } else settingObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("reportAPiCall", "reportAPiCall: $e")
            }

        }

    }

    fun logoutApiCall(url: String,request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            settingObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        settingObserver.postValue(Resource.success("logoutApiCall", it.body()))
                    } else settingObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("logoutApiCall", "logoutApiCall: $e")
            }

        }

    }


    fun deleteApiCall(url: String,request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            settingObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        settingObserver.postValue(Resource.success("deleteApiCall", it.body()))
                    } else settingObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("deleteApiCall", "deleteApiCall: $e")
            }

        }

    }



    fun feedBackApiCall(url: String,request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            settingObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        settingObserver.postValue(Resource.success("feedBackApiCall", it.body()))
                    } else settingObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("feedBackApiCall", "feedBackApiCall: $e")
            }

        }

    }


    fun askedQuestionsApiCall(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            settingObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(Constants.userLanguage, url).let {
                    if (it.isSuccessful) {
                        settingObserver.postValue(Resource.success("askedQuestionsApiCall", it.body()))
                    } else settingObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("askedQuestionsApiCall", "askedQuestionsApiCall: $e")
            }

        }

    }


}