package dev.dhzdhd.altivion.common

import arrow.core.Either
import arrow.core.Option
import arrow.core.flatten

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

    companion object {
        fun <T> fromEither(either: Either<AppError, T>): Value<T> {
            return when (either) {
                is Either.Right -> Data(either.value)
                is Either.Left -> Error(either.value)
            }
        }

        fun <T> fromOption(option: Option<T>, error: AppError): Value<T> {
            val either = option.toEither { error }
            return fromEither(either)
        }
    }
}
