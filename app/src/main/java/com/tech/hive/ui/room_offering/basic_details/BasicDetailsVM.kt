package com.tech.hive.ui.room_offering.basic_details

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
class BasicDetailsVM @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {
    val basicDetailObserver = SingleRequestEvent<JsonObject>()

    fun basicDetailApiCall(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            basicDetailObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        basicDetailObserver.postValue(Resource.success("basicDetailApiCall", it.body()))
                    } else basicDetailObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("basicDetailApiCall", "basicDetailApiCall: $e")
            }

        }

    }
}