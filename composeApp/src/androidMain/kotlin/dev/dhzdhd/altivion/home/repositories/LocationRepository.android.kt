package dev.dhzdhd.altivion.home.repositories

import arrow.core.Either
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.services.Location
import org.koin.core.annotation.Single

@Single
actual class PlatformLocationRepository: LocationRepository {
    actual override suspend fun getLocation(): Either<AppError, Location> {
        TODO("Not yet implemented")
    }
}