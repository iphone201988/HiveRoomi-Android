
package com.tech.hive.base.network

class NetworkError(val errorCode: Int, override val message: String?) : Throwable(message)
