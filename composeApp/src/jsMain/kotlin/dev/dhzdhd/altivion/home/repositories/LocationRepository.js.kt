package dev.dhzdhd.altivion.home.repositories

import arrow.core.Either
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.models.Location
import org.koin.core.annotation.Single

@Single
actual class PlatformLocationRepository actual constructor() : LocationRepository {
    actual override suspend fun getLocation(): Either<AppError, Location> =
        Either.Left(AppError.UnimplementedError("Not implemented for web target"))
}