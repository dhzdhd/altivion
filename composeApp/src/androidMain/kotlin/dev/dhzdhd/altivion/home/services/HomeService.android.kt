package dev.dhzdhd.altivion.home.services

import arrow.core.Either
import dev.dhzdhd.altivion.common.AppError


actual fun getLocation(): Either<AppError, Location> {
    return Either.Right(Location(1.0, 2.0))
}
