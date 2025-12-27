package dev.dhzdhd.altivion.common

interface Action

interface Store<in T: Action> {
    fun dispatch(action: T)
}

sealed interface AppError {
    val message: String

    data class NetworkError(override val message: String, val error: Throwable): AppError
    data class UnknownError(override val message: String): AppError
}

sealed interface Value<out T> {
    data class Data<out T>(val data: T): Value<T>
    data object Loading: Value<Nothing>
    data class Error(val error: AppError): Value<Nothing>
}
