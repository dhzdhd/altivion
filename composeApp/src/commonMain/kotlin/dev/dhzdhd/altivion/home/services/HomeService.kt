package dev.dhzdhd.altivion.home.services

import arrow.core.Either
import dev.dhzdhd.altivion.common.AppError


data class Location(val latitude: Double, val longitude: Double)

expect fun getLocation(): Either<AppError, Location>
