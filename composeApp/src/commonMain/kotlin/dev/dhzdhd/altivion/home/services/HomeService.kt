package dev.dhzdhd.altivion.home.services

import arrow.core.Either


data class Location(val latitude: Double, val longitude: Double)

expect fun getLocation(): Either<Throwable, Location>
