package com.tech.hive.ui.auth.personal_information

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
class PersonalFragmentVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel(){

    val observeCommon = SingleRequestEvent<JsonObject>()

    // personal information api

    fun userPersonalInformation(
        data: HashMap<String, RequestBody>,
        userPic: MultipartBody.Part?,

    ) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                val response =
                    apiHelper.apiForFormDataPutWithToken(Constants.USER_ME, data,userPic)
                if (response.isSuccessful && response.body() != null) {
                    observeCommon.postValue(Resource.success("userPersonalInformation", response.body()))
                } else {
                    observeCommon.postValue(
                        Resource.error(handleErrorResponse(response.errorBody(),response.code()), null)
                    )
                }
            } catch (e: java.lang.Exception) {
                observeCommon.postValue(Resource.error(e.message, null))
            }
        }
    }
}