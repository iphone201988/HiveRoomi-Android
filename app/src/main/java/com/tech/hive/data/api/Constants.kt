package com.tech.hive.data.api

import com.tech.hive.BuildConfig


object Constants {

    const val BASE_URL = BuildConfig.BASE_URL
    const val BASE_URL_IMAGE = BuildConfig.MEDIA_BASE_URL
    const val SOCKET_URL = BuildConfig.SOCKET_URL
    const val MAP_API_KEY = BuildConfig.MAP_API_KEY
    const val MAP_ID = BuildConfig.MAP_ID
    var quiz = false
    var userLanguage = "en"
    var userId = ""

    /**************** API LIST *****************/

    const val HEADER_API = "X-API-Key:lkcMuYllSgc3jsFi1gg896mtbPxIBzYkEL"
    const val USER_LOGIN = "user/login-firebase"
    const val VERIFY_OTP = "user/verify-otp"
    const val RESEND_OTP = "user/send-otp"
    const val USER_ME = "user/me"
    const val GET_QUIZ = "quiz/get_quiz"
    const val QUIZ_ANSWER = "quiz/user_quiz_answer"
    const val MATCH_LOOKING_ROOMMATE = "match/looking-roommate"
    const val MATCH_USER_ID = "match/user/"
    const val MATCH_LIKE = "match/like-dislike"
    const val MATCH_PENDING_LIKE = "match/pending-like"
    const val MATCH_ACCEPT_REJECT = "match/accept-Reject"
    const val GET_MATCH = "match/all"
    const val MATCH_LOOKING_LISTING = "match/looking-for-listing"
    const val MATCH_LISTING = "match/listing/"
    const val GET_CHAT = "chat/me"
    const val CHAT_MESSAGE = "chat/messages"
    const val USER_UPLOAD = "user/upload"
    const val USER_BLOCK = "user/block"
    const val USER_RATING = "user/rating"
    const val USER_REPORT = "user/report"
    const val USER_FEEDBACK = "user/feedback"
    const val USER_NOTIFICATION = "user/notifications"
    const val USER_FAX = "user/faq"
    const val USER_COMMUNITY_GUIDELINES = "user/community-guidelines"
    const val USER_SAFETY_TIPS = "user/safety-tips"
    const val LISTING = "listing"
    const val LISTING_MATCH = "listing/matchs"
    const val LISTING_CREATE = "listing/create"
    const val LOGOUT = "user/logout"
    const val USER_DELETE_PROFILE = "user/delete-profile"
    const val VISIT_SCHEDULE = "user/visit-schedule"



}