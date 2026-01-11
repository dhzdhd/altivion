package dev.dhzdhd.altivion.home.services

import arrow.core.Either
import arrow.core.Option
import arrow.core.handleErrorWith
import dev.dhzdhd.altivion.common.AppError
import dev.dhzdhd.altivion.home.models.Airplane
import dev.dhzdhd.altivion.home.models.Location
import dev.dhzdhd.altivion.home.models.RouteAndAirline
import dev.dhzdhd.altivion.home.repositories.ADSBDBRouteApi
import dev.dhzdhd.altivion.home.repositories.AirplaneDTO
import dev.dhzdhd.altivion.home.repositories.AirplaneImage
import dev.dhzdhd.altivion.home.repositories.AirplanesLiveAPI
import dev.dhzdhd.altivion.home.repositories.ImageAPI
import dev.dhzdhd.altivion.home.repositories.PlatformLocationRepository
import dev.dhzdhd.altivion.home.repositories.WebLocationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single
class HomeService(
    private val airplaneApi: AirplanesLiveAPI,
    private val imageApi: ImageAPI,
    private val routeApi: ADSBDBRouteApi,
    private val webLocationRepository: WebLocationRepository,
    private val platformLocationRepository: PlatformLocationRepository
) {
  fun getAirplanes(
      latitude: Double,
      longitude: Double,
      radius: Double
  ): Flow<Either<AppError, List<Airplane>>> {
    return flow {
      while (true) {
        emit(
            airplaneApi.getAirplanesByLatLon(latitude, longitude, radius).map {
              it.aircraft.map(AirplaneDTO::toAirplane)
            })
        delay(10000)
      }
    }
  }

  suspend fun getAirplaneImage(airplane: Airplane): Either<AppError, AirplaneImage> =
      imageApi.getImage(airplane)

  suspend fun getAirplaneRouteAndAirline(
      callsign: Option<String>
  ): Either<AppError, RouteAndAirline> =
      routeApi.getRouteAndAirlineByAirplaneCallsign(callsign).map { it.toRouteAndAirline() }

  suspend fun getLocation(): Either<AppError, Location> {
    return platformLocationRepository.getLocation().handleErrorWith {
      webLocationRepository.getLocation()
    }
  }
}
