package com.tech.hive.ui.for_room_mate.calenders

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
class CalenderActivityVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel(){
    val chatObserver = SingleRequestEvent<JsonObject>()

    fun getVisitApiCAll(url: String, data: HashMap<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            chatObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetWithQuery(Constants.userLanguage, data, url).let {
                    if (it.isSuccessful) {
                        chatObserver.postValue(Resource.success("getVisitApiCAll", it.body()))
                    } else chatObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getVisitApiCAll", "getVisitApiCAll: $e")
            }

        }

    }
}