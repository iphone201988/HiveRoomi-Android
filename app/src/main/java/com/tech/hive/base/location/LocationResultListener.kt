package com.tech.hive.base.location

import android.location.Location

interface LocationResultListener {
    fun getLocation(location: Location)
}