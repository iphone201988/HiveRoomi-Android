package com.tech.hive.data.api

import com.google.gson.JsonObject
import com.tech.hive.data.model.AnswerSendRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface ApiHelper {
    suspend fun apiForRawBody(
        lang: String, request: HashMap<String, Any>, url: String
    ): Response<JsonObject>

    suspend fun apiPostForRawBody(
        lang: String, url: String, request: HashMap<String, Any>
    ): Response<JsonObject>


    suspend fun apiPostForQuiz(
        lang: String, url: String, request: AnswerSendRequest
    ): Response<JsonObject>

    suspend fun apiForFormData(data: HashMap<String, Any>, url: String): Response<JsonObject>
    suspend fun apiForFormDataPut(data: HashMap<String, Any>, url: String): Response<JsonObject>
    suspend fun apiGetOutWithQuery(url: String): Response<JsonObject>
    suspend fun apiGetOnlyAuthToken(lang: String, url: String): Response<JsonObject>
    suspend fun apiGetWithQuery(
        lang: String, data: HashMap<String, String>, url: String
    ): Response<JsonObject>

    suspend fun apiDeleteListingWithQuery(
        lang: String,  url: String
    ): Response<JsonObject>

    suspend fun apiForPostMultipart(
        lang: String,
        url: String, map: HashMap<String, RequestBody>, part: MutableList<MultipartBody.Part>,video: MultipartBody.Part?
    ): Response<JsonObject>

    suspend fun apiForPostMultipartPut(
        lang: String,
        url: String, map: HashMap<String, RequestBody>, part: MutableList<MultipartBody.Part>,video: MultipartBody.Part?
    ): Response<JsonObject>

    suspend fun apiForPostMultipart(
        url: String, map: HashMap<String, RequestBody>?, part: MultipartBody.Part?
    ): Response<JsonObject>

    suspend fun apiForMultipartPut(
        url: String, map: HashMap<String, RequestBody>?, part: MultipartBody.Part?
    ): Response<JsonObject>

    suspend fun apiPutForRawBody(url: String, map: HashMap<String, Any>): Response<JsonObject>

    suspend fun apiForFormDataPutWithToken(
        url: String, map: HashMap<String, RequestBody>, userPic: MultipartBody.Part?,
    ): Response<JsonObject>

    suspend fun apiForFormDataPutWithToken3(
        lang: String,
        url: String, map: HashMap<String, RequestBody>, userPic: MultipartBody.Part?,
        idPic: MultipartBody.Part?,
        ownershipPic: MultipartBody.Part?,
    ): Response<JsonObject>


    suspend fun apiForImageUpload(
        lang: String,
        url: String,
        userPic: MultipartBody.Part?,
    ): Response<JsonObject>

}