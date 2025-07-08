package com.tech.hive.ui.for_room_mate.messages

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
class MessagesFragmentVM @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {
    val messagesObserver = SingleRequestEvent<JsonObject>()

    fun getChatApi(url: String,data: HashMap<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            messagesObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetWithQuery(Constants.userLanguage, data,url).let {
                    if (it.isSuccessful) {
                        messagesObserver.postValue(Resource.success("getChatApi", it.body()))
                    } else messagesObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getChatApi", "getChatApi: $e")
            }

        }

    }
}