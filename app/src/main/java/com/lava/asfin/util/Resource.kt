package com.lava.asfin.util

sealed class Resource<out T> {
    data class Success<out T>(val value: T): Resource<T>()
    data class Failure(val isNetworkError: Boolean,
                       val errorCode: Int,
                       val throwable: Throwable): Resource<Nothing>()
    object Loading: Resource<Nothing>()
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$value]"
            is Failure -> "Error[message=${throwable.message}, cause=${throwable.cause}]"
            is Loading -> "Loading"
        }
    }
}