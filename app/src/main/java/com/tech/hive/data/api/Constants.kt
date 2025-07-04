package com.tech.hive.data.api


object Constants {

    const val BASE_URL = "http://13.51.228.96:8111/api/v1/"
    const val BASE_URL_IMAGE = "http://69.49.232.235/job_app/api/"
    const val GOOGLE_API_KEY = "AIzaSyD5Jt2e9ocVmXovnsOsdmtdhPRkP8m9IhQ"

    var userType = 0
    var userLanguage = "en"

    /**************** API LIST *****************/

    const val HEADER_API = "X-API-Key:lkcMuYllSgc3jsFi1gg896mtbPxIBzYkEL"
    const val USER_LOGIN = "user/login-firebase"
    const val VERIFY_OTP = "user/verify-otp"
    const val RESEND_OTP = "user/send-otp"
    const val USER_ME = "user/me"
    const val LOGOUT = "/api/user/logoutUser"
    const val ACTIVITIES = "/api/user/getMyActivities"
    const val UPDATE_USER_DATA = "/api/user/updateUserData"
    const val GET_USER_DATA = "/api/user/getUserProfile"
    const val GET_ALL_BADGES = "/api/badge/getAllBadges"
    const val GET_ALL_PLANS = "/api/plan/getAllPlans"
    const val GET_PLAN_BY_ID = "/api/plan/getPlanById/"
    const val STAGE_BY_ID = "/api/stage/getStageById/"
    const val GET_ACHIEVEMENTS = "/api/achievement/getMyAchievements"
    const val SAVE_STAGE_HISTORY = "/api/stage/saveStageHistory"
    const val SAVE_RESULTS = "/api/result/saveResults"
    const val SAVE_RESULT_VIDEO = "/api/result/saveResultVideo"



}