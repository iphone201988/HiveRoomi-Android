package com.tech.hive.ui.for_room_mate.messages.chat

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
import javax.inject.Inject

@HiltViewModel
class ChatActivityVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel(){

val chatObserver = SingleRequestEvent<JsonObject>()

fun getChatWithIdApi(url: String, data: HashMap<String, String>) {
    CoroutineScope(Dispatchers.IO).launch {
        chatObserver.postValue(Resource.loading(null))
        try {
            apiHelper.apiGetWithQuery(Constants.userLanguage, data, url).let {
                if (it.isSuccessful) {
                    chatObserver.postValue(Resource.success("getChatWithIdApi", it.body()))
                } else chatObserver.postValue(
                    Resource.error(
                        handleErrorResponse(it.errorBody()), null
                    )
                )
            }
        } catch (e: Exception) {
            Log.i("getChatWithIdApi", "getChatWithIdApi: $e")
        }

    }

}

    fun sendImageAPi(url: String, userPic: MultipartBody.Part?) {
    CoroutineScope(Dispatchers.IO).launch {
        chatObserver.postValue(Resource.loading(null))
        try {
            apiHelper.apiForImageUpload(Constants.userLanguage, url,userPic).let {
                if (it.isSuccessful) {
                    chatObserver.postValue(Resource.success("sendImageAPi", it.body()))
                } else chatObserver.postValue(
                    Resource.error(
                        handleErrorResponse(it.errorBody()), null
                    )
                )
            }
        } catch (e: Exception) {
            Log.i("sendImageAPi", "sendImageAPi: $e")
        }

    }

}


    fun userBlockAPi( request: HashMap<String, Any>,url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            chatObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        chatObserver.postValue(Resource.success("userBlockAPi", it.body()))
                    } else chatObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("userBlockAPi", "userBlockAPi: $e")
            }

        }

    }

    fun userUnBlockAPi( request: HashMap<String, Any>,url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            chatObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        chatObserver.postValue(Resource.success("userUnBlockAPi", it.body()))
                    } else chatObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("userUnBlockAPi", "userUnBlockAPi: $e")
            }

        }

    }

}