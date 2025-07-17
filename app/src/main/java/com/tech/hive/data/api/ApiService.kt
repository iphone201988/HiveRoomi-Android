package com.tech.hive.data.api

import com.google.gson.JsonObject
import com.tech.hive.data.model.AnswerSendRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.QueryMap
import retrofit2.http.Url


interface ApiService {

//    @Header("Authorization") token: String,

    @POST
    suspend fun apiForRawBody(
        @Header("Accept-Language") lang: String, @Body data: HashMap<String, Any>, @Url url: String
    ): Response<JsonObject>

    @POST
    suspend fun apiPostForRawBody(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Url url: String,
        @Body data: HashMap<String, Any>
    ): Response<JsonObject>


    @POST
    suspend fun apiPostForQuiz(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Url url: String,
        @Body data: AnswerSendRequest
    ): Response<JsonObject>

    @PUT
    suspend fun apiPutForRawBody(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body data: HashMap<String, Any>,
    ): Response<JsonObject>

    @FormUrlEncoded
    @POST
    suspend fun apiForFormData(
        @FieldMap data: HashMap<String, Any>, @Url url: String
    ): Response<JsonObject>


    @FormUrlEncoded
    @PUT
    suspend fun apiForFormDataPut(
        @FieldMap data: HashMap<String, Any>,
        @Url url: String,
        @Header("Authorization") token: String
    ): Response<JsonObject>


    @Multipart
    @JvmSuppressWildcards
    @PUT
    suspend fun apiForFormDataPutWithToken(
        @Url url: String,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part userPic: MultipartBody.Part?,
        @Header("Authorization") token: String
    ): Response<JsonObject>


    @Multipart
    @JvmSuppressWildcards
    @POST
    suspend fun apiForImageUpload(
        @Header("Accept-Language") lang: String,
        @Url url: String,
        @Part userPic: MultipartBody.Part?,
        @Header("Authorization") token: String
    ): Response<JsonObject>


    @Multipart
    @JvmSuppressWildcards
    @PUT
    suspend fun apiForFormDataPutWithToken3(
        @Header("Accept-Language") lang: String,
        @Url url: String,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part userPic: MultipartBody.Part?,
        @Part idPic: MultipartBody.Part?,
        @Part ownershipPic: MultipartBody.Part?,
        @Header("Authorization") token: String
    ): Response<JsonObject>

    @GET
    suspend fun apiGetOutWithQuery(@Url url: String): Response<JsonObject>

    @GET
    suspend fun apiGetOnlyAuthToken(
        @Header("Accept-Language") lang: String,
        @Url url: String,
        @Header("Authorization") token: String
    ): Response<JsonObject>


    @GET
    suspend fun apiGetWithQuery(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Url url: String,
        @QueryMap data: HashMap<String, String>
    ): Response<JsonObject>

    @DELETE
    suspend fun apiDeleteListingWithQuery(
        @Header("Accept-Language") lang: String,
        @Header("Authorization") token: String,
        @Url url: String,
    ): Response<JsonObject>


    @Multipart
    @JvmSuppressWildcards
    @POST
    suspend fun apiForPostMultipart(
        @Header("Accept-Language") lang: String,
        @Url url: String,
        @Header("Authorization") token: String,
        @PartMap data: Map<String, RequestBody>,
        @Part image: MutableList<MultipartBody.Part>,
        @Part video: MultipartBody.Part?
    ): Response<JsonObject>


    @Multipart
    @JvmSuppressWildcards
    @PUT
    suspend fun apiForPostMultipartPut(
        @Header("Accept-Language") lang: String,
        @Url url: String,
        @Header("Authorization") token: String,
        @PartMap data: Map<String, RequestBody>,
        @Part image: MutableList<MultipartBody.Part>,
        @Part video: MultipartBody.Part?
    ): Response<JsonObject>





    @Multipart
    @JvmSuppressWildcards
    @PUT
    suspend fun apiMultipartPutWithoutParam(
        @Url url: String,
        @Header("Authorization") token: String,
        @Part userIdProof: MultipartBody.Part?,
        @Part ownershipProof: MultipartBody.Part?
    ): Response<JsonObject>


    @Headers(Constants.HEADER_API)
    @Multipart
    @JvmSuppressWildcards
    @PUT
    suspend fun apiForMultipartPut(
        @Header("Accept-Language") lang: String,
        @Url url: String,
        @Header("Authorization") token: String,
        @PartMap data: Map<String, RequestBody>?,
    ): Response<JsonObject>




}