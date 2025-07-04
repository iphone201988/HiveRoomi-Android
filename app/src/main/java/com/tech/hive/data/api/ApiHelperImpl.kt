package com.tech.hive.data.api

import com.google.gson.JsonObject
import com.tech.hive.base.local.SharedPrefManager
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService,
    private val sharedPreferences: SharedPrefManager
) : ApiHelper {

    override suspend fun apiForRawBody(
        lang: String,
        request: HashMap<String, Any>,
        url: String
    ): Response<JsonObject> {
        return apiService.apiForRawBody(lang, request, url)
    }

    override suspend fun apiPostForRawBody(
        lang: String,
        url: String,
        request: HashMap<String, Any>,
    ): Response<JsonObject> {
        return apiService.apiPostForRawBody(lang,getTokenFromSPref(), url, request)
    }

    override suspend fun apiForFormData(
        data: HashMap<String, Any>,
        url: String
    ): Response<JsonObject> {
        return apiService.apiForFormData(data, url)
    }

    override suspend fun apiForFormDataPut(
        data: HashMap<String, Any>, url: String
    ): Response<JsonObject> {
        return apiService.apiForFormDataPut(data, url, getTokenFromSPref())
    }

    override suspend fun apiGetOutWithQuery(url: String): Response<JsonObject> {
        return apiService.apiGetOutWithQuery(url)
    }

    override suspend fun apiGetOnlyAuthToken(url: String): Response<JsonObject> {
        return apiService.apiGetOnlyAuthToken(url, getTokenFromSPref())
    }

    override suspend fun apiGetWithQuery(
        data: HashMap<String, String>,
        url: String
    ): Response<JsonObject> {
        return apiService.apiGetWithQuery(url, data)
    }

    override suspend fun apiForPostMultipart(
        url: String, map: HashMap<String, RequestBody>, part: MutableList<MultipartBody.Part>
    ): Response<JsonObject> {
        return apiService.apiForPostMultipart(url, getTokenFromSPref(), map, part)
    }

    override suspend fun apiForPostMultipart(
        url: String,
        map: HashMap<String, RequestBody>?,
        part: MultipartBody.Part?,
    ): Response<JsonObject> {
        return apiService.apiForPostMultipart(url, getTokenFromSPref(), map, part)
    }

    override suspend fun apiForMultipartPut(
        url: String, map: HashMap<String, RequestBody>?, part: MultipartBody.Part?
    ): Response<JsonObject> {
        return apiService.apiForMultipartPut(url, getTokenFromSPref(), map, part)
    }

    override suspend fun apiPutForRawBody(
        url: String,
        map: HashMap<String, Any>,
    ): Response<JsonObject> {
        return apiService.apiPutForRawBody(url, getTokenFromSPref(), map)
    }


    override suspend fun apiForFormDataPutWithToken(
        url: String,
        map: HashMap<String, RequestBody>,
        userPic: MultipartBody.Part?,
    ):Response<JsonObject> {
        return apiService.apiForFormDataPutWithToken(url,map,userPic,getTokenFromSPref())
    }

    override suspend fun apiForFormDataPutWithToken3(
        url: String,
        map: HashMap<String, RequestBody>,
        userPic: MultipartBody.Part?,
         idPic: MultipartBody.Part?,
         ownershipPic: MultipartBody.Part?,
    ):Response<JsonObject> {
        return apiService.apiForFormDataPutWithToken3(url,map,userPic,idPic,ownershipPic,getTokenFromSPref())
    }




    private fun getTokenFromSPref(): String {
        //return "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIyIiwianRpIjoiZjQyNDNlYWM0OThjNjE0YWJkM2QzZjAyYTM3Nzg2YTNjNzcxZWZlYThjZGFlM2ZlMWM0ZjIxZGY0MThjNzVlOWMzY2QwYzM1Zjk5ZmVhNmUiLCJpYXQiOjE3MTM4Njk2OTYuOTc0NjU4MDEyMzkwMTM2NzE4NzUsIm5iZiI6MTcxMzg2OTY5Ni45NzQ2NjIwNjU1MDU5ODE0NDUzMTI1LCJleHAiOjE3NDU0MDU2OTYuOTY4NjEyOTA5MzE3MDE2NjAxNTYyNSwic3ViIjoiMTI5Iiwic2NvcGVzIjpbXX0.Ek0-Hp_vfhWLA88Vwblb_111fqWTYGHyNfjbJ6sgtugEviDp18i3zejQYXTaKiHmrBX2XEKbV3Ac2WQd4Lwr2Jpuv5bcDtlv_lRXY_p8XNQrE0l4u1QnBHyfWNk9V6WFZCgfUOa-uYQLmPWGewn0dE_a7_3yGl-uibjj39yKNpIbIs2J_o_ky7_LY4cuMWepjwZh1TEhggDMW72YS_SUgUVQEoG5M2auWudH2T4mDrEjbX1BngevQ6MCemavCoNV2sm38j9DecCFfh7JcJ4p644KLDLC0W0twHJ-4U7eJOnpAjuvv9WAtMFRxlWf8Oc_codrPPs4DaLYhiMRcAwVDtu4DHw1H8iBbu8zjVkgjkBGJuusEEoB7uKWka4t-eM5VO8uwknyZVT1y5uVnHj2RSsNhFz0605o4-QJXoJkRhJAJBs9ylOYsz1-YZD-pUK_KcS5ejfQjYMU05iLCMCjWpZkJjwpuQgitPcMp2NH0kiaIlLwqDdW_BkXU-sfxGf6VFUSjX49dC2mGAx7403lc5XLTcvgIenVEiuYPOxwX_OfqUl1iOc8HHQJ8G-Kez7Xl1Yk_M2UXRSUySYagOuMsKoT9CL48qBcCZPm8Wgu_O4Y1UgKH7zvJP_ZlRvVjrX6PmNusaeqG7TE8IJ_LBs_88HKiOv-cADLiN3-7s5FZDQ"
        return "Bearer ${
            sharedPreferences.getToken()
        }"
    }
}