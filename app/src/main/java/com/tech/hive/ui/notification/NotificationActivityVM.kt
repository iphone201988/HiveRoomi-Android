package com.tech.hive.ui.notification

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
class NotificationActivityVM @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {
    val notificationObserver = SingleRequestEvent<JsonObject>()

    fun getUserNotification(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            notificationObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(Constants.userLanguage, url).let {
                    if (it.isSuccessful) {
                        notificationObserver.postValue(Resource.success("getUserNotification", it.body()))
                    } else notificationObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getUserNotification", "getUserNotification: $e")
            }

        }

    }

    fun acceptRejectAPi(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            notificationObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        notificationObserver.postValue(Resource.success("acceptRejectAPi", it.body()))
                    } else notificationObserver.postValue(
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