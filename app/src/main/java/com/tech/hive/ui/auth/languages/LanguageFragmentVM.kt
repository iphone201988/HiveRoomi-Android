package com.tech.hive.ui.auth.languages

import com.tech.hive.base.BaseViewModel
import com.tech.hive.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LanguageFragmentVM @Inject constructor(private val apiHelper: ApiHelper): BaseViewModel(){
}