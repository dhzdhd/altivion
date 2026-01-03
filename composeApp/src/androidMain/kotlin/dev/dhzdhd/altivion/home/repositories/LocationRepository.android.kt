package dev.dhzdhd.altivion.home.repositories

import arrow.core.Either
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.models.Location
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.mobile
import org.koin.core.annotation.Single

@Single
actual class PlatformLocationRepository actual constructor() : LocationRepository {
  actual override suspend fun getLocation(): Either<AppError, Location> {
    val locationResult: GeolocatorResult = Geolocator.mobile().current()

    return when (locationResult) {
      is GeolocatorResult.Success ->
          Either.Right(
              Location(
                  locationResult.data.coordinates.latitude,
                  locationResult.data.coordinates.longitude))
      is GeolocatorResult.PermissionDenied ->
          Either.Left(AppError.PermissionError("Location permissions not found"))
      else -> Either.Left(AppError.UnknownError("Failed to fetch location"))
    }
  }
}
