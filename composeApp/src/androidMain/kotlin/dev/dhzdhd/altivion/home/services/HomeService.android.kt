package dev.dhzdhd.altivion.home.services

import arrow.core.Either


actual fun getLocation(): Either<Throwable, Location> {
    return Either.Right(Location(1.0, 2.0))
}
