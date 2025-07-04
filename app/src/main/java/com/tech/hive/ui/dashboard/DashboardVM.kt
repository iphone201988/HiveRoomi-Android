package com.tech.hive.ui.dashboard

import com.google.gson.JsonObject
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.event.SingleRequestEvent
import com.tech.hive.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardVM @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {
    val dashBoardObserver = SingleRequestEvent<JsonObject>()
}