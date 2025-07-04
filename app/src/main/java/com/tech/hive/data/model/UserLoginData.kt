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
