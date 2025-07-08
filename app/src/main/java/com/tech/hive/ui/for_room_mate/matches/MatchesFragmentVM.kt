package com.tech.hive.ui.for_room_mate.matches

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
class MatchesFragmentVM @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {
    val matchObserver = SingleRequestEvent<JsonObject>()
    fun getMatchApi(url: String,data: HashMap<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            matchObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetWithQuery(Constants.userLanguage, data,url).let {
                    if (it.isSuccessful) {
                        matchObserver.postValue(Resource.success("getMatchApi", it.body()))
                    } else matchObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getMatchApi", "getMatchApi: $e")
            }

        }

    }

    fun getMatchPendingApi(url: String,data: HashMap<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            matchObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetWithQuery(Constants.userLanguage, data,url).let {
                    if (it.isSuccessful) {
                        matchObserver.postValue(Resource.success("getMatchPendingApi", it.body()))
                    } else matchObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getMatchPendingApi", "getMatchPendingApi: $e")
            }

        }

    }


    fun acceptRejectAPi(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            matchObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        matchObserver.postValue(Resource.success("acceptRejectAPi", it.body()))
                    } else matchObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("acceptRejectAPi", "likeDisLike: $e")
            }

        }

    }
}