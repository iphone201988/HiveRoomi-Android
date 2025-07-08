package com.tech.hive.data.model


/** common response ***/
data class CommonResponse(
    val message: String?, val success: Boolean?
)



/** login and signup response ***/
data class LoginResponse(
    val `data`: LoginData?, val message: String?, val success: Boolean?
)

data class LoginData(
    val _id: String?,
    val ageRange: Any?,
    val email: String?,
    val isProfileComplete: Boolean?,
    val isQuizComplete: Boolean?,
    val isVerified: Boolean?,
    val language: String?,
    val name: String?,
    val otp: Int?,
    val profileImage: String?,
    val profileRole: Int?,
    val sendOtp: Boolean?,
    val timezone: String?,
    val token: String?,
    val totalQuizDone: Int?
)

/** Verify account response ***/
data class AccountVerifyResponse(
    val `data`: VerifyData?, val message: String?, val success: Boolean?
)

data class VerifyData(
    val _id: String?,
    val ageRange: Any?,
    val email: String?,
    val isVerified: Boolean?,
    val language: String?,
    val name: String?,
    val profileImage: String?,
    val profileRole: Int?,
    val timezone: String?,
    val token: String?
)

/** resend otp  response ***/
data class ResendOtpResponse(
    val `data`: ResendOtpData?,
    val message: String?,
    val success: Boolean?
)

data class ResendOtpData(
    val _id: String?,
    val email: String?
)

/** user profile response ***/
data class UserProfileResponse(
    val `data`: UserProfileData?,
    val message: String?,
    val success: Boolean?
)

data class UserProfileData(
    val __v: Int?,
    val _id: String?,
    val ageRange: String?,
    val bio: String?,
    val campus: String?,
    val createdAt: String?,
    val deviceToken: String?,
    val deviceType: Int?,
    val email: String?,
    val firebaseToken: String?,
    val gender: String?,
    val instagram: String?,
    val isDeleted: Boolean?,
    val isOnline: Boolean?,
    val isVerified: Boolean?,
    val jti: String?,
    val language: String?,
    val latitude: Double?,
    val likeCount: Int?,
    val linkedAccounts: List<LinkedAccount?>?,
    val linkedin: String?,
    val location: Location?,
    val loginType: Int?,
    val longitude: Double?,
    val name: String?,
    val onwershipProof: String?,
    val otp: String?,
    val otpExpiry: String?,
    val profession: String?,
    val profileImage: String?,
    val profileRole: Int?,
    val ratings: List<Any?>?,
    val timezone: String?,
    val updatedAt: String?,
    val userIdProof: String?

)

data class LinkedAccount(
    val _id: String?,
    val socialId: String?,
    val socialType: Int?
)

data class Location(
    val coordinates: List<Double?>?,
    val type: String?
)
/*** quiz question response ***/
data class QuizQuestionResponse(
    val `data`: List<QuestionData?>?,
    val message: String?,
    val success: Boolean?
)

data class QuestionData(
    val _id: String?,
    val description: String?,
    val quiz: List<Quiz?>?,
    val sort: Int?,
    val title: String?
)

data class Quiz(
    val group: String?,
    val groupValue: List<GroupValue?>?
)

data class GroupValue(
    val _id: String?,
    val answer: Any?,
    val options: List<Option?>?,
    val quizPosition: Int?,
    val title: String?,
    val type: String?,
    var selectedAnswer : Boolean =false
)

data class Option(
    val label: String?,
    val value: String?
)

/** quiz answer model **/
data class QuizAnswerResponse(
    val `data`: QuizAnswerData?,
    val message: String?,
    val success: Boolean?
)

data class QuizAnswerData(
    val _id: String?,
    val ageRange: String?,
    val bio: String?,
    val email: String?,
    val gender: String?,
    val instagram: String?,
    val isProfileComplete: Boolean?,
    val isQuizComplete: Boolean?,
    val isVerified: Boolean?,
    val language: String?,
    val linkedin: String?,
    val name: String?,
    val profession: String?,
    val profileImage: String?,
    val profileRole: Int?,
    val timezone: String?,
    val token: String?,
    val totalQuizDone: Int?
)
/** home api model **/
data class HomeApiResponse(
    val `data`: List<HomeApiData?>?,
    val message: String?,
    val pagination: Pagination?,
    val success: Boolean?
)

data class HomeApiData(
    val _id: String?,
    val ageRange: String?,
    val bio: String?,
    val campus: String?,
    val email: String?,
    val gender: String?,
    val name: String?,
    val profession: String?,
    val profileImage: String?,
    val profileRole: Int?,
    val providerType: String?,
    val quizs: List<HomeApiDataQuiz?>?,
    val timezone: String?



)



data class HomeApiDataQuiz(
    val answer: String?,
    val title: String?
)

/** second type home api model **/
data class HomeRoomType(
    val `data`: List<HomeRoomTData?>?,
    val message: String?,
    val pagination: Pagination?,
    val success: Boolean?
)

data class HomeRoomTData(
    val _id: String?,
    val address: String?,
    val description: String?,
    val images: List<String?>?,
    val latitude: Double?,
    val longitude: Double?,
    val pets: Boolean?,
    val price: Int?,
    val roomType: String?,
    val roommates: List<Roommate?>?,
    val smoke: String?,
    val title: String?,
    val userId: String?,
    val videos: List<Any?>?
)



data class Roommate(
    val _id: String?,
    val age: Int?,
    val gender: String?
)


/** Pending Match Response  **/
data class PendingMatchResponse(
    val `data`: List<PendingMatchData?>?,
    val message: String?,
    val pagination: Pagination?,
    val success: Boolean?
)

data class PendingMatchData(
    val _id: String?,
    val createdAt: String?,
    val status: Int?,
    val type: String?,
    val userId: UserId?
)


data class UserId(
    val _id: String?,
    val email: String?,
    val likeCount: Int?,
    val name: String?,
    val profileImage: String?
)

/** Get Chat Response  **/
data class GetChatResponse(
    val `data`: List<GetChatData>?,
    val message: String?,
    val onlineUsers: List<OnlineUser>?,
    val pagination: Pagination?,
    val success: Boolean?,
    val unReadMessageCount: Int?
)

data class GetChatData(
    val _id: String?,
    val messages: Messages?,
    val otherUser: OtherUser?,
    val updatedAt: String?
)

data class OnlineUser(
    val _id: String?,
    val otherUser: OtherUser?,
    val updatedAt: String?
)

data class Pagination(
    val limit: Int?,
    val page: Int?,
    val total: Int?
)

data class Messages(
    val __v: Int?,
    val _id: String?,
    val chatId: String?,
    val content: String?,
    val contentType: String?,
    val createdAt: String?,
    val deletedBy: List<Any?>?,
    val isRead: Boolean?,
    val `receiver`: String?,
    val sender: String?,
    val updatedAt: String?
)

/** Get User Message Response  **/
data class GetUserMessageResponse(
    val checkUserBlock: Any?,
    val `data`: List<GetUserData?>?,
    val isBlocked: Boolean?,
    val message: String?,
    val otherUser: OtherUser?,
    val pagination: ChatPagination?,
    val success: Boolean?
)

data class GetUserData(
    val _id: String?,
    val content: String?,
    val contentType: String?,
    val createdAt: String?,
    val isRead: Boolean?,
    val sender: Sender?
)

data class OtherUser(
    val _id: String?,
    val isOnline: Boolean?,
    val name: String?,
    val profileImage: String?
)

data class ChatPagination(
    val limit: Int?,
    val page: Int?,
    val total: Int?
)

data class Sender(
    val _id: String?,
    val isOnline: Boolean?,
    val name: String?,
    val profileImage: String?
)

















/** Upload Image Response  **/
data class UploadImageResponse(
    val `data`: UploadImageData?,
    val message: String?,
    val success: Boolean?
)

data class UploadImageData(
    val url: String?
)