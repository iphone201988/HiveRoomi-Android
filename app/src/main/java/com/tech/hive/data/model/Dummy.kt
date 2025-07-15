package com.tech.hive.data.model

import android.net.Uri

data class SettingsModel(
    val heading: String, val title: String, val isFirst: Boolean
)

data class ImageModel(var image : String, var type : Int)

data class CompatibilityModel(
    val title:String
)
data class RoomMateModel(
    val gender:String, val age:String
)

data class BoostPlanModel(
    val duration:String,val price:String,val perDayCost:String,val saving:String,var isSelected:Boolean=false
)

data class HomeFilterList(
    val name:String,var isSelected:Boolean=false
)

data class FrequentModel(
    val title: String
)
data class SafetyModel(
    val title: String, val subTitle:String
)

data class CommunityModel(
    val title: String, val subTitle:String
)
data class PrivacyModel(
    val title: String, val subTitle:String
)


data class ThirdTypeModel(
    var heading: String,
    var price: String,
    var firstTitle :String,
    var secondTitle: String,
    var isCheck :Boolean = false

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

