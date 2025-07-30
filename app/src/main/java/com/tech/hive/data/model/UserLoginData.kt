package com.tech.hive.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/** common response ***/
data class CommonResponse(
    val message: String?, val success: Boolean?
)

/*** rating response **/
data class RatingsResponse(
    val `data`: RatingsData?, val message: String?, val success: Boolean?
)

data class RatingsData(
    val averageRating: Double?, val totalRatings: Int?
)
/** Frequently Asked Questions Response **/
data class CommonQuestionsResponse(
    val `data`: List<CommonQuestionsData?>?,
    val message: String?,
    val success: Boolean?
)

data class CommonQuestionsData(
    val answer: String?,
    val question: String?
)

/** login and signup response ***/
data class LoginResponse(
    val `data`: LoginData?, val message: String?, val success: Boolean?
)

data class LoginData(
    val otp: Int?,
    val sendOtp: Boolean?,
    val _id: String?,
    val address: String?,
    val ageRange: String?,
    val bio: String?,
    val campus: String?,
    val email: String?,
    val gender: String?,
    val instagram: String?,
    val isProfileComplete: Boolean?,
    val isQuizComplete: Boolean?,
    val isVerified: Boolean?,
    val language: String?,
    val latitute: Double?,
    val longitude: Double?,
    val name: String?,
    val onwershipProof: String?,
    val profession: String?,
    val profileImage: String?,
    val profileRole: Int,
    val providerType: String?,
    val timezone: String?,
    val token: String?,
    val totalQuizDone: Int?,
    val userIdProof: String?
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
    val `data`: ResendOtpData?, val message: String?, val success: Boolean?
)

data class ResendOtpData(
    val _id: String?, val email: String?
)

/** user profile response ***/
data class UserProfileResponse(
    val `data`: UserProfileData?, val message: String?, val success: Boolean?
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
    val _id: String?, val socialId: String?, val socialType: Int?
)
@Parcelize
data class Location(
    val coordinates: List<Double?>?, val type: String?
): Parcelable

/*** quiz question response ***/
data class QuizQuestionResponse(
    val `data`: List<QuestionData?>?, val message: String?, val success: Boolean?
)

data class QuestionData(
    val _id: String?,
    val description: String?,
    val quiz: List<Quiz?>?,
    val sort: Int?,
    val title: String?
)

data class Quiz(
    val group: String?, val groupValue: List<GroupValue?>?
)

data class GroupValue(
    val _id: String?,
    var answer: String?,
    var options: List<Option?>?,
    val quizPosition: Int?,
    val title: String?,
    val type: String?,
    var selectedAnswer: Boolean = false
)

data class Option(
    val label: String?,
    var value: String?,
    var selectedAnswer: Boolean = false,
    var inputValue: String? = "",
    var lat: Double?=0.0,
    var long: Double?=0.0
)

/** quiz answer model **/
data class QuizAnswerResponse(
    val `data`: QuizAnswerData?, val message: String?, val success: Boolean?
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
@Parcelize
data class HomeApiResponse(
    val `data`: List<HomeApiData?>?,
    val message: String?,
    val pagination: Pagination?,
    val success: Boolean?,
    val unReadNotifications: Int?
): Parcelable
@Parcelize
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


): Parcelable

@Parcelize
data class HomeApiDataQuiz(
    val answer: String?, val title: String?
): Parcelable

/** second type home api model **/
@Parcelize
data class HomeRoomType(
    val `data`: List<HomeRoomTData?>?,
    val message: String?,
    val pagination: Pagination?,
    val success: Boolean?,
    val unReadNotifications: Int?


): Parcelable
@Parcelize
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
    val videos: String?

): Parcelable

@Parcelize
data class Roommate(
    val _id: String?, val age: Int?, val gender: String?
): Parcelable


/** Pending Match Response  **/
@Parcelize
data class PendingMatchResponse(
    val `data`: List<PendingMatchData?>?,
    val message: String?,
    val pagination: Pagination?,
    val success: Boolean?
): Parcelable
@Parcelize
data class PendingMatchData(
    val _id: String?,
    val createdAt: String?,
    val status: Int?,
    val type: String?,
    val userId: UserId?,
    val listingId: ListingId?,


    ): Parcelable
@Parcelize
data class UserId(
    val _id: String?,
    val email: String?,
    val likeCount: Int?,
    val name: String?,
    val profileImage: String?
): Parcelable

@Parcelize
data class ListingId(
    val _id: String?,
    val address: String?,
    val description: String?,
    val images: List<String?>?,
    val latitude: Double?,
    val likeCount: Int?,
    val longitude: Double?,
    val pets: Boolean?,
    val price: Int?,
    val roomType: String?,
    val roommates: List<ListingRoommate?>?,
    val smoke: String?,
    val title: String?,
    val videos: String?
): Parcelable

@Parcelize
data class ListingRoommate(
    val _id: String?, val age: Int?, val gender: String?
): Parcelable

/** Get Chat Response  **/
@Parcelize
data class GetChatResponse(
    val `data`: List<GetChatData>?,
    val message: String?,
    val onlineUsers: List<OnlineUser>?,
    val pagination: Pagination?,
    val success: Boolean?,
    val unReadMessageCount: Int?,
    val unReadNotifications: Int?
): Parcelable
@Parcelize
data class GetChatData(
    val _id: String?, val messages: Messages?, val otherUser: OtherUser?, val updatedAt: String?
): Parcelable
@Parcelize
data class OnlineUser(
    val _id: String?, val otherUser: OtherUser?, val updatedAt: String?
): Parcelable
@Parcelize
data class Pagination(
    val currentPage: Int?,
    val total: Int?,
    val totalPages: Int?

): Parcelable
@Parcelize
data class Messages(
    val __v: Int?,
    val _id: String?,
    val chatId: String?,
    val content: String?,
    val contentType: String?,
    val createdAt: String?,
    val deletedBy: List<String?>?,
    val isRead: Boolean?,
    val `receiver`: String?,
    val sender: String?,
    val updatedAt: String?
): Parcelable

/** Get User Message Response  **/
data class GetUserMessageResponse(
    val checkUserBlock: CheckUserBlock?,
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
@Parcelize
data class OtherUser(
    val _id: String?, val isOnline: Boolean?, val name: String?, val profileImage: String?
): Parcelable

data class ChatPagination(
    val currentPage: Int?,
    val total: Int?,
    val totalPages: Int?
)

data class Sender(
    val _id: String?, val isOnline: Boolean?, val name: String?, val profileImage: String?
)


data class CheckUserBlock(
    val __v: Int?,
    val _id: String?,
    val blockBy: String?,
    val createdAt: String?,
    val userId: String?
)


/** Upload Image Response  **/
data class UploadImageResponse(
    val `data`: UploadImageData?, val message: String?, val success: Boolean?
)

data class UploadImageData(
    val url: String?
)

/** Match Profile User Response  **/
data class MatchProfileUserResponse(
    val `data`: MatchProfileData?, val success: Boolean?
)

data class MatchProfileData(
    val _id: String?,
    val ageRange: String?,
    val averageRating: Double?,
    val bio: String?,
    val campus: String?,
    val email: String?,
    val gender: String?,
    val instagram: String?,
    val likeStatus: Int?,
    val address: String?,
    val linkedin: String?,
    val name: String?,
    val profession: String?,
    val profileImage: String?,
    val profileRole: Int?,
    val providerType: String?,
    val quizs: List<MatchProfileQuiz?>?,
    val ratings: List<Rating?>?,
    val timezone: String?,
    val totalRatings: Int?
)

data class MatchProfileQuiz(
    val answer: String?, val title: String?
)

data class Rating(
    val _id: String?, val rating: Double?, val userId: String?
)


/** second match User Response  **/
data class SecondMatchProfileResponse(
    val `data`: SecondMatchData?, val likeStatus: Int?, val message: String?, val success: Boolean?
)

data class SecondMatchData(
    val __v: Int?,
    val _id: String?,
    val address: String?,
    val airConditioner: Boolean?,
    val availableFrom: String?,
    val balcony: Boolean?,
    val cleanliness: Int?,
    val contractLength: String?,
    val createdAt: String?,
    val deposit: Int?,
    val description: String?,
    val elevator: Boolean?,
    val floor: String?,
    val furnishingStatus: String?,
    val heating: String?,
    val images: List<String?>?,
    val isDeleted: Boolean?,
    val kitchen: Boolean?,
    val latitude: Double?,
    val likeCount: Int?,
    val listingType: String?,
    val location: SecondLocation?,
    val longitude: Double?,
    val lookingFor: List<String?>?,
    val minimumStay: String?,
    val parking: Boolean?,
    val pets: Boolean?,
    val price: Int?,
    val privateBathroom: Boolean?,
    val roomType: String?,
    val roommates: List<SecondRoommate?>?,
    val size: String?,
    val smoke: String?,
    val status: Int?,
    val title: String?,
    val updatedAt: String?,
    val userId: String?,
    val utilitiesPrice: Int?,
    val videos: String?,
    val viewCount: Int?,
    val washingMachine: Boolean?,
    val wifi: Boolean?
)

data class SecondLocation(
    val coordinates: List<Double?>?, val type: String?
)

data class SecondRoommate(
    val _id: String?, val age: Int?, val gender: String?
)


/** get user profile Response  **/
data class GetUserProfileResponse(
    val `data`: UserProfile?, val message: String?, val success: Boolean?
)

data class UserProfile(
    val _id: String?,
    val ageRange: String?,
    val bio: String?,
    val email: String?,
    val gender: String?,
    val instagram: String?,
    val isVerified: Boolean?,
    val language: String?,
    val linkedin: String?,
    val name: String?,
    val onwershipProof: String?,
    val profession: String?,
    val profileImage: String?,
    val profileRole: Int,
    var address: String?,
    var campus: String?,
    val providerType: String?,
    val quizs: List<UserProfileQuiz?>?,
    val timezone: String?,
    val userIdProof: String?,
    val userResponse: List<UserResponse?>?
)

/** quiz answer model **/
data class UserProfileQuiz(
    val answer: String?, val title: String?
)

data class UserResponse(
    val __v: Int?,
    val _id: String?,
    val answer: String?,
    val quizId: String?,
    val quizTitle: String?,
    val userId: String?
)

/** role change response **/
data class RoleChangeResponse(
    val `data`: RoleChangeData?, val message: String?, val success: Boolean?
)

data class RoleChangeData(
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
    val onwershipProof: Any?,
    val profession: String?,
    val profileImage: String?,
    val profileRole: Int,
    val providerType: String?,
    val timezone: String?,
    val token: String?,
    val userIdProof: String?


)

/** quiz request model **/
data class AnswerSendRequest(
    val userQuizAnswer: List<UserQuizAnswer?>?
)

data class UserQuizAnswer(
    val quizId: String?,
    val answer: String?,

    )

/** get notification response **/
@Parcelize
data class GetNotificationResponse(
    val `data`: List<NotificationData?>?, val pagination: Pagination?, val success: Boolean?
): Parcelable
@Parcelize
data class NotificationData(
    val __v: Int?,
    val _id: String?,
    val body: String?,
    val createdAt: String?,
    val isRead: Boolean?,
    val likeId: LikeId?,
    val listingId: String?,
    val senderId: SenderId?,
    val title: String?,
    val type: String?,
    val userId: String?
): Parcelable
@Parcelize
data class LikeId(
    val __v: Int?,
    val _id: String?,
    val createdAt: String?,
    val profileId: String?,
    val status: Int?,
    val type: String?,
    val userId: String?
): Parcelable
@Parcelize
data class SenderId(
    val _id: String?, val name: String?, val profileImage: String?
): Parcelable

/** roommates request  model **/
data class RoomMateModelItem(
    val gender: String?, val age: String?

)


/** post listing  response **/
data class PostListingResponse(
    val `data`: PostListingData?, val message: String?, val success: Boolean?
)

data class PostListingData(
    val _id: String?,
    val address: String?,
    val airConditioner: Boolean?,
    val availableFrom: String?,
    val balcony: Boolean?,
    val cleanliness: Int?,
    val contractLength: String?,
    val createdAt: String?,
    val deposit: Int?,
    val description: String?,
    val elevator: Boolean?,
    val floor: String?,
    val furnishingStatus: String?,
    val heating: String?,
    val images: List<String?>?,
    val isDeleted: Boolean?,
    val kitchen: Boolean?,
    val latitude: Double?,
    val likeCount: Int?,
    val listingType: String?,
    val location: PostListingLocation?,
    val longitude: Double?,
    val lookingFor: List<String?>?,
    val minimumStay: String?,
    val parking: Boolean?,
    val pets: Boolean?,
    val price: Int?,
    val privateBathroom: Boolean?,
    val roomType: String?,
    val roommates: List<PostListingRoommate?>?,
    val size: String?,
    val smoke: String?,
    val status: Int?,
    val title: String?,
    val updatedAt: String?,
    val userId: PostListingUserId?,
    val utilitiesPrice: Int?,
    val videos: String?,
    val viewCount: Int?,
    val washingMachine: Boolean?,
    val wifi: Boolean?
)


data class PostListingLocation(
    val coordinates: List<Double?>?,
    val type: String?
)

data class PostListingRoommate(
    val _id: String?,
    val age: Int?,
    val gender: String?
)

data class PostListingUserId(
    val _id: String?,
    val name: String?,
    val profileImage: String?
)













/** get listing response **/
@Parcelize
data class GetListingResponse(
    val `data`: List<GetListingData?>?,
    val message: String?,
    val pagination: Pagination?,
    val success: Boolean?,
    val unReadNotifications: Int?
):Parcelable

@Parcelize
data class GetListingData(
    val _id: String?,
    val address: String?,
    val airConditioner: Boolean?,
    val availableFrom: String?,
    val balcony: Boolean?,
    val cleanliness: Int?,
    val contractLength: String?,
    val createdAt: String?,
    val deposit: Int?,
    val description: String?,
    val elevator: Boolean?,
    val floor: String?,
    val furnishingStatus: String?,
    val heating: String?,
    val images: List<String?>?,
    val isDeleted: Boolean?,
    val kitchen: Boolean?,
    val latitude: Double?,
    val likeCount: Int?,
    val listingType: String?,
    val location: Location?,
    val longitude: Double?,
    val lookingFor: List<String?>?,
    val minimumStay: String?,
    val parking: Boolean?,
    val pets: Boolean?,
    val price: Int?,
    val privateBathroom: Boolean?,
    val roomType: String?,
    val roommates: List<Roommate?>?,
    val size: String?,
    val smoke: String?,
    val status: Int?,
    val title: String?,
    val updatedAt: String?,
    val userId: String?,
    val utilitiesPrice: Int?,
    val videos: String?,
    val viewCount: Int?,
    val washingMachine: Boolean?,
    val wifi: Boolean?,
    var check : Boolean = false,
    val lsitingCreatedUser: LisitingCreatedUser?,


):Parcelable

@Parcelize
data class LisitingCreatedUser(
    val _id: String?,
    val name: String?,
    val profileImage: String?
):Parcelable


/** id update response **/
data class UserIdUpdateResponse(
    val `data`: UserIdUpdateData?,
    val message: String?,
    val success: Boolean?
)

data class UserIdUpdateData(
    val _id: String?,
    val ageRange: String?,
    val bio: String?,
    val campus: String?,
    val email: String?,
    val gender: String?,
    val instagram: String?,
    val isProfileComplete: Boolean?,
    val isQuizComplete: Boolean?,
    val isVerified: Boolean?,
    val language: String?,
    val latitute: Double?,
    val linkedin: String?,
    val longitude: Double?,
    val name: String?,
    val onwershipProof: String?,
    val profession: String?,
    val profileImage: String?,
    val profileRole: Int?,
    val providerType: String?,
    val timezone: String?,
    val token: String?,
    val userIdProof: String?
)

/** get visit response **/
data class GetVisitResponse(
    val `data`: List<VisitData?>?,
    val message: String?,
    val success: Boolean?
)

data class VisitData(
    val __v: Int?,
    val _id: String?,
    val createdAt: String?,
    val date: String?,
    val listingId: Any?,
    val note: String?,
    val schedulerId: SchedulerId?,
    val status: Int?,
    val time: String?,
    val updatedAt: String?,
    val visitorId: VisitorId?
)

data class SchedulerId(
    val _id: String?,
    val name: String?,
    val profileImage: String?
)

data class VisitorId(
    val _id: String?,
    val name: String?,
    val profileImage: String?
)

/** Get Match By Id response **/
@Parcelize
data class GetMatchByIdResponse(
    val `data`: List<GetMatchData?>?,
    val message: String?,
    val pagination: GetMatchPagination?,
    val success: Boolean?
): Parcelable
@Parcelize
data class GetMatchData(
    val __v: Int?,
    val _id: String?,
    val createdAt: String?,
    val listingId: GetMatchListingId?,
    val profileId: String?,
    val status: Int?,
    val type: String?,
    val userId: GetMatchUserId?
): Parcelable
@Parcelize
data class  GetMatchPagination(
    val limit: Int?,
    val page: Int?,
    val total: Int?,
    val totalPages: Int?
): Parcelable
@Parcelize
data class GetMatchListingId(
    val _id: String?,
    val address: String?,
    val description: String?,
    val images: List<String?>?,
    val latitude: Int?,
    val likeCount: Int?,
    val longitude: Int?,
    val pets: Boolean?,
    val price: Int?,
    val roomType: String?,
    val roommates: List<Roommate?>?,
    val smoke: String?,
    val title: String?,
    val videos: String?
): Parcelable
@Parcelize
data class GetMatchUserId(
    val _id: String?,
    val email: String?,
    val likeCount: Int?,
    val name: String?,
    val profileImage: String?



): Parcelable

