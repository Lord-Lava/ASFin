package com.lava.asfin.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return withContext(Dispatchers.IO) {
        try {
            Resource.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            Log.e("SafeApiCall", throwable.message ?: "Error in safeApiCall")
            throwable.printStackTrace()
            when (throwable) {
                is HttpException -> {
                    Resource.Failure(false, throwable.code(), throwable)
                }
                else -> {
                    Resource.Failure(true, -1, throwable)
                }
            }
        }
    }
}