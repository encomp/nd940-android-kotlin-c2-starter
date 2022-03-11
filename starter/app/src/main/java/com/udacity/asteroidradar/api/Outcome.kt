package com.udacity.asteroidradar.api

data class Outcome<out T>(val status: Status, val data: T?, val error: Exception?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Outcome<T> {
            return Outcome(Status.SUCCESS, data, null, null)
        }

        fun <T> error(message: String, error: Exception?): Outcome<T> {
            return Outcome(Status.ERROR, null, error, message)
        }

        fun <T> loading(data: T? = null): Outcome<T> {
            return Outcome(Status.LOADING, data, null, null)
        }
    }

    override fun toString(): String {
        return "Result(status=$status, data=$data, error=$error, message=$message)"
    }
}