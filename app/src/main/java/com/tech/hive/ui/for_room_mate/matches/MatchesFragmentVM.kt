package com.tech.hive.ui.for_room_mate.matches

import com.google.gson.JsonObject
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.event.SingleRequestEvent
import com.tech.hive.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MatchesFragmentVM @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {
    val matchObserver = SingleRequestEvent<JsonObject>()
}