package com.tech.hive.ui.auth.change_password

import android.util.Log
import com.google.gson.JsonObject
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.Resource
import com.tech.hive.base.utils.event.SingleRequestEvent
import com.tech.hive.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordFragmentVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {
    val observeCommon = SingleRequestEvent<JsonObject>()
    fun forgotEmail(lang:String,request: HashMap<String, Any>, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBody(lang,request, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("forgotEmail", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("forgotEmail", "forgotEmail: $e")
            }
        }
    }


    fun verifyEmail(lang:String,request: HashMap<String, Any>, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBody(lang,request, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("verifyEmail", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("verifyEmail", "verifyEmail: $e")
            }
        }
    }


    fun changePassword(lang:String,request: HashMap<String, Any>, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBody(lang,request, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("changePassword", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("changePassword", "changePassword: $e")
            }
        }
    }
}