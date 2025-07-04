package com.tech.hive.data.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface ApiHelper {
    suspend fun apiForRawBody(
        lang: String,
        request: HashMap<String, Any>,
        url: String
    ): Response<JsonObject>

    suspend fun apiPostForRawBody(
        lang: String,
        url: String,
        request: HashMap<String, Any>
    ): Response<JsonObject>

    suspend fun apiForFormData(data: HashMap<String, Any>, url: String): Response<JsonObject>
    suspend fun apiForFormDataPut(data: HashMap<String, Any>, url: String): Response<JsonObject>
    suspend fun apiGetOutWithQuery(url: String): Response<JsonObject>
    suspend fun apiGetOnlyAuthToken(url: String): Response<JsonObject>
    suspend fun apiGetWithQuery(data: HashMap<String, String>, url: String): Response<JsonObject>
    suspend fun apiForPostMultipart(
        url: String,
        map: HashMap<String, RequestBody>,
        part: MutableList<MultipartBody.Part>
    ): Response<JsonObject>

    suspend fun apiForPostMultipart(
        url: String,
        map: HashMap<String, RequestBody>?,
        part: MultipartBody.Part?
    ): Response<JsonObject>

    suspend fun apiForMultipartPut(
        url: String,
        map: HashMap<String, RequestBody>?,
        part: MultipartBody.Part?
    ): Response<JsonObject>

    suspend fun apiPutForRawBody(url: String, map: HashMap<String, Any>): Response<JsonObject>

    suspend fun apiForFormDataPutWithToken(
        url: String, map: HashMap<String, RequestBody>, userPic: MultipartBody.Part?,
    ): Response<JsonObject>

    suspend fun apiForFormDataPutWithToken3(
        url: String, map: HashMap<String, RequestBody>, userPic: MultipartBody.Part?,
        idPic: MultipartBody.Part?,
        ownershipPic: MultipartBody.Part?,
    ): Response<JsonObject>

}