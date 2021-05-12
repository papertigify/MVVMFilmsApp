package com.example.moviefilms.utils

class Resource<T>(val status: Status, val data: T?,  val message: String?) {

    enum class Status {
        SUCCESS, ERROR, INIT
    }

    companion object {
        fun <T> success(data: T): Resource<T> = Resource(Status.SUCCESS, data, null)

        fun <T> error(message: String?): Resource<T> = Resource(Status.ERROR, null, message)

        fun <T> init(): Resource<T> = Resource(Status.INIT, null, null)
    }
}