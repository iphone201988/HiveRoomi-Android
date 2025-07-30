package com.tech.hive.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import okhttp3.MultipartBody


data class SettingsModel(
    val heading: String, val title: String, val isFirst: Boolean
)

data class ImageModel(
    var image: String,
    var type: Int,
    val imageMultiplatform: MutableList<Pair<String, MultipartBody.Part>> = mutableListOf()
)





data class CompatibilityModel(
    val title:String
)

data class BoostPlanModel(
    val duration:String,val price:String,val perDayCost:String,val saving:String,var isSelected:Boolean=false
)

data class HomeFilterList(val userStatus :Int,
    val name:String,var isSelected:Boolean=false
)

data class PrivacyModel(
    val title: String, val subTitle:String
)

data class MatchProfileModel(
    var title: String,
    var desc: String,
    var heading: String,
    var check :Boolean)
data class SecondMatchProfileModel(
    var title: String,
    var desc: String,
    var heading: String,
    var check :Boolean)




data class Answer(val type: Int, var label : String,var value: String,  var selectedAnswer: Boolean=false)


data class DiscoverQuestionModel(
    var question: String,
    var answer : List<DiscoverAnswerModel>,
    var selectedAnswerIndex: Int = -1
)

data class DiscoverAnswerModel(var answer: String, var selectedAnswer: Boolean=false)




@Parcelize
data class RoommateModelClass(
    var age: String,
    var professionRole: String,
    var campus: String,
    var gender: String,
    var language: String,
    var location: String,
    var lat: Double?,
    var long: Double?
) : Parcelable

@Parcelize
data class HomeModelClass(
    var amenities: String,
    var propertyFeatures : String,
    var furnishedStatus: String,

): Parcelable
