package com.tech.hive.ui.for_room_mate.home

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
class HomeFragmentVM @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {

    val observeCommon = SingleRequestEvent<JsonObject>()

    fun getHomeApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(Constants.userLanguage, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getHomeApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getHomeApi", "getHomeApi: $e")
            }

        }

    }


    fun getMatchedProfile(data: HashMap<String, String>, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetWithQuery(Constants.userLanguage,data, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getMatchedProfile", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getMatchedProfile", "getMatchedProfile: $e")
            }

        }

    }

    fun getMatchedProfileSecond(data: HashMap<String, String>, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetWithQuery(Constants.userLanguage,data, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getMatchedProfile", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getMatchedProfile", "getMatchedProfile: $e")
            }

        }

    }


    fun getHomeApiListening(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(Constants.userLanguage, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getHomeApiListening", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getHomeApiListening", "getHomeApi: $e")
            }

        }

    }


    fun matchLikeApi(url: String ,request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("matchLikeApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("matchLikeApi", "matchLikeApi: $e")
            }

        }

    }



    fun likeDisLike(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("likeDisLike", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("likeDisLike", "likeDisLike: $e")
            }

        }

    }

    fun userRatings(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("userRatings", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("userRatings", "userRatings: $e")
            }

        }

    }

    fun getListing(url: String,data: HashMap<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetWithQuery(Constants.userLanguage, data,url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getListing", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getListing", "getListing: $e")
            }

        }

    }

    fun deleteListingList(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiDeleteListingWithQuery(Constants.userLanguage,url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("deleteListingList", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("deleteListingList", "deleteListingList: $e")
            }

        }

    }


    fun basicDetailEditAPiCall(url: String, map: HashMap<String, RequestBody>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForMultipartPut(Constants.userLanguage, url,map).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("basicDetailEditAPiCall", it.body()))
                    } else observeCommon.postValue(
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


    fun getMatchId(url: String, data : HashMap<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetWithQuery(Constants.userLanguage, data,url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getMatchId", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(it.errorBody()), null
                        )
                    )
                }
            } catch (e: Exception) {
                Log.i("getMatchId", "getMatchId: $e")
            }

        }

    }

    fun acceptRejectAPi(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiPostForRawBody(Constants.userLanguage, url,request).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("acceptRejectAPi", it.body()))
                    } else observeCommon.postValue(
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