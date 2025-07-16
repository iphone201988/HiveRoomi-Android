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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class BasicDetailsVM @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {
    val basicDetailObserver = SingleRequestEvent<JsonObject>()

    fun basicDetailApiCall(url: String, map: HashMap<String, RequestBody>, part: MutableList<MultipartBody.Part>,video: MultipartBody.Part?) {
        CoroutineScope(Dispatchers.IO).launch {
            basicDetailObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForPostMultipart(Constants.userLanguage, url,map,part,video).let {
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

    fun basicDetailEditAPiCall(url: String, map: HashMap<String, RequestBody>, part: MutableList<MultipartBody.Part>,video: MultipartBody.Part?) {
        CoroutineScope(Dispatchers.IO).launch {
            basicDetailObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForPostMultipartPut(Constants.userLanguage, url,map,part,video).let {
                    if (it.isSuccessful) {
                        basicDetailObserver.postValue(Resource.success("basicDetailEditAPiCall", it.body()))
                    } else basicDetailObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("basicDetailEditAPiCall", "basicDetailEditAPiCall: $e")
            }

        }

    }
}