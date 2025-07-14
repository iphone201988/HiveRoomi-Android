package com.tech.hive.ui.for_room_mate.profile

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
class ProfileFragmentVM @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {
    val profileObserver = SingleRequestEvent<JsonObject>()

    fun getUserProfile(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            profileObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(Constants.userLanguage, url).let {
                    if (it.isSuccessful) {
                        profileObserver.postValue(Resource.success("getUserProfile", it.body()))
                    } else profileObserver.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getUserProfile", "getUserProfile: $e")
            }

        }

    }


    fun userUpdateProfile(
        data: HashMap<String, RequestBody>,
        userPic: MultipartBody.Part?,

        ) {
        CoroutineScope(Dispatchers.IO).launch {
            profileObserver.postValue(Resource.loading(null))
            try {
                val response =
                    apiHelper.apiForFormDataPutWithToken(Constants.USER_ME, data,userPic)
                if (response.isSuccessful && response.body() != null) {
                    profileObserver.postValue(Resource.success("userUpdateProfile", response.body()))
                } else {
                    profileObserver.postValue(
                        Resource.error(handleErrorResponse(response.errorBody(),response.code()), null)
                    )
                }
            } catch (e: java.lang.Exception) {
                profileObserver.postValue(Resource.error(e.message, null))
            }
        }
    }

}