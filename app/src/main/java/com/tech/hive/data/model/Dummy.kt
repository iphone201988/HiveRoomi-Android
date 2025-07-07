package com.tech.hive.data.model

import android.R

data class ProfessionRole(
    val id: Int,
    val name: String
)

data class Age(
    val id: Int,
    val name: String
)

data class Gender(
    val id: Int,
    val name: String
)

data class SettingsModel(
    val heading: String, val title: String, val isFirst: Boolean
)

data class MatchesModel(
   val image:Int,val name:String
)

data class StatusModel(
   val image:Int,val status:Boolean
)

data class ChatModel(
   val image:Int,val name:String,val message:String,val time:String,val status:Boolean
)

data class NotificationModel(
   val type:Int,val title:String,val compatibilty:String?
)
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



data class DiscoverModel(
    var heading: String,
    var price: String,
    var firstTitle :String,
    var secondTitle: String

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

data class QuestionModel(
    var id: String,
    var heading: String,
    var question: String,
    var heading1: String,
    var question1: String,
    val id1: String,
    var type :Int,
    var answer : List<Answer>,
    var answer1: List<Answer> = answer.map { it.copy() },
var selectedAnswerPosition: Int = -1
)



data class Answer(val type: Int, var label : String,var value: String,  var selectedAnswer: Boolean=false)


data class DiscoverQuestionModel(
    var question: String,
    var answer : List<DiscoverAnswerModel>,
    var selectedAnswerIndex: Int = -1
)



data class DiscoverAnswerModel(var answer: String, var selectedAnswer: Boolean=false)


data class AnswerSendResponse(
    val userQuizAnswer: List<UserQuizAnswer?>?
)

data class UserQuizAnswer(
    val answer: String?,
    val quizId: String?
)










